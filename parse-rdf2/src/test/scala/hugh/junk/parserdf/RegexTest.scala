package hugh.junk.parserdf

import org.scalatest.FunSuite

/**
  * Created by hughj on 1/13/17.
  */
class RegexTest extends FunSuite {
val items = io.Source.fromFile("src/test/resources/sample1.nt").getLines().toList

  test("I can see the test items from the test"){
    assert(items.size != 0)
  }

  test("every item starts with a '<' or '_:'"){
    assert(items map(s =>s.startsWith("<") || s.startsWith("_:")) reduceLeft(_ && _))
  }

  test("every item ends with a '.'"){
    assert(items map(_.endsWith(".")) reduceLeft(_ && _))
  }

  test("we can isolate subject between < and >"){
    val rx = """^<([^>]+).*""".r
    val zzz = items.map(_ match {
      case rx(sub) => sub
    })
    zzz.foreach(l => assert(!l.contains("<") && !l.contains(">")))
  }

  test("we can isolate predicate between < and >"){
    val rx = """^<([^>]+)>\s*<([^>]+)>.*""".r
    val preds = items.map(_ match {
      case rx(sub, prd) => prd
    })
    preds.foreach(l => assert(!l.contains("<") && !l.contains(">")))
//    preds foreach println
  }

  test("we can isolate the object for further decoding"){
    val rx = """^<([^>]+)>\s*<([^>]+)>\s*(.+)\.\s*$""".r
    val objs = items.map(_ match {
      case rx(_, _, obj) => obj.trim
    })
    objs foreach println
    objs.foreach(l => assert(!l.endsWith("."),"must not include period at end"))
    objs.foreach(y => y(0) match {
      case '"' => assert(y.endsWith("\""))
      case '<' => assert(y.endsWith(">"))
    })
  }


}
