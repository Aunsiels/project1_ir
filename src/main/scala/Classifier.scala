import ch.ethz.dal.tinyir.io.ReutersRCVStream

/**
  * Created by mmgreiner on 11.11.16.
  */
abstract class Classifier(val dir:  String = "") {

  def train(trainStream: ReutersRCVStream): Unit = throw new Exception("train not implemented")

  def train(trainDir: String): Unit = train(new RCVStreamSmart(trainDir, stopWords = true, stemming = true))

  def train(): Unit = train(dir + "/train")


  def classify(testStream: ReutersRCVStream) : Map[String, Set[String]] = throw new Exception("classify not implemented")

  def classify(testDir: String): Map[String, Set[String]] = classify(new RCVStreamSmart(testDir, stopWords = true, stemming = true))

  def classify(): Map[String, Set[String]] = classify(dir + "/test")


  def evaluate(validateStream: ReutersRCVStream): Unit = {

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
