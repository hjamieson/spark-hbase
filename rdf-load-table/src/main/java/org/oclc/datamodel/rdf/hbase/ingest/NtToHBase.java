package org.oclc.datamodel.rdf.hbase.ingest;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.spark.JavaHBaseContext;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.UUID;

/**
 * Created by jamiesoh on 1/22/17.
 * <p>
 * Description - reads an .nt formatted RDF source and stores it in the given HBase table.
 * args:  <path-of-nt-file> <table-name>
 */
public class NtToHBase {
    private static RowkeyGenerator keyGen;

    public static void main(String[] args) {
        // todo make sure we have a path & a table as args!
        String infile = args[0];
        String strTableName = args[1];

        SparkConf cnf = new SparkConf().setAppName("RDF Table Loader");
        JavaSparkContext jsc = new JavaSparkContext(cnf);
        Configuration hbaseConf = HBaseConfiguration.create();
        hbaseConf.addResource("hbase-site.xml");
        JavaHBaseContext jhbc = new JavaHBaseContext(jsc, hbaseConf);

        JavaRDD<String> input = jsc.textFile(infile);
        jhbc.bulkPut(input, TableName.valueOf(strTableName), NtToHBase::nt2Put);

        jsc.close();
    }

    /**
     * converts the incoming nt string into a PUT.
     * todo what if we can't convert the input?
     *
     * @param text
     * @return
     */
    private static Put nt2Put(String text) {
        Put put = new Put(nextKey());
        put.addColumn(Bytes.toBytes("d"), Bytes.toBytes("nt"), Bytes.toBytes(text));
        return put;
    }

    private static synchronized byte[] nextKey() {
        if (keyGen == null) {
            // set up mock get generator:
            keyGen = new RowkeyGenerator() {
                @Override
                public byte[] nextKey() {
                    return UUID.randomUUID().toString().getBytes();
                }
            };
        }
        return keyGen.nextKey();
    }

}
