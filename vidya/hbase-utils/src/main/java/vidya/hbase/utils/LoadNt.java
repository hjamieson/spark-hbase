package vidya.hbase.utils;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.UUID;

/**
 * Created by hughj on 12/10/16.
 * <p>
 * Reads data from input file and writes it to HBase table
 */
public class LoadNt {
   private static final Logger LOG = LoggerFactory.getLogger(LoadNt.class);
   public static final byte[] CF_DATA = "d".getBytes();
   private HashFunction hf = Hashing.murmur3_32();


   public static void main(String[] args) throws IOException {
      System.exit(new LoadNt().load(args[0], args[1]));
   }

   public int load(String fileName, String szTableName) throws IOException {
      int rc = 0;
      Configuration conf = HBaseConfiguration.create();
      Connection con = ConnectionFactory.createConnection();
      TableName tableName = TableName.valueOf(szTableName);
      Table table = con.getTable(tableName);

      FileSystem fs = FileSystem.get(URI.create(fileName), conf);
      long recordsInserted = 0;
      long recordsRead = 0;
      try (BufferedReader bis = new BufferedReader(new InputStreamReader(fs.open(new Path(fileName))))) {
         String inputLine = null;
         while ((inputLine = bis.readLine()) != null) {
            recordsRead++;
            table.put(asPut(inputLine, table));
            recordsInserted++;
         }
         LOG.info("total records read/inserted = {} / {}", recordsRead, recordsInserted);
      } finally {
         table.close();
         con.close();
      }
      return rc;
   }

   private Put asPut(String inputLine, Table table) {
      NT t = new NT(inputLine);
      Put put = new Put(nextKey(inputLine.getBytes()).getBytes());
      put.addColumn(CF_DATA, "s".getBytes(), Bytes.toBytes(t.getSubject()));
      put.addColumn(CF_DATA, "p".getBytes(), Bytes.toBytes(t.getPredicate()));
      put.addColumn(CF_DATA, "o".getBytes(), Bytes.toBytes(t.getObject().getBody()));
      put.addColumn(CF_DATA, "t".getBytes(), Bytes.toBytes(t.getObject().getType().toString()));

      return put;
   }

   private String nextKey(byte[] data) {
      HashCode hashCode = hf.hashBytes(data);
      return hashCode.toString();
   }

}
