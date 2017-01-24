package hugh.junk.parserdf

import scala.io.Source

/**
  * Created by hughj on 1/12/17.
  */
object Parser {

  val spo = """^\s*(\S+)\s*(\S+)\s*(\S+)\s*\.$""".r
  val spl = """^\s*(\S+)\s*(\S+)\s*(".+"(?=\s))\s\.$""".r
  val sptl = """^\s*(\S+)\s*(\S+)\s*(".+"(?=\^)\^\^<[^>]+>)\s*\.$""".r
  val spll = """^\s*(\S+)\s*(\S+)\s*("[^"]+"@\S+)\s*\.$""".r

  def main(args: Array[String]): Unit = {

    // make sure we are passed a file as the primary arg(0):
    require(args.length > 0, "path to input file required!")

    // create the source
    val rawData = Source.fromFile(args(0))
    rawData.getLines() foreach println

    System.exit(0)
  }

  class Codon(sub: String, prd: String, objNode: ObjNode)

  class ObjNode(nt: Int, value: String, lang: String, schema: String)

  object Codon {
    def apply(input: String): Codon = {
      new Codon("", "", new ObjNode(0, "", "", ""))
    }
  }

}
