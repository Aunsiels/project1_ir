import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

/**
  * Created by mmgreiner on 08.11.16.
  */

@SerialVersionUID(100L)
class RCVSimple (val name: String, val title: String, val codes: Set[String],
                 val tokens: List[String], val vocabulary: Set[String]) extends Serializable {

}

object RCVSimple {
  def main(args: Array[String]): Unit = {
    val fname = RCVSimple.toString + ".txt"
    val oos = new ObjectOutputStream(new FileOutputStream(fname))

    val x = new RCVSimple(name = "markus", title = "greiner", tokens = List("hallo", "you"),
      vocabulary = Set("hallo", "you"), codes = Set("A", "B"))

    oos.writeObject(x)
    oos.close

    val ois = new ObjectInputStream(new FileInputStream(fname))
    val obj = ois.readObject()

    ois.close

  }

}