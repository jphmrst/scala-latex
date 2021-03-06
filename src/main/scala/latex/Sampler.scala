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
import org.maraist.util.FilesCleaner
import scala.collection.mutable.ArrayBuffer
import org.maraist.graphviz.{Graphable, GraphStyle}

/** Methods for a samples manual builder.
 */
trait Sampler {
  def addSamples(guide:LaTeXdoc): FilesCleaner
  protected def newCleaner() = new FilesCleaner

  protected def section(doc:LaTeXdoc, title:String):Unit = {
    doc ++= "\\section{"
    doc ++= title
    doc ++= "}\n"
  }

  protected def subsection(doc:LaTeXdoc, title:String):Unit = {
    doc ++= "\\subsection{"
    doc ++= title
    doc ++= "}\n"
  }

  protected def latexable(doc:LaTeXdoc, what:LaTeXRenderable):Unit = {
    what.toLaTeX(doc)
  }

  protected def latexable(doc:LaTeXdoc, title:String,
                          what:LaTeXRenderable):Unit = {
    subsection(doc, title)
    latexable(doc, what)
  }

  protected def graphable[X, Y, Style[A, B] <: GraphStyle[A, B]](
    doc: LaTeXdoc, cleaner: FilesCleaner, what: Graphable[X, Y, Style],
    tag: String, width: String)(
    using options: Style[X, Y]):
      Unit = {
    // println("  In 5-arg Sampler.graphable with " + options)
    doc.graphable(what, tag, width)
  }

  protected def graphable[X, Y, Style[A, B] <: GraphStyle[A, B]](
    doc: LaTeXdoc, cleaner: FilesCleaner, what: Graphable[X, Y, Style],
    tag: String, title: String, width: String)(
    using options: Style[X, Y]):
      Unit = {
    // println(" In 6-arg Sampler.graphable with " + options)
    subsection(doc, title)
    graphable(doc, cleaner, what, tag, width)
  }
}
