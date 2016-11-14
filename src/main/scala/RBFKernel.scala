import breeze.linalg.{DenseVector, norm}

import math._
import scala.util.Random

/**
  * Created by Aun on 06/11/2016.
  */
class RBFKernel(sigma : Double, nReduced : Int = 1, inputSize : Int = 1) extends Kernel with ShiftInvariantKernel{

    case class RandomFeature(weight : DenseVector[Double], bias : Double) {
        def apply(x : DenseVector[Double]) = {
            math.cos(weight.dot(x) + bias)
        }
    }

    def apply(x : DenseVector[Double], y : DenseVector[Double]) : Double = {
        val normDiff = norm(x - y)
        math.exp(-1 * normDiff * normDiff / 2.0 / sigma / sigma)
    }

    private val normal01 = breeze.stats.distributions.Gaussian(0, 1 / sigma / sigma)
    private val random = new Random

    private def generateRandomFeature(size : Int) = {
        val weight = DenseVector.rand(size, normal01)
        val bias = random.nextDouble()
        RandomFeature(weight, bias)
    }

    private lazy val randomFeatures = for (i <- 0 until nReduced) yield generateRandomFeature(inputSize)

    def apply(x : DenseVector[Double]) : DenseVector[Double] = {
        val out = DenseVector.zeros[Double](nReduced)
        val norm = math.sqrt(2.0 / nReduced)
        for ((rf, i) <- randomFeatures.zipWithIndex){
            val temp : Double = norm * rf(x)
            out(i) = temp
        }
        out
    }

    def getReducedDim = nReduced
}
