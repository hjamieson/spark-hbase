package org.oclc.datamodel.rdf.hbase.ingest;

/**
 * Description - implementors of this interface promise to return a new byte array that can be used as
 * a rowkey for new items.
 * Created by jamiesoh on 1/22/17.
 */
public interface RowkeyGenerator {
    byte[] nextKey();
}
