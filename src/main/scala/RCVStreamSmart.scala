/**
  * Created by mmgreiner on 02.11.16.
  */

import ch.ethz.dal.tinyir.processing.XMLDocument
import ch.ethz.dal.tinyir.io.ReutersRCVStream

class RCVStreamSmart(path: String, ext: String = ".xml", stopWords: Boolean = false, stemming: Boolean = false) extends ReutersRCVStream(path, ext) {

  override def stream : Stream[RCVParseSmart/*XMLDocument*/] = unparsed.stream.map(is => new RCVParseSmart(is, stopWords, stemming))

}

object RCVStreamSmart {
  def main(args: Array[String]): Unit = {
    val dir = "/Users/mmgreiner/Projects/InformationRetrieval/data/score2/train/"
    val slice = 8000

    var t = System.nanoTime
    val smartStream = new RCVStreamSmart(dir, stopWords = true, stemming=true)
    val docs1 = smartStream.stream.slice(0, slice)
    val tokens1 = docs1.flatMap(x => x.tokens)
    val rawtok1 = docs1.flatMap(x => x.raw_tokens)
    val vocab1 = tokens1.toSet
    val vocab2 = rawtok1.toSet
    println(s"time smart: ${(System.nanoTime - t).toDouble / 1000000000.0}")
    println(s" tokens nof ${tokens1.size}, vocab set ${vocab1.size}")
    println(s" raw    nof ${rawtok1.size}, vocab set ${vocab2.size}")
    println("done")



  }
}