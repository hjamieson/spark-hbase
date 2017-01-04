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
}
