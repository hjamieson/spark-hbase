package hugh.spark.alpha;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Created by hughj on 12/28/16.
 *
 * Description - hard-coded to read the foobar table just to follow the instructions from Vidya's
 * webpage on accessing HBase from spark.  This example was used to figure out how to get spark on
 * the client al set up.
 */
public class ReadFoobar {
   public static void main(String[] args) {
      System.exit(new ReadFoobar().exec());
   }

   private int exec() {
      SparkConf sc = new SparkConf().setAppName("Reading Foobar");
      JavaSparkContext jsc = new JavaSparkContext(sc);

      Configuration conf = HBaseConfiguration.create();
      conf.addResource("hbase-site.xml");
      conf.set(TableInputFormat.INPUT_TABLE, "foobar");

      JavaPairRDD<ImmutableBytesWritable, Result> rdd = jsc.newAPIHadoopRDD(conf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
      System.out.printf("rows in foobar = %d%n", rdd.count());

      JavaRDD<String> keys = rdd.map(kv -> {
         String key = Bytes.toString(kv._1.get());
         return key;
      });

      keys.take(10).forEach(k -> System.out.printf("new key = %s%n", k));

      jsc.close();
      return 0;

   }
}
