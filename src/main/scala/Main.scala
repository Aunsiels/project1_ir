
/**
  * Created by mmgreiner on 09.11.16.
  */

import java.time.{Duration, LocalDateTime}
import java.util.logging.Logger
import scala.util.Try


object Main {

  // set a single line formatter
  //System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s: %5$s%6$s%n")
  System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tT %4$s %2$s: %5$s%6$s%n")

  val log = Logger.getLogger("Classifiers.RunAll")

  val user = System.getProperty("user.name")

  /**
   * A simple class that during development returns the directories of the training and validation files
    * @param path: Directory which contains three directories train, test and validate
    * @param trainDir path to directory containing training zip file. optional
    * @param testDir path to directory containing test zip file. optional
    * @param validateDir path to directory containing validation zip file. optional
   */

  case class Files(val path: String, trainDir: String = "", testDir: String = "", validateDir: String = "") {
    val train = if (trainDir == "") path + "/train" else trainDir
    val test = if (testDir == "") path + "/test" else testDir
    val validate = if (validateDir == "") path + "/validate" else validateDir
  }

  val help = {
    println("Information Retrieval Project 1")
    println
    println("Test 3 Classifiers")
    println("arguments: <directory with train, test, and validate subdirectories> [<maxDocs>]")
    println
  }

  def main(args: Array[String]): Unit = {

    val files =
      if (args.length > 0) Files(args(0))
      else user match {
        case "mmgreiner"  =>  Files("/Users/mmgreiner/Projects/InformationRetrieval/data/score2")
        case "Michael"    => Files("CC:/Users/Michael/Desktop/IR Data/Project 1/allZIPs3/")
        case _            => Files("./zips/")
      }

    val slice = if (args.length > 1) args(1).toInt else -1

    System.gc

    log.info(s"$user, heap-space: ${Timer.freeMB()} of ${Timer.totalMB()}")

    log.info(s"Bayes classifier, reading ${files.train}")

    val bayesClassifier = new BayesClassifier()

    if (slice > 0) {
      val trainings = new RCVStreamSmart(files.train, maxDocs = slice)
      log.info(s"stream length ${trainings.stream.length}")
      var mystream = Stream[RCVParseSmart]()
      mystream = trainings.stream
      log.info("copied to mystream")

      log.info("training")
      bayesClassifier.train(trainings)

      val validates = new RCVStreamSmart(files.validate, maxDocs = slice / 3)
      val classes = bayesClassifier.classify(validates)
      log.info("print classes")
      classes.foreach(x => println(x._1, x._2))
      log.info("completed")

      return
    }

    log.info("classifying and evaluating")
    bayesClassifier.trainAndEvaluate()

  }
}
