import ch.ethz.dal.tinyir.io.ReutersRCVStream

/**
  * Created by mmgreiner on 11.11.16.
  */
abstract class Classifier(val dir:  String = "") {

  def train(trainStream: RCVStreamSmart): Unit = throw new Exception("train not implemented")

  def train(trainDir: String): Unit = train(new RCVStreamSmart(trainDir, stopWords = true, stemming = true))

  def train(): Unit = train(dir + "/train")


  def classify(testStream: RCVStreamSmart) : Map[String, Set[String]] = throw new Exception("classify not implemented")

  def classify(testDir: String): Map[String, Set[String]] = classify(new RCVStreamSmart(testDir, stopWords = true, stemming = true))

  def classify(): Map[String, Set[String]] = classify(dir + "/test")


  def evaluate(validateStream: RCVStreamSmart): Unit = {

    val classifiedCodes = classify(validateStream)
    val trueCodes =  validateStream.stream.groupBy(_.name).mapValues(c => c.head.codes)
    val evaluator = new Evaluator()
    evaluator.evaluateTextCategorization(classifiedCodes, trueCodes)
    val stat =  Evaluation.getStat(classifiedCodes.map(doc => doc._2), trueCodes.map(doc => doc._2), 1.0)
    println("Evaluation Test : " + stat)
  }

  def evaluate(validateDir: String): Unit = classify(new RCVStreamSmart(validateDir, stopWords = true, stemming = true))

  def evaluate(): Unit = evaluate(dir + "/validate")



  def trainAndEvaluate(): Unit = {
    train()
    evaluate()
  }

}

object Classifier {
  def testVocabulary(dir: String): Unit = {
    val t = new Timer(heapInfo = true)
    println("heap " + Timer.freeMB())
    val stream = new RCVStreamSmart(dir, stopWords = true, stemming = true)
    println(s"stream length ${stream.length}")

    val mystream = stream.stream
    println(s"mystream length ${mystream.length}")

    val vocabulary = mystream.flatMap(x => {
      t.progress(s"${x.ID} ${x.title}")
      x.vocabulary
    })
    println(s"nof distinct words ${vocabulary.size}")
  }

  def main(args: Array[String]): Unit = {
    testVocabulary("/Users/mmgreiner/Projects/InformationRetrieval/data/score2/train")
  }
}
