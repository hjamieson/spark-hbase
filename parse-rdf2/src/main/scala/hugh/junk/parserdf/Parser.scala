package hugh.junk.parserdf

import scala.io.Source

/**
  * Created by hughj on 1/12/17.
  */
object Parser {

  def main(args: Array[String]): Unit = {

    // make sure we are passed a file as the primary arg(0):
    require(args.length > 0, "path to input file required!")

    // create the source
    val rawData = Source.fromFile(args(0))
    val codons = rawData.getLines().map(Codon(_))

    System.exit(0)
  }


}
