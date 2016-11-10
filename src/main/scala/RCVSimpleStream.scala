
/**
  * Created by mmgreiner on 08.11.16.
  *
  * This class preprocesses a stream of xml documents for classifications and creates a zip file with the crucial
  * information of those documents. this allows for very < 2 min of loading the training data
  */


import java.io.{FileInputStream, FileOutputStream, InputStream, ObjectInputStream, ObjectOutputStream}
import java.util.logging.Logger
import java.util.zip.{ZipEntry, ZipFile, ZipInputStream, ZipOutputStream}

import ch.ethz.dal.tinyir.processing.{XMLDocument}

import scala.util.{Failure, Success, Try}
import scala.collection.JavaConversions._


@SerialVersionUID(19651110)
class RCVSimpleStream extends Serializable {

  var stream: Stream[RCVSimple] = _

  def this(filename: String) {
    this()
    read(filename)
  }

  /**
    * Read the zip file by extracting files. Too slow, use streams only
    * @param filename
    * @return
    */
  def read2(filename: String): Stream[RCVSimple] = {
    Try(new ZipFile(filename)) match {
      case Failure(zip) => Stream[RCVSimple]()
      case Success(zip) => {
        val entries = zip.entries.toList // toList required conversion
        val result = entries.map(entry => {
          val ois = new ObjectInputStream(new FileInputStream(entry.getName))
          ois.readObject.asInstanceOf[RCVSimple]
        })
        result.toStream
      }
    }
  }

  /**
    * Get the stream of RCVSimple from the given zip file
    * @param filename zip file name
    * @see RCVSimpleStream
    */
  def read(filename: String) = {

    val t = new Timer(heapInfo = true, log = RCVSimpleStream.log)
    System.gc

    Try(new FileInputStream(filename)) match {
      case Failure(fis) => {
        RCVSimpleStream.log.warning(s"failed to open $filename")
        stream = Stream[RCVSimple]()
      }
      case Success(fis) => {
        RCVSimpleStream.log.info(f"start reading $filename of ${fis.getChannel.size.toDouble / (1024 * 1024)}%1.2f MBytes")

        val buf = collection.mutable.ListBuffer[RCVSimple]()

        val zip = new ZipInputStream(fis)
        var entry = zip.getNextEntry
        while (entry != null) {
          val ois = new ObjectInputStream(zip)
          val x: RCVSimple = ois.readObject.asInstanceOf[RCVSimple]
          buf.append(x)
          t.progress("... and reading")
          entry = zip.getNextEntry
        }
        t.info("completed reading")
        zip.close
        stream = buf.toStream
      }
    }
  }

  /**
    * Write the stream to the given zipfile name. If no filename is given, a default name is taken.
    * @param filename
    */
  def write(filename: String = RCVSimpleStream.DefaultFName) = {

    System.gc

    val t = new Timer(heapInfo = true, log = RCVSimpleStream.log)
    // we have to serialize it file by file, otherwise heap crash

    // val buf = collection.mutable.ListBuffer[RCVSimple]()

    t.progress(s"starting writing zip file $filename")
    val zip = new ZipOutputStream(new FileOutputStream(filename, false))

    stream.foreach(x => {
      // val oos = new ObjectOutputStream(new FileOutputStream(tempPath + x.name))
      zip.putNextEntry(new ZipEntry(x.name))
      val oos = new ObjectOutputStream(zip)
      oos.writeObject(x)
      oos.flush
      zip.closeEntry
      t.progress("... and going")
    })
    zip.close
    t.info("competed writing")
    // now zip
  }

  /**
    * creates the stream of RCVSimple from the given stream. The stream can be any subclass fo XMLDocument such as
    * ReutersRCV or RCVSmart
    * @param rcv_stream a stream of extended class of XMLDocument
    */
  def this(rcv_stream: Stream[XMLDocument]) = {
    this()
    stream = rcv_stream.map(x => new RCVSimple(name = x.name, title = x.title, codes = x.codes,
                                              tokens = x.tokens,
                                              vocabulary =  x match {
                                              case v: RCVParseSmart => v.vocabulary
                                              case _ => x.tokens.toSet
                                            })
    )
  }
}

object RCVSimpleStream {

  val DefaultFName = "RCVSimpleStream.zip"

  val log = Logger.getLogger(RCVSimpleStream.getClass.getName)

  def testWrite(dir: String, file: String): Unit = {
    val rcv = new RCVStreamSmart(dir)
    val simple = new RCVSimpleStream(rcv.stream)
    log.info("writing all")
    simple.write(file)
    log.info("completed writing")
  }

  def testRead(file: String): Unit = {
    val simple = new RCVSimpleStream(file)
    log.info(simple.stream.head.title)

    log.info(s"running through stream of length ${simple.stream.length}, ${simple.stream.size}")
    val t = new Timer(heapInfo = true, log=log)
    simple.stream.foreach(x => t.progress("streaming"))
    log.info("finished")
  }

  def main(args: Array[String]): Unit = {
    val dir = "/Users/mmgreiner/Projects/InformationRetrieval/data/score2/train/"

    val user = System.getProperty("user.name")
    log.info(s"user: $user training files $dir")
    val fname = "train-all2.zip"
    testWrite(dir, fname)
    testRead(fname)


  }
}
