import ch.ethz.dal.tinyir.processing.XMLDocument

//class SmartConditionalWordProbability(category: String, termFrequenciesPerDocument : Map[String, Map[String, Int]], denominatorCategory : Double, denominatorNotCategory : Double, docsPerCategory : Set[String]) {
//class SmartConditionalWordProbability(category: String, tfMergedWithCat: Map[String, Int], tfMergedWithoutCat: Map[String, Int], denominatorCategory : Double, denominatorNotCategory : Double, docsPerCategory : Set[String]) {
class SmartConditionalWordProbability(category: String, termFrequenciesPerDocument : Map[String, Map[String, Int]], termFrequenciesOverAllDocs : Map[String, Int], denominatorCategory : Double, denominatorNotCategory : Double, docsPerCategory : Set[String]) {
  
    /*var termFrequenciesOfDocsWithCat = termFrequenciesPerDocument.filter(tfPerDoc => docsPerCategory.contains(tfPerDoc._1))
    var tfMergedWithCat : Map[String, Int] = Map()
    termFrequenciesOfDocsWithCat.foreach{
      tf => tf._2.toMap
      tfMergedWithCat = tfMergedWithCat ++ tf._2.map {
        case (term,frequency) => term -> (tfMergedWithCat.getOrElse(term, 0) + frequency) 
      }
    }
    var termFrequenciesOfDocsWithoutCat = termFrequenciesPerDocument.filterNot(tfPerDoc => docsPerCategory.contains(tfPerDoc._1))
    var tfMergedWithoutCat : Map[String, Int] = Map()
    termFrequenciesOfDocsWithoutCat.foreach{
      tf => tf._2.toMap
      tfMergedWithoutCat = tfMergedWithoutCat ++ tf._2.map {
        case (term,frequency) => term -> (tfMergedWithoutCat.getOrElse(term, 0) + frequency) 
      }
    }*/
    
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
      //var sum = docsPerCategory.toList.map(docName => (termFrequenciesPerDocument(docName).getOrElse(term, 0))).sum.toDouble + 1.0
      var sum = tfMergedWithCat.getOrElse(term, 0) + 1.0
      //println("p(w|c) for w=" + term + " and c="+category + ": " + sum+"/"+denominatorCategory)
      return (sum/denominatorCategory)
    }
    
    def getPwcOfDocsWithoutCatForTerm(term : String) : Double = {
      //var docs = termFrequenciesPerDocument.keys.filterNot(docsPerCategory)
      //var sum = docs.toList.map(docName => (termFrequenciesPerDocument(docName).getOrElse(term, 0))).sum.toDouble + 1.0
      var sum = tfMergedWithoutCat.getOrElse(term, 0) + 1.0
      //println("p(w| not c) for w=" + term + " and c="+category + ": " + sum+"/"+denominatorNotCategory)
      return (sum/ denominatorNotCategory)
    }
}