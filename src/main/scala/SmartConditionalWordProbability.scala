import ch.ethz.dal.tinyir.processing.XMLDocument

class SmartConditionalWordProbability(category: String, termFrequenciesPerDocument : Map[String, Map[String, Int]], termFrequenciesOverAllDocs : Map[String, Int], denominatorCategory : Double, denominatorNotCategory : Double, docsPerCategory : Set[String]) {
      
    var termFrequenciesOfDocsWithCat = termFrequenciesPerDocument.filter(tfPerDoc => docsPerCategory.contains(tfPerDoc._1))
    var tfMergedWithCat : Map[String, Int] = Map()
    termFrequenciesOfDocsWithCat.foreach{
      tf => tf._2.toMap
      tfMergedWithCat = tfMergedWithCat ++ tf._2.map {
        case (term,frequency) => term -> (tfMergedWithCat.getOrElse(term, 0) + frequency) 
      }
    }
    
    var tfMergedWithoutCat = termFrequenciesOverAllDocs ++ tfMergedWithCat.map {
      case (term,frequency) => term -> (termFrequenciesOverAllDocs.getOrElse(term, 0) - frequency)
    }
  
    def getPwcOfDocsWithCatForTerm(term : String) : Double = {
      var sum = tfMergedWithCat.getOrElse(term, 0) + 1.0
      (sum/denominatorCategory)
    }
    
    def getPwcOfDocsWithoutCatForTerm(term : String) : Double = {
      var sum = tfMergedWithoutCat.getOrElse(term, 0) + 1.0
      (sum/ denominatorNotCategory)
    }
}