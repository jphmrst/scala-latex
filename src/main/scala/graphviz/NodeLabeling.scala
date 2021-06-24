// Copyright (C) 2017 John Maraist
// See the LICENSE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
// implied, for NON-COMMERCIAL use.  See the License for the specific
// language governing permissions and limitations under the License.


package org.maraist.graphviz

trait NodeLabeling[-S] {
  def getLabel(s:S):String
}
object NodeLabeling {
  given NodeLabeling[Any] with
    def getLabel(s:Any):String = s.toString()

  given NodeLabeling[Set[? <: Any]] with
    def getLabel(ss:Set[? <: Any]):String = {
      val sb = new StringBuilder
      var sep = "{"
      val sublabeler = summon[NodeLabeling[Any]]
      for(s <- ss) {
        sb ++= sep
        sb ++= sublabeler.getLabel(s)
        sep = ", "
      }
      sb += '}'
      sb.toString
    }

  def labelingSetOf[Elem](nl:NodeLabeling[Elem]):NodeLabeling[Set[Elem]] =
    labelingSetOf[Elem]("{", ",", "}", nl)
  def labelingSetOf[Elem](prefix:String, separator:String, suffix:String,
                          nl:NodeLabeling[Elem]):NodeLabeling[Set[Elem]] =
    new NodeLabeling[Set[Elem]] {
      def getLabel(ss:Set[Elem]):String = {
        val sb = new StringBuilder
        var sep = prefix
        for(s <- ss) {
          sb ++= sep
          sb ++= nl.getLabel(s)
          sep = separator
        }
        sb ++= suffix
        sb.toString()
      }
    }
}
