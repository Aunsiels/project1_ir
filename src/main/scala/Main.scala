
/**
  * Created by mmgreiner on 09.11.16.
  */

import java.io.FileOutputStream
import java.time.{Duration, LocalDateTime}
import java.util.logging.Logger
import scala.util.matching.Regex

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
    val validate = if (validateDir == "") path + "/validation" else validateDir
  }

  val help = {
    println("Information Retrieval Project 1 Group 11")
    println
    println("Test 3 Classifiers")
    println("arguments: <data-directory> [ITERATIONS=<int>] [LEARNING=<double>] [TEST=<int>] [SKIP=BAYES,LINREG,SVM]")
    println
  }

  def testReducedBayesOnly(files: Files, slice: Int) = {
    val trainings = new RCVStreamSmart(files.train, maxDocs = slice)
    log.info(s"stream length ${trainings.stream.length}")
    var mystream = Stream[RCVParseSmart]()
    mystream = trainings.stream
    log.info("copied to mystream")

    log.info("training")
    val bayesClassifier = new BayesClassifier()
    bayesClassifier.train(trainings)

    val validates = new RCVStreamSmart(files.validate, maxDocs = slice / 3)
    val classes = bayesClassifier.classify(validates)
    log.info("print classes")
    classes.foreach(x => println(x._1, x._2))
    log.info("completed")
  }

  def WriteToFile(result: Map[String, Set[String]], name: String) = {
    // ir-project-2016-1-[groupid]-[nb|lr|lsvm].txt
    val fname = s"ir-project-2016-1-11-$name.txt"
    val f = new java.io.PrintWriter(new FileOutputStream(fname))
    log.info(s"Writing $fname")

    result.toSeq.sortBy(_._1).foreach(x => {
      val labels = x._2.mkString(" ")
      f.println(s"${x._1} $labels")
    })
    f.close()
  }

  def main(args: Array[String]): Unit = {

    val files =
      if (args.length > 0) Files(args(0))
      else user match {
        case "mmgreiner"  =>  Files("/Users/mmgreiner/Projects/InformationRetrieval/data/score4")
        case "Michael"    => Files("CC:/Users/Michael/Desktop/IR Data/Project 1/allZIPs3/")
        case _            => Files("./zips/")   // Julien
      }

    var slice = 0

    // default values
    var iterations = 10000
    var learningRate = 0.001
    var skipBayes = false
    var skipLR = false
    var skipSVM = false

    // command line parameter patterns
    val iterOption = """I.*=\d+""".r
    val learnOption = """L.*=\d+\.\d+""".r
    val testOption = """T.*=\d+""".r
    val skipBayesOption = """S.*=.*BAYES.*""".r
    val skipLROption = """S.*=.*LINREG.*""".r
    val skipSVMOption = """S.*=.*SVM.*""".r

    if (args.length > 1) {
      args.tail.foreach(a => {
        val e = a.indexOf("=")
        a.toUpperCase() match {
          case iterOption(_*) => iterations = a.slice(e + 1, 100).toInt
          case learnOption(_*) => learningRate = a.slice(e + 1, 100).toDouble
          case testOption(_*) => slice = a.slice(e + 1, 100).toInt
          case skipBayesOption(_*) => skipBayes = true
          case skipLROption(_*) => skipLR = true
          case skipSVMOption(_*) => skipSVM = true
          case _ => println(s"argument $a not recognized")
        }
      })
    }

    System.gc

    log.info(s"$user, heap-space: ${Timer.freeMB()} of ${Timer.totalMB()}")
    log.info(s"data-folder=${files.path}, iterations=$iterations, learning rate=$learningRate, skip Bayes=$skipBayes" +
      s" skip Linear Regression=$skipLR, skip SVM=$skipSVM")


    // testing only
    if (slice > 0) {
      testReducedBayesOnly(files, slice)
      return
    }

    if (! skipBayes) {
      log.info(s"Bayes classifier, reading ${files.train}")
      val bayesClassifier = new BayesClassifier(files.path)
      log.info("classifying and evaluating")
      val bayesResult = bayesClassifier.trainEvaluateClassify()
      WriteToFile(bayesResult, "nb")
      log.info("completed Bayes")
    }

    if (! skipLR) {
      log.info(s"Logistic Regression, reading ${files.train}")
      val logreg = new MainLogRes(files.path, nIterations = iterations, learningRate = learningRate)
      val logResult = logreg.trainEvaluateClassify()
      WriteToFile(logResult, "lr")
      log.info("completed Logistic Regression")
    }

    if (! skipSVM) {
      log.info(s"SVM classifier, reading ${files.train}")
      val svmClassifier = new SVMMain(files.path)
      val svmResults = svmClassifier.trainEvaluateClassify()
      WriteToFile(svmResults, "lsvm")
      log.info("completed SVM")
    }

  }
}
