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
import scala.Tuple2;

import java.util.NavigableMap;

/**
 * Created by hughj on 12/28/16.
 * <p>
 * Description - dumps all the cells it finds to stdout.  Definitely a rookie app.
 */
public class CellDump {
   private final String strTableName;

   public CellDump(String tableName) {
      this.strTableName = tableName;
   }

   public static void main(String[] args) {
      if (args.length < 1) {
         System.err.println("table name required!");
         System.exit(1);
      }
      System.exit(new CellDump(args[0]).exec());
   }

   private int exec() {
      SparkConf sc = new SparkConf().setAppName("Reading " + strTableName);
      JavaSparkContext jsc = new JavaSparkContext(sc);

      Configuration conf = HBaseConfiguration.create();
      conf.addResource("hbase-site.xml");
      conf.set(TableInputFormat.INPUT_TABLE, strTableName);

      JavaPairRDD<ImmutableBytesWritable, Result> rdd = jsc.newAPIHadoopRDD(conf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
      System.out.printf("rows in foobar = %d%n", rdd.count());

//      JavaRDD<NavigableMap<byte[], byte[]>> nav = rdd.map(kv -> kv._2).map(result -> result.getFamilyMap("d".getBytes()));
      JavaRDD<Tuple2<ImmutableBytesWritable, NavigableMap<byte[], byte[]>>> nav1 = rdd.map(kv -> new Tuple2<>(kv._1, kv._2.getFamilyMap("d".getBytes())));
      System.out.printf("nav.size = %d%n", nav1.count());
      nav1.foreach(nm -> {
         System.out.printf("%s=>", Bytes.toString(nm._1().get()));
         nm._2().forEach((a, b) -> System.out.printf("{%s:%s}", Bytes.toString(a), Bytes.toString(b)));
         System.out.println();
      });

      return 0;

   }
}
