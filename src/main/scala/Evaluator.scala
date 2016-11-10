class Evaluator() {
  
  var beta = 1
  
  def evaluateTextCategorization(chosenLabels : Map[String, Set[String]] , trueLabels : Map[String, Set[String]]) : Double = {
    //var docNames = trueLabels.keySet.zipWithIndex
    //var docF1Scores = docNames.map { case(k,v) => computeF1Score(trueLabels(k), trueLabels(k)) }
    val docNames = trueLabels.keySet.toList
    //println(docNames)
    val docScores = docNames.map(docName => computeF1Score(chosenLabels(docName), trueLabels(docName)))
    //println("scores per document: " + docScores)
    val docPrecision = docScores.map(score => score(1))
    //println(docPrecision)
    val overallPrecision = docPrecision.sum / docPrecision.size
    println("overall precision: " + overallPrecision)
    val docRecall = docScores.map(score => score(2))
    //println(docRecall)
    val overallRecall = docRecall.sum / docRecall.size
    println("overall recall: " + overallRecall)
    val docF1Scores = docScores.map(score => score(0))
    //println(docF1Scores)
    var overallF1Score = docF1Scores.sum / docF1Scores.size
    println("overall f1 score (1): " + overallF1Score)
    overallF1Score = (((beta*beta)+1)*overallPrecision*overallRecall) / ((beta*beta*overallPrecision)+overallRecall)
    println("overall f1 score (2): " + overallF1Score)
  
    overallF1Score
  }
  
  def computeF1Score(chosenLabels : Set[String], trueLabels : Set[String]) : List[Double] = {
    //println("Chosen labels: " + chosenLabels)
    //println("True labels: " + trueLabels)
    val correctLabelsChosen = chosenLabels.intersect(trueLabels).size.toDouble
    val totalLabelsChosen = chosenLabels.size.toDouble
    val totalLabelsGroundTruth = trueLabels.size.toDouble
    var precision = correctLabelsChosen / totalLabelsChosen
    if(totalLabelsChosen == 0) {
      precision = 0
    }
    var recall = correctLabelsChosen / totalLabelsGroundTruth
    if(totalLabelsGroundTruth == 0) {
      recall = 0
    }
    
    //println(precision)
    //println(recall)
    var f1score = 0.0
    if(correctLabelsChosen != 0) {
      f1score = (((beta*beta)+1)*precision*recall) / ((beta*beta*precision)+recall)
    }
    
    /*println(precision)
    println(recall)*/
    //println(f1score)
    List(f1score, precision, recall)
  }
}
