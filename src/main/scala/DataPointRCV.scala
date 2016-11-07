import breeze.linalg.DenseVector
/**
  * Created by Aun on 06/11/2016.
  */
class DataPointRCV (tokens : List[String], classes : Set[Int], dictionary : Map[String, Int]) extends DataPoint {
    override def input: DenseVector[Double] = {
        val x = DenseVector.zeros[Double](dictionary.size + 1)
        x(dictionary.size) = 1.0
        // Words
        for (word <- tokens){
            if (dictionary.contains(word)){
                x(dictionary.getOrElse(word, 0)) += 1
            }
        }
        x
    }

    override def output: Set[Int] = classes
}
