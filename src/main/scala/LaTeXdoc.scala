// Copyright (C) 2017 John Maraist
// See the LICENSE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
// implied, for NON-COMMERCIAL use.  See the License for the specific
// language governing permissions and limitations under the License.

package org.maraist.latex
import scala.language.postfixOps
import scala.sys.process.* // scalastyle:ignore
import scala.collection.mutable.ListBuffer
import java.io.File
import java.io.FileWriter
import java.io.BufferedWriter
import org.maraist.util.FilesCleaner
import org.maraist.graphviz.Graphable

/** Accumulation of LaTeX source, to be processed when closed.
 * @param rootFile Root name of the output file (without .tex, .pdf
 * etc. suffixes)
 */
class LaTeXdoc(var rootFile:String) {
  /** Title of the document */
  var title:String=""
  /** Author(s) of the document */
  var author:String=""
  /** Date of the document (or \today if empty) */
  var date:String=""
  /** Number of passes needed to latex the document */
  var passes:Int=1
  var bw:BufferedWriter = new BufferedWriter(new FileWriter("/dev/null"))
  private val packageSpecs = new ListBuffer[PackageSpec]()
  private val preamble = new ListBuffer[String]()
  private var opened=false;
  private def cleaner = new FilesCleaner

  var classOptions:Option[String] = None

  def writeClassOptions(bw:BufferedWriter):Unit = classOptions match {
    case None => { }
    case Some(s) => {
      bw.write("[")
      bw.write(s)
      bw.write("]")
    }
  }

  private var serial:Int = 0
  def getSerial:Int = {
    val result = serial
    serial += 1
    result
  }

  def isOpen:Boolean = opened

  protected def postclean(filename:String):Unit = { cleaner += filename }

  /** Add to the preamble */
  def addPreamble(s:String):Unit = {
    if (opened) {
      throw new RuntimeException
          ("Cannot add preamble material once body is open")
    }
    preamble += s
  }

  /** Add a package */
  def addPackage(p:String):Unit = {
    if (opened) {
      throw new RuntimeException("Cannot add packages once body is open")
    }
    packageSpecs += new PackageSpec(p,None)
  }
  /** Add a package, with options */
  def addPackage(p:String, opts:String):Unit = {
    if (opened)
      throw new RuntimeException("Cannot add packages once body is open")
    packageSpecs += new PackageSpec(p,Some(opts))
  }

  def open():Unit = {
    if (opened) { throw new RuntimeException("Cannot reopen file") }
    opened=true;

    val file = new File(rootFile + ".tex")
    bw = new BufferedWriter(new FileWriter(file))
    bw.write("\\documentclass")
    writeClassOptions(bw)
    bw.write("{article}\n")
    for(packageSpec <- packageSpecs) {
      packageSpec.render(bw)
    }
    for(pre <- preamble) {
      bw.write(pre)
    }

    var makeTitle = false
    if (title.length>0 || author.length>0 || date.length>0)  {
      bw.write("\\title{" + title + "}\n\\author{" + author + "}\n")  // scalastyle:ignore
      if (date.length>0)
        bw.write("\\date{" + date + "}\n")
      else
        bw.write("\\date{\\today}\n")
      makeTitle=true
    }
    bw.write("\\begin{document}\n")
    if (makeTitle) bw.write("\\maketitle\n")
  }

  /** Add text to the body */
  def ++=(s:String):Unit = {
    if (!opened) {
      throw new RuntimeException("Cannot write to unopened file")
    }
    bw.write(s)
    bw.flush()
  }

  /** Add text to the body */
  def ++=*(s:String):Unit = {
    ++= (s.replace("&", "\\&")
          .replace("#", "\\#")
          .replace("%", "\\%")
          .replace(". ", ".\\ "))
  }

  /** Finish receiving body text, and run LaTeX on the constructed
   *  document.
   */
  def close():Unit = {
    if (!opened) { throw new RuntimeException("Cannot close opened file") }
    bw.write("\\end{document}\n")
    bw.close()

    val lastSlash = rootFile.lastIndexOf('/')
    if (lastSlash<0) {
      for(a <- 1 to passes) {
        { Seq("pdflatex",rootFile) !! }
      }
    } else {
      val dir = rootFile.substring(0,lastSlash)
      val bareRootFile = rootFile.substring(1+lastSlash)
      for(a <- 1 to passes) {
        Process("pdflatex "+bareRootFile, new File(dir)).!!
      }
    }
    cleaner.clean
  }

  def graphable[X,Y](what:Graphable[X,Y], tag:String, width:String):Unit = {
    what.graphviz(tag)
    cleaner += (tag + ".pdf")
    this ++= "\\includegraphics[width="
    this ++= width
    this ++= "]{"
    this ++= tag
    this ++= ".pdf}"
  }
}

class PackageSpec(name:String, options:Option[String]) {
  def render(bw:BufferedWriter):Unit = {
    bw.write("\\usepackage")
    options match {
      case Some(s) => {
        bw.write("[")
        bw.write(s)
        bw.write("]")
      }
      case _ => { }
    }
    bw.write("{")
    bw.write(name)
    bw.write("}\n")
  }
}
