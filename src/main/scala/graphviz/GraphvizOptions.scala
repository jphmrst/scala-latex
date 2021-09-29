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

class GraphvizOptions[S,T](
  var format: String = "pdf",
  var srcSuffix: String = "dot",
  var executable: String = "dot",
  var keepDOT: Boolean = false,
  var fontSize: Int = GraphvizOptions.defaultFontSize,
  var margin: Double = GraphvizOptions.defaultMargin,
  var nodeShape: String = "circle",
  var finalNodeShape: String = "doublecircle",
  var getNodeLabel: (S, Graphable[S, T]) => String =
    (s: S, _: Graphable[S, T]) => s.toString(),
  var getEdgeLabel: (T, S, S, Graphable[S, T]) => String =
    (t: T, _: S, _: S, _: Graphable[S, T]) => t.toString()) {

  def this(opts: GraphvizOptions[S, T]) = {
    this(
      opts.format, opts.srcSuffix, opts.executable, opts.keepDOT,
      opts.fontSize, opts.margin, opts.nodeShape, opts.finalNodeShape)
  }
}

object GraphvizOptions {
  given go[S, T]: GraphvizOptions[S, T] = new GraphvizOptions[S, T]()
  def derivedFrom[S, T](using base: GraphvizOptions[S, T])(
    format: String = base.format,
    srcSuffix: String = base.srcSuffix,
    executable: String = base.executable,
    keepDOT: Boolean = base.keepDOT,
    fontSize: Int = base.fontSize,
    margin: Double = base.margin,
    nodeShape: String = base.nodeShape,
    finalNodeShape: String = base.finalNodeShape,
    getNodeLabel: (S, Graphable[S, T]) => String = base.getNodeLabel,
    getEdgeLabel: (T, S, S, Graphable[S, T]) => String = base.getEdgeLabel) =
    new GraphvizOptions[S, T](
      format, srcSuffix, executable, keepDOT, fontSize, margin, nodeShape,
      finalNodeShape, getNodeLabel, getEdgeLabel)

  val defaultFontSize: Int = 12
  val defaultMargin: Double = 0.5
}
