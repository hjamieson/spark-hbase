package org.oclc.model.hashpoc;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by hughj on 1/3/17.
 */
public class HashUtilsTest {

   @Test
   public void testGenHash() {
      short hashValue = HashUtils.hashCode("http://entity.oclc.org/12345".getBytes());
      assertThat(hashValue, not(equalTo(0)));
      assertThat(hashValue, lessThan((short)512));
      System.out.println("bits = " + DatatypeConverter.printHexBinary(Shorts.toByteArray(hashValue)));
   }

   @Test
   public void testShiftRight() {
      int f = 0x0b0b0a0a;
      int s = f >> 16;
      assertThat(s, equalTo(0x0b0b));
      assertThat(f & 0x0000ffff, equalTo(0x0a0a));

      System.out.println(Integer.valueOf(-1).shortValue());
   }

   @Test
   public void testSignedShift() {
      int bits = Integer.MIN_VALUE;
      System.out.println("bits = " + bits);
      assertThat(bits, lessThan(0));
      assertThat(bits >> 8, lessThan(0));
      assertThat(bits >>> 8, greaterThan(0));
      System.out.println(DatatypeConverter.printHexBinary(Ints.toByteArray(bits)));
   }
}
