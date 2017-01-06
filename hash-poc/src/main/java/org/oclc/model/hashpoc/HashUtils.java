package org.oclc.model.hashpoc;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * Created by hughj on 1/3/17.
 */
public class HashUtils {
   public static short hashCode(byte[] bytes){
      HashFunction hf = Hashing.goodFastHash(16);
      HashCode hc = hf.hashBytes(bytes);
      return (short) (hc.asInt() % 512);
   }

   /**
    * returns an 8-bit salt value that can be used for distribution on a rowkey.  NOte that
    * it is only capable of expressing a 256 value, which would be the upper value for the
    * number of regions.
    * @param bytes  the bytes to use for the hash
    * @return single byte with salt value
    */
   public static byte getSalt(byte[] bytes){
      HashFunction hf = Hashing.goodFastHash(16);
      HashCode hc = hf.hashBytes(bytes);
      byte[] hash = hc.asBytes();
      return (byte) hash[hash.length-1];
   }
}
