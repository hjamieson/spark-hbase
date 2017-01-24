package hugh.junk.parserdf

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

/**
  * Created by hughj on 1/13/17.
  */
@RunWith(classOf[JUnitRunner])
class RegexTest extends FunSuite {
  val items = io.Source.fromFile("src/test/resources/sample1.nt").getLines().toList
  val spo = """^\s*(\S+)\s*(\S+)\s*(\S+)\s*\.$""".r
  val spl = """^\s*(\S+)\s*(\S+)\s*(".+"(?=\s))\s\.$""".r
  val sptl = """^\s*(\S+)\s*(\S+)\s*(".+"(?=\^)\^\^<[^>]+>)\s*\.$""".r
  val spll = """^\s*(\S+)\s*(\S+)\s*("[^"]+"@\S+)\s*\.$""".r

  test("I can see the test items from the test") {
    assert(items.size != 0)
  }

  test("every item starts with a '<' or '_:'") {
    assert(items map (s => s.startsWith("<") || s.startsWith("_:")) reduceLeft (_ && _))
  }

  test("every item ends with a '.'") {
    assert(items map (_.endsWith(".")) reduceLeft (_ && _))
  }

  test("all stmts composed of 3 globs + .") {

    val zzz = items.map(_ match {
      case spo(s, p, o) => (s, p, o)
      case spl(s, p, o) => (s, p, o)
      case sptl(s, p, o) => (s, p, o)
      case spll(s, p, o) => (s, p, o)
    })
  }

  test("subject is either <sub> or _: form") {
    val zzz = items.map(_ match {
      case spo(s, _, _) => s
      case spl(s, _, _) => s
      case sptl(s, _, _) => s
      case spll(s, _, _) => s
    })
    zzz.foreach(l => assert(l.startsWith("<") || l.startsWith("_:")))
  }

  test("we can isolate predicate between < and >") {
    val preds = items.map(_ match {
      case spo(_, p, _) => p
      case spl(_, p, _) => p
      case sptl(_, p, _) => p
      case spll(_, p, _) => p
    })
    val px = """^<([^>]+)>$""".r
    val preds2 = preds.map(_ match { case px(p) => p })
  }

  test("we can isolate the object for further decoding") {
    val objs = items.map(_ match {
      case spl(_, _, o) => (o, "spl")
      case sptl(_, _, o) => (o, "sptl")
      case spll(_, _, o) => (o, "spll")
      case spo(_, _, o) => (o, "spo")
    })
    objs.foreach(l => assert(!l._1.endsWith("."), "must not include period at end"))
    objs.foreach(y => y._2 match {
      case "spo" => assert(y._1.endsWith(">") || y._1.startsWith("_:"))
      case "spl" => assert(y._1.endsWith("\""))
      case "spll" => assert(y._1.matches(""".+@\S+"""))
      case "sptl" => assert(y._1.startsWith("\"")&& y._1.endsWith(">"))
    })
  }


}
