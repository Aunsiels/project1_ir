
/**
  * Created by mmgreiner on 09.11.16.
  */

import java.time.{Duration, LocalDateTime}
import java.util.logging.Logger


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
    println("arguments: <directory with train, test, and validate subdirectories>")
    println
  }

  def main(args: Array[String]): Unit = {

    val files =
      if (args.length > 2) Files(args(1), args(2))
      else user match {
        case "mmgreiner"  =>  Files("/Users/mmgreiner/Projects/InformationRetrieval/data/score2")
        case "Michael"    => Files("CC:/Users/Michael/Desktop/IR Data/Project 1/allZIPs3/")
        case _            => Files("./zips/")
      }

    System.gc

    log.info(s"$user, heap-space: ${Timer.freeMB()} of ${Timer.totalMB()}")

    val slice = 10000

    log.info(s"Bayes classifier, reading ${files.train}")

    log.info("dummy")
    val dummyClassifier = new DummyClassifier(files.path)
    dummyClassifier.train()

    log.info("Naive Bayes Classifier")
    val bayesClassifier = new BayesClassifier()
    log.info("training")
    bayesClassifier.train(new RCVStreamSmart(files.test))

    log.info("classifying and evaluating")
    //bayesClassifier.trainAndEvaluate()

  }
}
