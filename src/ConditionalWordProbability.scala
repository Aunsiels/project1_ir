import ch.ethz.dal.tinyir.processing.XMLDocument

class ConditionalWordProbability(seqOfXMLDocs: Seq[XMLDocument], vocabularySize: Double, category: String) {

    val tokensOfDocsWithCat = seqOfXMLDocs.filter(_.codes(category)).flatMap(_.tokens).toSet
    val denominatorOfDocsWithCat = tokensOfDocsWithCat.size.toDouble + vocabularySize
    val termFrequenciesOfDocsWithCat = tokensOfDocsWithCat.groupBy(identity).map(term => (term._1, term._2.size+1))     
    val tokensOfDocsWithoutCat = seqOfXMLDocs.filterNot(_.codes(category)).flatMap(_.tokens).toSet
    val denominatorOfDocsWithoutCat = tokensOfDocsWithoutCat.size.toDouble + vocabularySize
    val termFrequenciesOfDocsWithoutCat = tokensOfDocsWithoutCat.groupBy(identity).map(term => (term._1, term._2.size+1))
        
    def getPwcOfDocsWithCatForTerm(term : String) : Double = {
       return (termFrequenciesOfDocsWithCat.getOrElse(term, 1.0).toString().toDouble / denominatorOfDocsWithCat.toDouble)
    }
    
    def getPwcOfDocsWithoutCatForTerm(term : String) : Double = {
       return (termFrequenciesOfDocsWithoutCat.getOrElse(term, 1.0).toString().toDouble / denominatorOfDocsWithoutCat.toDouble)
    }
}