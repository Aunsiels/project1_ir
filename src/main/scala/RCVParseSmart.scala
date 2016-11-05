/**
  * Created by mmgreiner on 02.11.16.
  */

import java.io.InputStream

import ch.ethz.dal.tinyir.io.{DocStream, ReutersRCVStream}
import ch.ethz.dal.tinyir.processing.ReutersRCVParse
import com.github.aztek.porterstemmer.PorterStemmer


class RCVParseSmart(is: InputStream, reduceStopWords: Boolean = false, stemming: Boolean = false) extends ReutersRCVParse(is) {

  val raw_tokens = super.tokens

  /**
    * Stopwords is taken from nltk toolkit stopwords - english
    */
  val Stopwords = List(
    "i",
    "me",
    "my",
    "myself",
    "we",
    "our",
    "ours",
    "ourselves",
    "you",
    "your",
    "yours",
    "yourself",
    "yourselves",
    "he",
    "him",
    "his",
    "himself",
    "she",
    "her",
    "hers",
    "herself",
    "it",
    "its",
    "itself",
    "they",
    "them",
    "their",
    "theirs",
    "themselves",
    "what",
    "which",
    "who",
    "whom",
    "this",
    "that",
    "these",
    "those",
    "am",
    "is",
    "are",
    "was",
    "were",
    "be",
    "been",
    "being",
    "have",
    "has",
    "had",
    "having",
    "do",
    "does",
    "did",
    "doing",
    "a",
    "an",
    "the",
    "and",
    "but",
    "if",
    "or",
    "because",
    "as",
    "until",
    "while",
    "of",
    "at",
    "by",
    "for",
    "with",
    "about",
    "against",
    "between",
    "into",
    "through",
    "during",
    "before",
    "after",
    "above",
    "below",
    "to",
    "from",
    "up",
    "down",
    "in",
    "out",
    "on",
    "off",
    "over",
    "under",
    "again",
    "further",
    "then",
    "once",
    "here",
    "there",
    "when",
    "where",
    "why",
    "how",
    "all",
    "any",
    "both",
    "each",
    "few",
    "more",
    "most",
    "other",
    "some",
    "such",
    "no",
    "nor",
    "not",
    "only",
    "own",
    "same",
    "so",
    "than",
    "too",
    "very",
    "s",
    "t",
    "can",
    "will",
    "just",
    "don",
    "should",
    "now",
    "d",
    "ll",
    "m",
    "o",
    "re",
    "ve",
    "y",
    "ain",
    "aren",
    "couldn",
    "didn",
    "doesn",
    "hadn",
    "hasn",
    "haven",
    "isn",
    "ma",
    "mightn",
    "mustn",
    "needn",
    "shan",
    "shouldn",
    "wasn",
    "weren",
    "won",
    "wouldn"
    )

  var stem = (w: String) => if (stemming) PorterStemmer.stem(w) else w

  val stop = (w: String) => if (Stopwords contains w) "<STOP>" else w


  def tokenize(words: List[String]): List[String] = {
    var w = words.flatMap(x =>
      x.replaceAll("^\\d+[/-]\\d+[/-]\\d+$", "<DAT>")       // dates or phone numbers
        .replaceAll("^\\d+([.,]\\d+)*$", "<NUM>")           // general numbers
        .replaceAll("[,;.:]", " <PUN> ")                    // punctation
        .replaceAll("\\s", " ")
        .split(" ")
        .filterNot(_==""))
    w.map(w => stem(stop(w)))
  }

  private var _smartTokens: List[String] = _

  override def tokens = {
    if (_smartTokens == null)
      _smartTokens = tokenize(raw_tokens)
    _smartTokens
  }

  def test(stream: ReutersRCVStream, howMany: Integer = 500): Unit = {
    val docs = stream.stream.slice(0, howMany)
    val rawset = raw_tokens.toSet
    val tokset = tokens.toSet
    println(s"nof raw_tokens ${raw_tokens.size}, distinct ${rawset.size}, nof smart token ${tokens.size}, ${tokset.size}")
  }
}

object RCVParseSmart {
  def main(args: Array[String]) {

    val ps = PorterStemmer.stem("failing")

    val dir = "/Users/mmgreiner/Projects/InformationRetrieval/data/score2/train-orig/"
    val fname = dir + "100009newsML.xml"
    val parse = new RCVParseSmart(DocStream.getStream(fname), reduceStopWords = true, stemming = true)
    val title = parse.title
    println(title)
    println("DocID = " + parse.ID)
    println("Date  = " + parse.date)
    println("Codes = " + parse.codes.mkString(" "))
    println("raw_tokens = " + parse.raw_tokens)
    println(s"raw  set size ${parse.raw_tokens.size}")
    println("tokens = " + parse.tokens)
    println(s"tokenset size ${parse.tokens.toSet.size}")

  }
}
