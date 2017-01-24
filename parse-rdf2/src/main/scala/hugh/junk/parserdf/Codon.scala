package hugh.junk.parserdf

/**
  * Created by jamiesoh on 1/24/17.
  */
class Codon(val sub: String, val prd: String, val obj: ObjNode)

class ObjNode(val nt: Int, val value: String, val lang: Option[String], val schema: Option[String])

object Codon {
  /*
these regexs are paired with a value that can be used to know which variant
was used to match the incoming nt input.
 */

  /* classic s-p-o with all IRIs. */
  val spo = ("""^\s*(\S+)\s*(\S+)\s*(\S+)\s*\.$""".r, 0)
  /* simple literal object */
  val spl = ("""^\s*(\S+)\s*(\S+)\s*(".+"(?=\s))\s\.$""".r, 1)
  /* object is a typed literal */
  val sptl = ( """^\s*(\S+)\s*(\S+)\s*"(.+"(?=\^))\^\^<([^>]+)>\s*\.$""".r, 2)
  /* object is lang-tagged literal */
  val spll = ("""^\s*(\S+)\s*(\S+)\s*("[^"]+"@\S+)\s*\.$""".r, 3)


  def apply(input: String): Option[Codon] = {
    // split the nt into (s,p,o) sections.  The variant used is the 1st item in the tuple.
    try {
      val codon = input match {
        case spl._1(s, p, o) => (spl._2, s, p, o, None)
        case sptl._1(s, p, o, t) => (sptl._2, s, p, o, Some(t))
        case spll._1(s, p, o) => (spll._2, s, p, o, None)
        case spo._1(s, p, o) => (spo._2, s, p, o, None)
      }
      val result = codon._1 match {
        case 0 => {
          new Codon(codon._2, codon._3, new ObjNode(codon._1, codon._4, None, None))
        }
        case 1 => {
          // breakdown simple literal
          new Codon(codon._2, codon._3, new ObjNode(codon._1, codon._4, None, None))
        }
        case 2 => {
          // store typed literal
          new Codon(codon._2, codon._3, new ObjNode(codon._1, codon._4, None, codon._5))
        }
        case 3 => {
          // language tagged literal
          new Codon(codon._2, codon._3, new ObjNode(codon._1, codon._4, codon._5, None))
        }
      }
      Some(result)
    } catch {
      case nomatch: MatchError => {
        println(s"unparsable input == $input")
        None
      }
    }
  }
}
