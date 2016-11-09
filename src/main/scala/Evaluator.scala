class Evaluator() {
  
  def evaluateTextCategorization(chosenLabels : Map[String, Set[String]] , trueLabels : Map[String, Set[String]]) : Double = {
    //var docNames = trueLabels.keySet.zipWithIndex
    //var docF1Scores = docNames.map { case(k,v) => computeF1Score(trueLabels(k), trueLabels(k)) }
    var docNames = trueLabels.keySet.toList   
    //println(docNames)
    var docF1Scores = docNames.map(docName => computeF1Score(chosenLabels(docName), trueLabels(docName)))
    println("scores per document: " + docF1Scores)
    var overallF1Score = docF1Scores.sum / docF1Scores.size
    println("overall score: " + overallF1Score)
    return overallF1Score
  }
  
  def computeF1Score(chosenLabels : Set[String], trueLabels : Set[String]) : Double = {
    println("Chosen labels: " + chosenLabels)
    println("True labels: " + trueLabels)
    var correctLabelsChosen = chosenLabels.intersect(trueLabels).size.toDouble
    var totalLabelsChosen = chosenLabels.size.toDouble
    var totalLabelsGroundTruth = trueLabels.size.toDouble
    var precision = correctLabelsChosen / totalLabelsChosen
    var recall = correctLabelsChosen / totalLabelsGroundTruth
    var beta = 1
    var alpha = 0.5
    //println(precision)
    //println(recall)
    var f1score = 0.0
    if(correctLabelsChosen != 0) {
      f1score = (((beta*beta)+1)*precision*recall) / ((beta*beta*precision)+recall)
    }
    
    /*println(precision)
    println(recall)*/
    //println(f1score)
    return f1score
  }
}
