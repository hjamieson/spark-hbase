package vidya.hbase.utils;

import org.apache.commons.cli.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create a table in HBase.  This is a demo job only.
 * Created by hughj on 12/4/16.
 */
public class CreateTable {
   private static final Logger LOG = LoggerFactory.getLogger(CreateTable.class);

   public static final String OPT_TABLE = "t";
   public static final String OPT_FORCE = "f";

   public static void main(String[] args) throws Exception {
      System.exit(new CreateTable().run(args));
   }

   public int run(String[] args) throws Exception {
      CommandLineParser cli = new PosixParser();
      CommandLine cl;
      try {
         cl = cli.parse(getOpts(), args);
         if (!cl.hasOption(OPT_TABLE)) {
            throw new ParseException("no table name provided");
         }
      } catch (ParseException e) {
         System.err.println("error: " + e.getMessage());
         new HelpFormatter().printHelp("CreateTable", getOpts());
         return 1;
      }

      checkClassPath();

      String tableName = cl.getOptionValue(OPT_TABLE);
      LOG.info("creating table: " + tableName);

      makeTable(tableName, cl.hasOption(OPT_FORCE));

      return 0;
   }

   private int makeTable(String tableName, boolean noForce) throws Exception {
      Configuration conf = HBaseConfiguration.create();
      TableName tName = TableName.valueOf(tableName);
      Connection conn = ConnectionFactory.createConnection();
      Admin admin = conn.getAdmin();
      boolean preExists = admin.tableExists(TableName.valueOf(tableName));
      LOG.info("table {} {} exist", tableName, preExists ? "does" : "does not");

      if (preExists && !noForce) {
         LOG.error("table exists and FORCE option not specified; abend!");
         return 1;
      }

      if (preExists){
         admin.disableTable(tName);
         admin.deleteTable(tName);
         LOG.info("existing table {} deleted", tableName);
      }

      HTableDescriptor table = new HTableDescriptor(tName);
      table.addFamily(new HColumnDescriptor("d"));
      admin.createTable(table);
      return 0;
   }

   /**
    * tests for the existence of the hbase-site.xml conf in the classpath
    */
   private static void checkClassPath() {
      if (CreateTable.class.getResourceAsStream("/hbase-site.xml") == null) {
         throw new RuntimeException("hbase-site.xml is not available");
      }
   }

   public static Options getOpts() {
      Options opts = new Options();
      opts.addOption(OPT_TABLE, true, "name of table");
      opts.addOption(OPT_FORCE, false, "force table create");
      return opts;
   }
}
