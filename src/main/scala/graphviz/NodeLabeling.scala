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

trait SimpleLabeling[-S] {
  def getLabel(s: S): String
}

trait NodeLabeling[S, T] {
  def getLabel(s: S, graph: Graphable[S, T]): String
}

object NodeLabeling {
  given labelAny[S, T]: NodeLabeling[S, T] with
    def getLabel(s: S, g: Graphable[S, T]):String = s.toString()

  given labelAnySet[S, SS <: Set[S], T](using sl: SimpleLabeling[S]):
      NodeLabeling[SS, T] with
    def getLabel(ss: SS, g: Graphable[SS, T]): String = {
      val sb = new StringBuilder
      var sep = "{"
      for(s <- ss) {
        sb ++= sep
        sb ++= sl.getLabel(s)
        sep = ", "
      }
      sb += '}'
      sb.toString
    }

  def labelingSetOf[Elem,T](nl:SimpleLabeling[Elem]):NodeLabeling[Set[Elem],T] =
    labelingSetOf[Elem,T]("{", ",", "}", nl)
  def labelingSetOf[Elem,T](
    prefix:String, separator:String, suffix:String,
    nl: SimpleLabeling[Elem]):
      NodeLabeling[Set[Elem], T] =
    new NodeLabeling[Set[Elem], T] {
      def getLabel(ss:Set[Elem], g: Graphable[Set[Elem],T]):String = {
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
