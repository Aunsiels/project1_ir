import ch.ethz.dal.tinyir.processing.XMLDocument

class ConditionalWordProbability(seqOfXMLDocs: Seq[XMLDocument], vocabularySize: Double, category: String) {

    val tokensOfDocsWithCat = seqOfXMLDocs.filter(_.codes(category)).flatMap(_.tokens)
    val denominatorOfDocsWithCat = tokensOfDocsWithCat.size.toDouble + vocabularySize
    val termFrequenciesOfDocsWithCat = tokensOfDocsWithCat.groupBy(identity).map(term => (term._1, term._2.size.toDouble))     
    val tokensOfDocsWithoutCat = seqOfXMLDocs.filterNot(_.codes(category)).flatMap(_.tokens)
    val denominatorOfDocsWithoutCat = tokensOfDocsWithoutCat.size.toDouble + vocabularySize
    val termFrequenciesOfDocsWithoutCat = tokensOfDocsWithoutCat.groupBy(identity).map(term => (term._1, term._2.size.toDouble))
      
    def getPwcOfDocsWithCatForTerm(term : String) : Double = {
      //var sum = docsPerCategory(category).toList.map(docName => (termFrequenciesPerDocument(docName).getOrElse(term, 0))).sum.toDouble + 1.0
      //var denominator = denominatorsPerCategory(category).toDouble
      var sum = termFrequenciesOfDocsWithCat.getOrElse(term, 0.0) + 1
      var denominator = denominatorOfDocsWithCat.toDouble
      //println("p(w|c) for w=" + term + " and c="+category + ": " + sum+"/"+denominator)
      return (sum/denominator)
    }
    
    def getPwcOfDocsWithoutCatForTerm(term : String) : Double = {
      var sum = termFrequenciesOfDocsWithoutCat.getOrElse(term, 0.0) + 1
      var denominator = denominatorOfDocsWithoutCat.toDouble
      //println("p(w| not c) for w=" + term + " and c="+category + ": " + sum+"/"+denominator)
      return (sum/ denominator)
    }
}