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
  var finalNodeShape: String = "doublecircle") {
  def this(opts: GraphvizOptions[S, T]) = {
    this(
      opts.format, opts.srcSuffix, opts.executable, opts.keepDOT,
      opts.fontSize, opts.margin, opts.nodeShape, opts.finalNodeShape)
  }
}

object GraphvizOptions {
  given go[S, T]: GraphvizOptions[S, T] = new GraphvizOptions[S, T]()
  def makeLocalOptions[S, T](using opts: GraphvizOptions[S, T]) =
    new GraphvizOptions[S, T](opts)
  val defaultFontSize: Int = 12
  val defaultMargin: Double = 0.5
}
