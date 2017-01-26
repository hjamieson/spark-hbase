package hugh.junk.parserdf

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

/**
  * Created by jamiesoh on 1/24/17.
  */
@RunWith(classOf[JUnitRunner])
class CodonTest extends FunSuite {
  val items = io.Source.fromFile("src/test/resources/sample1.nt").getLines().toList
  val i1 = """<http://bnb.data.bl.uk/id/agent/2Kings%26LuvPublishers> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/dc/terms/Agent> ."""
  val i2 = """<http://example.org/show/218> <http://www.w3.org/2000/01/rdf-schema#label> "That Seventies Show" ."""
  val malformedItem = "<babble><babble> foo"

  test("I can unmarshal a basic nt stmt") {
    val c = Codon(i1)
    assert(c.isDefined, "make sure we got one back")
    assert(c.get.obj.nt == 1, "we used spo to parse")
    println(s"${c.get.sub} was type ${c.get.obj.nt}")
  }

  test("I can process all items") {
    val codons = items.map(Codon(_))
    assert(items.size == codons.size, "all items parsed")
  }

  test("Parse fails for invalid input") {
    val codon = Codon(malformedItem);
    assert(codon == None, "we should get None")
  }

  test("I have a toString override"){
    val c = Codon(i2)
    assert(c.isDefined)
    val cod = c.get
    assert(cod.sub == "http://example.org/show/218")
    assert(cod.prd == "http://www.w3.org/2000/01/rdf-schema#label")
    assert(cod.obj.nt == 5)
    assert(cod.obj.value == "That Seventies Show")
    println(c.toString)
  }

  test("I can print all codons"){
    items.map(Codon(_)).foreach(c => println(c.get))
  }

}
