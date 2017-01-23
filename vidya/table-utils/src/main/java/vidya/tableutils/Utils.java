package vidya.tableutils;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * Created by hughj on 12/4/16.
 */
public class Utils {

   private static final HashFunction hf = Hashing.murmur3_32();

   public static byte[] hashData(byte[] data){
      return hf.hashBytes(data).asBytes();
   }
}
