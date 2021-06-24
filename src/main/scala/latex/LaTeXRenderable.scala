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

/** Objects which are convertable to LaTeX via a
 * {@link org.maraist.latex.LaTeXdoc LaTeXdoc} instance.
 */
trait LaTeXRenderable {
  /** Write this object to the given
   * {@link org.maraist.latex.LaTeXdoc LaTeXdoc}.
   */
  def toLaTeX(doc:LaTeXdoc): Unit
}

/** Trait of factories which convert objects to a LaTeX representation.
 */
trait LaTeXRenderer[X] {
  /** Write an object to the given
   * {@link org.maraist.latex.LaTeXdoc LaTeXdoc}.
   */
  def toLaTeX(doc:LaTeXdoc, x:X): Unit
}

/** Some typical {@link org.maraist.latex.LaTeXRenderable LaTeXRenderable}
 *  instances.
 */
object LaTeXRenderables {
  val nothing: LaTeXRenderable = new LaTeXRenderable {
    def toLaTeX(doc:LaTeXdoc): Unit = { }
  }

  private val singleCenteredColumnBegin:String = "\\begin{tabular}[c]{@{}c@{}}"
  private val singleCenteredColumnEnd:String = "\\end{tabular}"

  def iterableVerticalTable[A<:LaTeXRenderable](items:Iterable[A],
                                                doc:LaTeXdoc): Unit = {
    doc ++= singleCenteredColumnBegin
    var sep = ""
    for(item <- items) {
      doc ++= sep
      item.toLaTeX(doc)
      sep = LaTeX.linebreak
    }
    doc ++= singleCenteredColumnEnd
  }
  def iterableVerticalTableOrSolo[A<:LaTeXRenderable](items:Iterable[A],
                                                      doc:LaTeXdoc): Unit = {
    items.size match {
      case 0 => { }
      case 1 => items.iterator.next().toLaTeX(doc)
      case n:Int => {
        doc ++= singleCenteredColumnBegin
        var sep = ""
        for(item <- items) {
          doc ++= sep
          item.toLaTeX(doc)
          sep = "\\\\ "
        }
        doc ++= singleCenteredColumnEnd
      }
    }
  }
  def iterableVerticalTable[A](items:Iterable[A],doc:LaTeXdoc,
                               helper:(A,LaTeXdoc)=>Unit): Unit = {
    if (items.isEmpty) {
      doc ++= "---"
    } else {
      doc ++= singleCenteredColumnBegin
      var sep = ""
      for(item <- items) {
        doc ++= sep
        helper(item,doc)
        sep = "\\\\ "
      }
      doc ++= singleCenteredColumnEnd
    }
  }
}
