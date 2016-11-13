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
                     stopWords: Boolean = false, stemming: Boolean = false, maxDocs: Int = Int.MaxValue)
  extends ReutersRCVStream(path, ext) {

  override def stream : Stream[RCVParseSmart/*XMLDocument*/] =
    unparsed.stream.slice(0, maxDocs).map(is => new RCVParseSmart(is, stopWords, stemming))

}


object RCVStreamSmart {


  def testVocabulary(dir: String, stopWords: Boolean, stemming: Boolean): Unit = {
    val t = new Timer(heapInfo = true)
    println(s"heap ${Timer.freeMB()}, stopwords=$stopWords, stemming=$stemming")
    val stream = new RCVStreamSmart(dir, stopWords = stopWords, stemming = stemming)
    var vocabularyAll = collection.mutable.Set[String]()
    var totalTokens = 0
    var rawVocabulary = collection.mutable.Set[String]()

    val vocabulary = stream.stream.foreach(x => {
      vocabularyAll = vocabularyAll union x.vocabulary
      totalTokens += x.tokens.size
      t.progress(s"${x.title}, ${vocabularyAll.size}")
    })
    println(s"nof distinct words ${vocabularyAll.size}, totalTokens ${totalTokens}")
  }


  def main(args: Array[String]): Unit = {
    val dir = "/Users/mmgreiner/Projects/InformationRetrieval/data/score2/train/"

    testVocabulary(dir, false, false)
    System.gc
    testVocabulary(dir, true, false)
    System.gc
    testVocabulary(dir, true, true)
  }
}