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

open class GraphStyle[S,T](
  // An identifier for this GraphStyle
  var id: String = "",

  // Process-related
  var format: String = "pdf",
  var srcSuffix: String = "dot",
  var executable: String = "dot",

  // Graph properties
  var keepDOT: Boolean = false,
  var fontSize: Int = GraphStyle.defaultFontSize,
  var margin: Double = GraphStyle.defaultMargin,

  // Node properties
  var nodeShape: (S, Graphable[S, T]) => String =
    (s: S, _: Graphable[S, T]) => "circle",
  var nodeLabel: (S, Graphable[S, T]) => String =
    (s: S, _: Graphable[S, T]) => s.toString(),

  // Edge properties
  var edgeLabel: (T, S, S, Graphable[S, T]) => String =
    (t: T, _: S, _: S, _: Graphable[S, T]) => t.toString()) {

  val internalId: String = if id.length == 0 then super.toString() else id

  override def toString(): String = internalId

  def this(opts: GraphStyle[S, T]) = {
    this(
      opts.internalId,
      opts.format, opts.srcSuffix, opts.executable,
      opts.keepDOT, opts.fontSize, opts.margin,
      opts.nodeShape, opts.nodeLabel,
      opts.edgeLabel)
  }
}

object GraphStyle {
  given go[S, T]: GraphStyle[S, T] = new GraphStyle[S, T]("global-default")
  def derivedFrom[S, T](using base: GraphStyle[S, T])(
    id: String = "",
    format: String = base.format,
    srcSuffix: String = base.srcSuffix,
    executable: String = base.executable,
    keepDOT: Boolean = base.keepDOT,
    fontSize: Int = base.fontSize,
    margin: Double = base.margin,
    nodeShape: (S, Graphable[S, T]) => String = base.nodeShape,
    nodeLabel: (S, Graphable[S, T]) => String = base.nodeLabel,
    edgeLabel: (T, S, S, Graphable[S, T]) => String = base.edgeLabel) =
    new GraphStyle[S, T](
      id,
      format, srcSuffix, executable,
      keepDOT, fontSize, margin,
      nodeShape, nodeLabel,
      edgeLabel)

  val defaultFontSize: Int = 12
  val defaultMargin: Double = 0.5
}
