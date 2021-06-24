// Copyright (C) 2017 John Maraist
// See the LICENSE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
// implied, for NON-COMMERCIAL use.  See the License for the specific
// language governing permissions and limitations under the License.

package org.maraist.util
import java.io.File
import scala.collection.mutable.ArrayBuffer

class FilesCleaner(filenames:String*) {
  val names:ArrayBuffer[String] = collection.mutable.ArrayBuffer(filenames*)
  def clean:Unit = for(filename <- filenames) {
    new File(filename).delete()
  }
  def +=(filename:String):Unit = { names += filename }
  def ++=(filenames:Seq[String]):Unit = ++=(filenames)
}

object FilesCleaner {
  def apply(filenames:String*):FilesCleaner = {
    val result = new FilesCleaner()
    result ++= filenames
    result
  }
}
