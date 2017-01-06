package org.oclc.data.hashpoc;

import com.google.common.primitives.Shorts;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by hughj on 1/3/17.
 */
public class HashUtilsTest {

   public static final int TEST_SIZE = 256;
   Stream<String> getIris(int count){
      String[] strings = new String[count];
      for (int i = 0; i < count; i++){
         strings[i]= "http://oc.lc/ent/abc"+i;
      }
      return Arrays.stream(strings);
   }

   @Test
   public void testGenHash() {
      short hashValue = HashUtils.hashCode16("http://entity.oclc.org/12345".getBytes());
      assertThat(hashValue, not(equalTo(0)));
      assertThat(hashValue, lessThan((short)512));
      System.out.println("bits = " + DatatypeConverter.printHexBinary(Shorts.toByteArray(hashValue)));
   }

   @Test
   public void testGetSalt(){
      int numSamples = 1000;
      Stream<String> iris = getIris(numSamples);
      Stream<Byte> salts = iris.map(s -> HashUtils.getSalt(s.getBytes()));
      assertThat("we should get a diff salt for each",(int)salts.count(), equalTo(numSamples));

      assertThat("expect ~2/1 distribution",getIris(numSamples).map(s -> HashUtils.getSalt(s.getBytes())).distinct().count(), lessThan((long)(numSamples/2)));
   }

   @Test
   public void testSaltDistribution(){
      List<Byte> res = getIris(256).map(s -> HashUtils.getSalt(s.getBytes())).collect(Collectors.toList());
      Stream<Byte> byteStream = res.stream();
      assertThat("expect at least half of 256 distinct values",byteStream.distinct().count(), greaterThan(128l));
   }

   @Test
   public void testSaltStability(){
      byte[] store1 = new byte[TEST_SIZE];
      byte[] store2 = new byte[TEST_SIZE];
      for (int i=0; i < store1.length; i++){
            store1[i] = HashUtils.getSalt(("http://oc.lc/ent/" + i).getBytes());
            store2[i] = HashUtils.getSalt(("http://oc.lc/ent/" + i).getBytes());
      }
      for (int j = 0; j < store1.length; j++){
         assertThat("salt value should be equal for same IRI",store1[j], equalTo(store2[j]));
      }
   }

   @Test
   public void testGetSaltedRowKey(){
      String SAMPLE = "abdcefg";
      byte[] rowkey = HashUtils.saltedRowKey(SAMPLE.getBytes());
      assertThat("must not be 'a'",rowkey[0], not(0x97));
      byte[] unsalt = HashUtils.unsalt(rowkey);
      assertThat("salt byte should be removed", rowkey[0], not(unsalt[0]));
      assertThat("expect 'a' restored", new String(unsalt), equalTo(SAMPLE));

      List<String> originalKeys = getIris(5).collect(Collectors.toList());
      List<String> keys360 = getIris(5).map(p -> HashUtils.saltedRowKey(p.getBytes())).map(z -> HashUtils.unsalt(z)).map(y -> new String(y)).collect(Collectors.toList());
      assertThat(originalKeys, containsInAnyOrder(keys360.toArray()));
   }
}
