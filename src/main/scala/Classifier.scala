import ch.ethz.dal.tinyir.io.ReutersRCVStream

/**
  * Created by mmgreiner on 11.11.16.
  */
abstract class Classifier(val dir:  String) {

  abstract def train(trainStream: ReutersRCVStream)

  def train(trainDir: String) = train(new RCVStreamSmart(trainDir, stopWords = true, stemming = true))

  def train() = train(dir + "/train")


  abstract def classify(testStream: ReutersRCVStream) : Map[String, Set[String]]

  def classify(testDir: String): Map[String, Set[String]] = classify(new RCVStreamSmart(testDir, stopWords = true, stemming = true))

  def classify(): Map[String, Set[String]] = classify(dir + "/test")


  def evaluate(validateStream: ReutersRCVStream) = {

    val classifiedCodes = classify(validateStream)
    val trueCodes =  validateStream.stream.groupBy(_.name).mapValues(c => c.head.codes)
    val evaluator = new Evaluator()
    evaluator.evaluateTextCategorization(classifiedCodes, trueCodes)
    val stat =  Evaluation.getStat(classifiedCodes.map(doc => doc._2), trueCodes.map(doc => doc._2), 1.0)
    println("Evaluation Test : " + stat)
  }

  def evaluate(validateDir: String) = classify(new RCVStreamSmart(validateDir, stopWords = true, stemming = true)))

  def evaluate() = evaluate(dir + "/validation")



  def trainAndEvaluate() = {
    train()
    evaluate()
  }

}
