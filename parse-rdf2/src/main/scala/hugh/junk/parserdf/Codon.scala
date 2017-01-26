package hugh.junk.parserdf

/**
  * Created by jamiesoh on 1/24/17.
  */
class Codon(val sub: String, val prd: String, val obj: ObjNode) {
  override def toString: String = s"(${sub}, ${prd}, ${obj})"
}

class ObjNode(val nt: Int, val value: String, val lang: Option[String], val schema: Option[String]) {
  override def toString(): String = {
    nt match {
      case 1 => s"(${nt},${value})"
      case 2 => s"(${nt}, ${value})"
      case 3 => s"(${nt}, ${value}, ${schema.get})"
      case 4 => s"(${nt}, ${value}, ${lang.get})"
      case 5 => s"(${nt}, ${value})"
    }
  }
}

object Codon {
  /*
  literal forms:
     " ... " .  - plain literal (may have escapes)
     " ... "@en - tagged literal (may have escapes)
     "..."^^<schema> - typed literal with schema
   */
  val items = io.Source.fromFile("src/test/resources/sample1.nt").getLines().toList
  val firstPassRx = """^\s*(\S+)\s*(\S+)\s*(.+)\s*\.$""".r
  // breaks the input into 3 groups
  val iri =
  """^<([^>]+)>\s*$""".r
  val local = """^(_:.+)\s*$""".r
  val literalPlain = """"(.+)"\s*""".r
  val literalTagged = """(.+)@(.+)\s*""".r
  val literalTyped = """"([^"]+)"\^\^(.+)\s*""".r


  def apply(input: String): Option[Codon] = {
    // split the nt into (s,p,o) sections.  The variant used is the 1st item in the tuple.
    try {
      val codon = input match {
        case firstPassRx(s, p, o) => (s, p, o)
      }

      // assumed: subject and pred are always IRIs
      val subject = isolateIri(codon._1)
      val predicate = isolateIri(codon._2)


      // process the object portion
      val objn = codon._3 match {
        case iri(id) => new ObjNode(1, id, None, None)
        case local(loc) => new ObjNode(2, loc, None, None)
        case literalTyped(l, sch) => new ObjNode(3, l, None, Some(sch))
        case literalTagged(l, lang) => new ObjNode(4, l, Some(lang), None)
        case literalPlain(l) => new ObjNode(5, l, None, None)
      }

      Some(new Codon(subject, predicate, objn))
    } catch {
      case nomatch: MatchError => {
        println(s"unparsable input == $input")
        None
      }
    }
  }

  def isolateIri(text: String): String = {
    text match {
      case iri(s) => s
      case local(s) => s
    }
  }


}
