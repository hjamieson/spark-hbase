package hugh.junk.parserdf

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

/**
  * Created by hughj on 1/13/17.
  */
@RunWith(classOf[JUnitRunner])
class RegexTest extends FunSuite {
  /*
  literal forms:
     " ... " .  - plain literal (may have escapes)
     " ... "@en - tagged literal (may have escapes)
     "..."^^<schema> - typed literal with schema
   */
  val items = io.Source.fromFile("src/test/resources/sample1.nt").getLines().toList
  val firstPassRx = """^\s*(\S+)\s*(\S+)\s*(.+)\s*\.$""".r  // breaks the input into 3 groups
  val iri = """^<([^>]+)>\s*$""".r
  val local = """^(_:.+)\s*$""".r

  test("I can see the test items from the test") {
    assert(items.size != 0)
  }

  test("every item starts with a '<' or '_:'") {
    assert(items map (s => s.startsWith("<") || s.startsWith("_:")) reduceLeft (_ && _))
  }

  test("every item ends with a '.'") {
    assert(items map (_.endsWith(".")) reduceLeft (_ && _))
  }

  test ("we can parse literals with escaped quotes"){

    val sample = """"This is a multi-line\nliteral with many quotes (\"\"\"\"\")\nand two apostrophes ('')." ."""
    val expected = """This is a multi-line\nliteral with many quotes (\"\"\"\"\")\nand two apostrophes ('')."""
    val litrx = """^\s*"(.+)"\s*\.$""".r
    val literal = sample match {
      case litrx(l) => l
    }
    assert(literal == expected)
  }

  test("all stmts composed of 3 globs + .") {

    val zzz = items.map(_ match {
      case firstPassRx(s, p, o) => (s, p, o)
    })
  }

  test("subject is either <sub> or _: form") {
    val zzz = items.map(_ match {
      case firstPassRx(s, _, _) => s
    })
    zzz.foreach(l => assert(l.startsWith("<") || l.startsWith("_:")))
    zzz.foreach(_ match {
      case iri(s) => s
      case local(s) => s
    })
  }

  test("we can isolate predicate between < and >") {
    val preds = items.map(_ match {
      case firstPassRx(_, p, _) => p
    })
    preds.foreach(_ match {
      case iri(p) => p
    })
  }

  test("we can isolate the object for further decoding") {
    val objs = items.map(_ match {
      case firstPassRx(_, _, o) => o
    })

    val literalPlain = """"(.+)"\s*""".r
    val literalTagged = """(.+)@(.+)\s*""".r
    val literalTyped = """"([^"]+)"\^\^(.+)\s*""".r

    val nodes = objs.map(_ match {
      case iri(id) => (4, id, None, None)
      case local(loc) => (5, loc, None, None)
      case literalTyped(l, sch) => (3, l, None, Some(sch))
      case literalTagged(l, lang) => (2, l, Some(lang), None)
      case literalPlain(l) => (1, l, None, None)
      case s => (6, s, None, None)
    })

    nodes.foreach(n => {
      n match {
        case (1, txt, None, None) => println(1, txt)
        case (2, txt, Some(lng), None) => println(2,txt)
        case (3, txt, None, Some(sch)) => println(3, sch)
        case (4, id, _,_) => println(4, id)
        case (5, loc, _,_) => println(5, loc)
      }
    })
  }

//  test ("object-literals are unquoted"){
//    val sample = """<http://bnb.data.bl.uk/id/agent/Abacus> <http://www.w3.org/2000/01/rdf-schema#label> "Abacus" ."""
//    val literal = sample match {
//      case spl(_,_,l) => l
//    }
//    assert(literal == "Abacus")
//  }

  test("we can unpack the global and local iris") {
    val o1 = "<http://bnb.data.bl.uk/id/concept/ddc/e20/823.914>"
    val o2 = "_:abc123"
    val out = o1 match {
      case iri(a) => a
      case _ => o1
    }
    assert(out == "http://bnb.data.bl.uk/id/concept/ddc/e20/823.914")
    val out2 = o2 match {
      case iri(a) => a
      case _ => o2
    }
    assert(out2 == "_:abc123")
  }


}
