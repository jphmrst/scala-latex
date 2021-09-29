// Copyright (C) 2017, 2021 John Maraist
// See the LICENSE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
// implied, for NON-COMMERCIAL use.  See the License for the specific
// language governing permissions and limitations under the License.

package org.maraist.graphviz

class GraphStyle[S,T](
  // Process-related
  var format: String = "pdf",
  var srcSuffix: String = "dot",
  var executable: String = "dot",

  // Graph properties
  var keepDOT: Boolean = false,
  var fontSize: Int = GraphStyle.defaultFontSize,
  var margin: Double = GraphStyle.defaultMargin,

  // Node properties
  var nodeShape: String = "circle",
  var finalNodeShape: String = "doublecircle",
  var nodeLabel: (S, Graphable[S, T]) => String =
    (s: S, _: Graphable[S, T]) => s.toString(),

  // Edge properties
  var edgeLabel: (T, S, S, Graphable[S, T]) => String =
    (t: T, _: S, _: S, _: Graphable[S, T]) => t.toString()) {

  def this(opts: GraphStyle[S, T]) = {
    this(
      opts.format, opts.srcSuffix, opts.executable, opts.keepDOT,
      opts.fontSize, opts.margin, opts.nodeShape, opts.finalNodeShape)
  }
}

object GraphStyle {
  given go[S, T]: GraphStyle[S, T] = new GraphStyle[S, T]()
  def derivedFrom[S, T](using base: GraphStyle[S, T])(
    format: String = base.format,
    srcSuffix: String = base.srcSuffix,
    executable: String = base.executable,
    keepDOT: Boolean = base.keepDOT,
    fontSize: Int = base.fontSize,
    margin: Double = base.margin,
    nodeShape: String = base.nodeShape,
    finalNodeShape: String = base.finalNodeShape,
    nodeLabel: (S, Graphable[S, T]) => String = base.nodeLabel,
    edgeLabel: (T, S, S, Graphable[S, T]) => String = base.edgeLabel) =
    new GraphStyle[S, T](
      format, srcSuffix, executable, keepDOT, fontSize, margin, nodeShape,
      finalNodeShape, nodeLabel, edgeLabel)

  val defaultFontSize: Int = 12
  val defaultMargin: Double = 0.5
}
