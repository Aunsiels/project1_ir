
/**
  * Created by mmgreiner on 09.11.16.
  */

import java.time.{Duration, LocalDateTime}
import java.util.logging.Logger


object RunAll {

  // set a single line formatter
  //System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s: %5$s%6$s%n")
  System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tT %4$s %2$s: %5$s%6$s%n")

  val log = Logger.getLogger("Classifiers.RunAll")

  val user = System.getProperty("user.name")

  /**
   * A simple class that during development returns the directories of the training and validation files
   * @param training path to directory containing training zip file
   * @param validation path to directory containin validation zip file
   */

  case class Files(val training: String, val validation: String)

  val help = {
    println("Information Retrieval Project 1")
    println
    println("Test 3 Classifiers")
    println("arguments: <training-dir> <validation-dir>")
    println
  }

  def main(args: Array[String]): Unit = {

    val files =
      if (args.length > 2) Files(args(1), args(2))
      else user match {
        case "mmgreiner"  =>  Files("/Users/mmgreiner/Projects/InformationRetrieval/data/score2/train",
          "/Users/mmgreiner/Projects/InformationRetrieval/data/score2/validate")
        case _            => Files("C:/Users/Michael/Desktop/IR Data/Project 1/trainingData/50000",
          "C:/Users/Michael/Desktop/IR Data/Project 1/validationSub/10")
      }

    log.info(s"$user, heap-space: ${Timer.freeMB()}")

    val slice = 10000

    log.info(s"Bayes classifier, reading ${files.training}")
    val rcvStreamTraining = new RCVStreamSmart(files.training, stopWords=true, stemming=true, maxDocs=slice)
    log.info("Number of training documents: " + rcvStreamTraining.stream.length)

    val rcvStreamValidation = new RCVStreamSmart(files.validation, stopWords = true, stemming=true, maxDocs=(slice/2))

    log.info("Number of validation documents: " + rcvStreamValidation.stream.length)

    log.info("training Bayes Classifier")
    var bayesClassifier = new BayesClassifier()
    bayesClassifier.train(rcvStreamTraining)
    log.info("...completed")

    log.info("labelling")
    var chosenLabels = bayesClassifier.labelNewDocuments(rcvStreamValidation)
    log.info("...completed")

    log.info("evaluating")
    var evaluator = new Evaluator()
    var trueLabels = rcvStreamValidation.stream.groupBy(_.name).mapValues(c => c.head.codes.toSet)
    evaluator.evaluateTextCategorization(chosenLabels, trueLabels)
    log.info("...completed")

  }
}
