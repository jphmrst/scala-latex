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
import java.io.{File, FileWriter, BufferedWriter}
import org.maraist.util.FilesCleaner
import org.maraist.graphviz.
  {Graphable, GraphvizOptions, NodeLabeling, TransitionLabeling}

/** Accumulation of LaTeX source, to be processed when closed.
  * @param rootFile Root name of the output file (without `.tex`,
  * `.pdf` etc. suffixes)
  */
class LaTeXdoc(var rootFile: String) {
  /** Title of the document */
  var title: String=""
  /** Author(s) of the document */
  var author: String=""
  /** Date of the document (or \today if empty) */
  var date: String=""
  /** Number of passes needed to latex the document */
  var passes: Int=1
  var bw: BufferedWriter = new BufferedWriter(new FileWriter("/dev/null"))
  private val packageSpecs = new ListBuffer[PackageSpec]()
  private val preamble = new ListBuffer[String]()
  private var opened=false;
  private def cleaner = new FilesCleaner

  var classOptions: Option[String] = None

  private trait DocState(val phase: String, val isOpen: Boolean) {
    def addPreamble(s: String): Unit =
      throw new IllegalStateException(
        "Cannot call addPreamble " + phase)
    def setClass(p: String): Unit =
      throw new IllegalStateException(
        "Cannot call setClass " + phase)
    def setClassOptions(p: String): Unit =
      throw new IllegalStateException(
        "Cannot call setClassOptions " + phase)
    def addPackage(p: String): Unit =
      throw new IllegalStateException(
        "Cannot call addPackage " + phase)
    def addPackage(p: String, opts: String): Unit =
      throw new IllegalStateException(
        "Cannot call addPackage " + phase)
    def open(): Unit =
      throw new IllegalStateException("Cannot call open " + phase)
    def +=(c: Char): Unit =
      throw new IllegalStateException("Cannot call += " + phase)
    def ++=(s: String): Unit =
      throw new IllegalStateException("Cannot call ++= " + phase)
    def ++=/(s: String): Unit =
      throw new IllegalStateException("Cannot call ++=/ " + phase)
    def ++=*(s: String): Unit =
      throw new IllegalStateException("Cannot call ++=* " + phase)
    def ++=*/(s: String): Unit =
      throw new IllegalStateException("Cannot call ++=*/ " + phase)
    def close(): Unit =
      throw new IllegalStateException("Cannot call close " + phase)
    def graphable[X,Y](what: Graphable[X,Y], tag: String, width: String
    )(using
      nodeLabeling: NodeLabeling[X],
      transitionLabeling: TransitionLabeling[Y],
      options: GraphvizOptions
    ):
        Unit =
      throw new IllegalStateException("Cannot call graphable " + phase)
  }

  private var docState: DocState = new BeforeOpen

  private var docClass: String = "article"

  private class BeforeOpen
      extends DocState("before opening document", false) {
    override def setClass(c: String): Unit = {
      docClass = c
    }
    override def setClassOptions(p: String): Unit = {
      classOptions = Some(p)
    }
    override def addPreamble(s: String): Unit = { preamble += s }
    override def addPackage(p: String): Unit = {
      packageSpecs += new PackageSpec(p,None)
    }
    override def addPackage(p: String, opts: String): Unit = {
      packageSpecs += new PackageSpec(p,Some(opts))
    }
    override def open(): Unit = {
      docState = new Open

      val file = new File(rootFile + ".tex")
      bw = new BufferedWriter(new FileWriter(file))
      bw.write("\\documentclass")
      writeClassOptions(bw)
      bw.write("{")
      bw.write(docClass)
      bw.write("}\n")
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
  }

  private class Open extends DocState("after opening document", true) {

    override def ++=(s: String): Unit = {
      bw.write(s)
      bw.flush()
    }

    override def +=(c: Char): Unit = {
      bw.write(c)
      bw.flush()
    }

    override def ++=/(s: String): Unit = ++=(s + "\n")

    override def ++=*(s: String): Unit =
      ++= (s
        .replace("&", "\\&")
        .replace("#", "\\#")
        .replace("%", "\\%")
        .replace(". ", ".\\ "))

    override def ++=*/(s: String): Unit = ++=*(s + "\n")

    override def close(): Unit = {
      docState = new Closed
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

    override def graphable[X,Y](
      what: Graphable[X,Y], tag: String, width: String
    )(using
      nodeLabeling: NodeLabeling[X],
      transitionLabeling: TransitionLabeling[Y],
      options: GraphvizOptions
    ):
        Unit = {
      // println(" - In LaTeXdoc.graphable")
      what.graphviz(tag)
      cleaner += (tag + ".pdf")
      this ++= "\\includegraphics[width="
      this ++= width
      this ++= "]{"
      this ++= tag
      this ++= ".pdf}"
    }
  }

  private class Closed extends DocState("on closed document", false)

  protected def writeClassOptions(bw: BufferedWriter): Unit =
    classOptions match {
      case None => { }
      case Some(s) => {
        bw.write("[")
        bw.write(s)
        bw.write("]")
      }
    }

  private var serial: Int = 0
  protected def getSerial: Int = {
    val result = serial
    serial += 1
    result
  }

  protected def postclean(filename: String): Unit = {
    cleaner += filename
  }

  // -----------------------------------------------------------------
  // The remaining methods delegate to the docState.
  // -----------------------------------------------------------------

  /** Returns true after the document is opened for writing the body,
    * but before it is closed.
    */
  def isOpen: Boolean = docState.isOpen

  /** Set the document class of this document.
    */
  def setClass(c: String): Unit = docState.setClass(c)

  /** Set the document class options of this document.
    */
  def setClassOptions(p: String): Unit = docState.setClassOptions(p)

  /** Add to the preamble. */
  def addPreamble(s: String): Unit = docState.addPreamble(s)

  /** Add a package. */
  def addPackage(p: String): Unit = docState.addPackage(p)

  /** Add a package, with options. */
  def addPackage(p: String, opts: String): Unit =
    docState.addPackage(p, opts)

  /** Conclude prelude operations, and open the document for writing
    * contents.
    */
  def open(): Unit = docState.open()

  /** Add LaTeX source to the body. */
  def ++=(s: String): Unit = docState.++=(s)

  /** Add character to the body. */
  def +=(c: Char): Unit = docState.+=(c)

  /** Add text to the body, encoding it as LaTeX. */
  def ++=*(s: String): Unit = docState.++=*(s)

  /** Add LaTeX source to the body, following it with a carriage return
    * in the source.
    */
  def ++=/(s: String): Unit = docState.++=/(s)

  /** Add text to the body, encoding it as LaTeX, and following it with
    * a carriage return in the source.
    */
  def ++=*/(s: String): Unit = docState.++=*/(s)

  /** Finish receiving body text, and run LaTeX on the constructed
   *  document.
   */
  def close(): Unit = docState.close()

  /** Render an object which can be depicted via Graphviz.
    */
  def graphable[X,Y](what: Graphable[X,Y], tag: String, width: String)(using
    nodeLabeling: NodeLabeling[X],
    transitionLabeling: TransitionLabeling[Y],
    options: GraphvizOptions
  ): Unit = docState.graphable(what, tag, width)
}

/** Description of the LaTeX package associated with a document.
  * @param name The name of the package.
  * @param options A list of options to be passed to the package.
  */
class PackageSpec(name: String, options: Option[String]) {
  def render(bw: BufferedWriter): Unit = {
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
