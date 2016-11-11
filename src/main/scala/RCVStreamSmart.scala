/**
  * Created by mmgreiner on 02.11.16.
  */

import ch.ethz.dal.tinyir.io.ReutersRCVStream
import java.io.{FileOutputStream, PrintWriter}
import scala.collection.JavaConversions._



/**
  * This class extends the ReutersRCVStream.
  * Together with the class RCVParseSmart, it provides smarter tokenizers. See the documentation for RCVParseSmart for
  * all the parsing options.
  *
  * Contains a main class so that it can be tested.
  *
  * @param path path where the zip file resides
  * @param ext extensions to unzip and consider
  * @param stopWords if true, replace all stop words with <STOP>. Default false
  * @param stemming if true, do Porter stemming. Default false
  */
class RCVStreamSmart(path: String, ext: String = ".xml",
                     stopWords: Boolean = false, stemming: Boolean = false)
  extends ReutersRCVStream(path, ext) {

  override def stream : Stream[RCVParseSmart/*XMLDocument*/] =
    unparsed.stream.map(is => new RCVParseSmart(is, stopWords, stemming))

}


object RCVStreamSmart {


  def testVocabulary(dir: String): Unit = {
    val t = new Timer(heapInfo = true)
    println("heap " + Timer.freeMB())
    val stream = new RCVStreamSmart(dir, stopWords = true, stemming = true)
    val vocabulary = stream.stream.flatMap(x => {
      t.progress(s"${x.ID} ${x.title}")
      x.vocabulary
    })
    println(s"nof distinct words ${vocabulary.size}")
  }


  def main(args: Array[String]): Unit = {
    val dir = "/Users/mmgreiner/Projects/InformationRetrieval/data/score2/train/"

    testVocabulary(dir)
  }
}