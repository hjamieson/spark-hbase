package hugh.spark.alpha;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.spark.JavaHBaseContext;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.NavigableMap;

/**
 * Created by hughj on 12/28/16.
 * <p>
 * Description - do we see the new hbase spark API?
 */
public class ApiCheck {
   private final String strTableName;

   public ApiCheck(String tableName) {
      this.strTableName = tableName;
   }

   public static void main(String[] args) {
      if (args.length < 1) {
         System.err.println("table name required!");
         System.exit(1);
      }
      System.exit(new ApiCheck(args[0]).exec());
   }

   private int exec() {
      SparkConf sc = new SparkConf().setAppName("Reading " + strTableName);
      JavaSparkContext jsc = new JavaSparkContext(sc);

      Configuration conf = HBaseConfiguration.create();
      conf.addResource("hbase-site.xml");

      JavaHBaseContext hbc = new JavaHBaseContext(jsc, conf);

      Scan scan = new Scan();
//      scan.setFilter(new KeyOnlyFilter());

      JavaRDD<Tuple2<ImmutableBytesWritable, Result>> rdd = hbc.hbaseRDD(TableName.valueOf(strTableName), scan);
      rdd.foreach(kv -> {
         ImmutableBytesWritable key = kv._1();
         Result result = kv._2();
         System.out.printf("key=%s",Bytes.toString(key.get()));
         if (result.isEmpty()){
            System.out.printf(", rs=empty");
         } else {
            NavigableMap<byte[], byte[]> fm = result.getFamilyMap("d".getBytes());
            fm.forEach((k,v)-> System.out.printf("{%s, %s}", Bytes.toString(k), Bytes.toString(v)));
         }
         System.out.println();
      });

      return 0;

   }
}
