package vidya.hbase.utils;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.hamcrest.core.Is.isA;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by hughj on 12/10/16.
 */

public class NTTest {
   private static String nt1 = "<http://bnb.data.bl.uk/id/agent/AcceleratorScienceandTechnologyCentreDaresburyLaboratory> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/dc/terms/Agent> .";
   private static String nt2 = "<http://bnb.data.bl.uk/id/concept/ddc/e21/372.83205> <http://www.w3.org/2004/02/skos/core#notation> \"372.83205\"^^<http://dewey.info/schema-terms/Notation> .";
   @Test
   public void testSplitSubject() {
      NT nt = new NT("<foobar><isA><urlOfSomething> .");
      assertThat(nt.getSubject(), equalTo("foobar"));

      nt = new NT(nt1);
      assertThat(nt.getSubject(),equalTo("http://bnb.data.bl.uk/id/agent/AcceleratorScienceandTechnologyCentreDaresburyLaboratory"));
   }

   @Test
   public void testSplitPredicate() {
      NT nt = new NT("<foobar><isA><urlOfSomething> .");
      assertThat(nt.getPredicate(), equalTo("isA"));
      nt = new NT(nt1);
      assertThat(nt.getPredicate(),equalTo("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
   }

   @Test
   public void testSplitObject() {
      NT nt = new NT("<foobar><isA><urlOfSomething> .");
      assertThat(nt.getObject(), isA(NT.NTObject.class));
      assertThat(nt.getObject().getType(), equalTo(NT.ObjectType.URI));
      assertThat(nt.getObject().getBody(), equalTo("urlOfSomething"));
      nt = new NT(nt1);
      assertThat(nt.getObject().getType(), equalTo(NT.ObjectType.URI));
      assertThat(nt.getObject().getBody(), equalTo("http://purl.org/dc/terms/Agent"));
      assertThat(new NT(nt2).getObject().getType(),equalTo(NT.ObjectType.UNKNOWN));
   }

   @Test
   public void testSamplesFile() throws IOException {
      InputStream is = this.getClass().getResourceAsStream("/samples.nt");
      try (BufferedReader rdr = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/samples.nt")))){
         String text;
         while ((text = rdr.readLine())!=null){
            NT nt = new NT(text);
            assertNotNull(nt.getSubject());
            assertNotNull(nt.getPredicate());
            assertNotNull(nt.getObject());
         }
      }
   }

}
