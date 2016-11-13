/**
  * Created by mmgreiner on 11.11.16.
  */

import scala.util.{Try, Failure, Success}

object TermFrequenciesTest {

  def printStream(stream: RCVStreamSmart): Unit = {
    val t = new Timer(heapInfo = true)
    stream.stream.foreach(doc => t.progress(s"${doc.title}, ${doc.tokens.size}, ${doc.vocabulary.size}"))
  }


  def getTF(stream: RCVStreamSmart): Unit = {
    println("getting highest TF")
    val t = new Timer(heapInfo = true, step = 500)

    var tfAllDocs = collection.mutable.Map[String, Integer]()

    stream.stream.foreach(doc => {
      val tf = doc.tokens.groupBy(identity).mapValues(l => l.length)
      tf.foreach(x => {
        if (tfAllDocs contains x._1)
          tfAllDocs(x._1) += x._2
        else
          tfAllDocs += (x._1 -> x._2)
      })
      t.progress(s"${doc.title}")
    })

    val sorted = tfAllDocs.toSeq.sortBy(_._2).reverse
    val highfreq = tfAllDocs.filter(t => t._2 > 30000)
    println(highfreq.toSeq.sortBy(_._2).reverse)

  }

  def main(args: Array[String]): Unit = {
    System.gc
    Try(new RCVStreamSmart(args(0) + "/train", stopWords = true, stemming = true, maxDocs = args(1).toInt)) match {
      case Success(stream: RCVStreamSmart) => getTF(stream)
      case _ => println("No directory given")
    }
  }
}
