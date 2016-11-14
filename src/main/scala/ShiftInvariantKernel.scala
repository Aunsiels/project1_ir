import breeze.linalg.DenseVector
/**
  * Created by Aun on 12/11/2016.
  */
trait ShiftInvariantKernel extends Kernel {
    def apply(x: DenseVector[Double]): DenseVector[Double]
    def getReducedDim : Int
}
