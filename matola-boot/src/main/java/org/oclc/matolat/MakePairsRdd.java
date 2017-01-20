package org.oclc.matolat;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

/**
 * Created by hughj on 1/19/17.
 * <p>
 * Example of how to read an HDFS file and split it into pairs.  There are several ways to do this,
 * so don't take this as gospel.  YMMV.
 */
public class MakePairsRdd {
   public static void main(String[] args) {
      if (args.length == 0) {
         throw new IllegalArgumentException("path to input file required");
      }

      SparkConf sConf = new SparkConf().setAppName("Split Lines");
      JavaSparkContext jsc = new JavaSparkContext(sConf);

      /*
      assume your input line is in the format:
      key \t some-really-interesting-data-as-value

      (1) create the RDD to get the raw lines
      (2) map each line into a pair (k,v)
       */

      JavaRDD<String> lines = jsc.textFile(args[0]);

      /*
      this is the really UGLY way to code it if you are a lame java dork:

      JavaPairRDD<String,String> pairs = lines.mapToPair(new PairFunction<String, String, String>() {
         public Tuple2<String, String> call(String s) throws Exception {
            String[] split = s.split("\\t");
            return new Tuple2<>(split[0], split[1]);
         }
      });
       */

      /* if you are one of the cool kids, do it this way using lambda & compiler inference: */

      JavaPairRDD<String, String> pairs = lines.mapToPair(s -> {
         String[] tokens = s.split("\t");
         return new Tuple2<>(tokens[0], tokens[1]);
      });

      /*
      now you have your RDD of (k,v) that you can process like you normally would.  Lets
      just get a count of distinct values:
       */

      System.out.println("number of distinct values = "+ pairs.values().distinct().count());

   }
}
