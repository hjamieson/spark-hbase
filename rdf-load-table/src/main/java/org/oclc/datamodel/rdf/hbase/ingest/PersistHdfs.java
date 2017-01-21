package org.oclc.datamodel.rdf.hbase.ingest;

/**
 * Created by jamiesoh on 1/21/17.
 *
 * Description: reads an RDF file containing RDF statements in some serialization format.  There are a few ways
 * this could be formatter:
 * 1) single row with RDF blob (no key-value).  Assumes each blob is RDFXML or TTL.  NOT .nt.
 * 2) k-v format: each row has a key, a separator, and an RDF blob like (1).  NOT .nt.
 * 3) .nt format: each row is a .nt statement with a terminal (.).
 * 4) key + .nt : like (3), but
 */
public class PersistHdfs {
}
