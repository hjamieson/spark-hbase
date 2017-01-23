package vidya.tableutils;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;


/**
 * Created by hughj on 12/4/16.
 */
public class KeyTests {
   // lets use guava to hash a subject and return a key
   @Test
   public void makeKeyTest(){
      // set up some test data
      String spo1 = "<http://bnb.data.bl.uk/id/agent/AcumenPublications> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/dc/terms/Agent> .";
      String spo2 = "<http://bnb.data.bl.uk/id/agent/AcumenPublications> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent> .";
      HashFunction hf = Hashing.murmur3_32();

      HashCode hc1 = hf.hashBytes(spo1.getBytes());
      HashCode hc2 = hf.hashBytes(spo2.getBytes());
      System.out.println("hashcode="+ hc1.toString());
      System.out.println("hashcode="+ hc2.toString());
      assertThat(hc1.asBytes(),not(equalTo(hc2.asBytes())));

      // now lets compare it to the Utils version
      assertThat(hc1.asBytes(),equalTo(Utils.hashData(spo1.getBytes())));
      assertThat(hc2.asBytes(),equalTo(Utils.hashData(spo2.getBytes())));

   }
}
