package org.oclc.data.hashpoc;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.util.Arrays;

/**
 * Created by hughj on 1/3/17.
 */
public class HashUtils {
    /**
     * generates a 16-bit (short) hash value that can be used  as a salt.  This is not used yet!
     * @param bytes
     * @return
     */
    public static short hashCode16(byte[] bytes) {
        HashFunction hf = Hashing.goodFastHash(16);
        HashCode hc = hf.hashBytes(bytes);
        return (short) (hc.asInt() % 512);
    }

    /**
     * returns an 8-bit salt value that can be used for distribution on a rowkey.  NOte that
     * it is only capable of expressing a 256 value, which would be the upper value for the
     * number of regions.
     *
     * @param bytes the bytes to use for the hash
     * @return single byte with salt value
     */
    public static byte getSalt(byte[] bytes) {
        HashFunction hf = Hashing.goodFastHash(16);
        HashCode hc = hf.hashBytes(bytes);
        byte[] hash = hc.asBytes();
        return (byte) hash[hash.length - 1];
    }

    /**
     * prepends a salt byte value to the from of the given byte[], using the byte[] as the
     * source of the hash from which the salt is taken.  NOTE - you really can't pass this
     * to a String constructor once you do this.  Its one-way.
     * @param bytes
     * @return
     */
    public static byte[] saltedRowKey(byte[] bytes) {
        byte[] dest = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, dest, 1, bytes.length);
        dest[0] = getSalt(bytes);
        return dest;
    }

    public static byte[] unsalt(byte[] bytes){
        return Arrays.copyOfRange(bytes, 1, bytes.length);
    }
}
