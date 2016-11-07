/**
  * Created by Aun on 06/11/2016.
  */
object Evaluation {
    case class Stat(precision : Double,
                    recall : Double,
                    f1 : Double)


    // retriev = prediction
    def getStat[A] (retriev : Set[A], relev : Set[A], beta : Double) : Stat = {
        val truePos = (relev & retriev).size.toDouble
        val precision = truePos / retriev.size
        val recall = truePos / relev.size
        val f1 = (beta * beta + 1) * precision * recall / (beta * beta * precision + recall)
        Stat(precision, recall, f1)
    }

    def getStat[A] (retrievs : Iterable[Set[A]], relevs : Iterable[Set[A]], beta : Double) : Stat = {
        var precisionCount = 0.0
        var recallCount = 0.0
        var nPositive = 0.0
        for ((retriev, relev) <- retrievs.zip(relevs)){
            precisionCount += retriev.size
            recallCount += relev.size
            nPositive += (relev & retriev).size.toDouble
        }
        val precision = nPositive / precisionCount
        val recall = nPositive / recallCount
        val f1 = (beta * beta + 1) * precision * recall / (beta * beta * precision + recall)
        Stat(precision, recall, f1)
    }
}