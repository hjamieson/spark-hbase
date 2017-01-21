# rdf-load-table
Various experiments in reading raw RDF data and loading it either to HDFS or HBase.  Loading RDF
into a pre-digested format that can be easily ported to many storage formats makes it easier for
us to read, load, and examine RDF data.
To make this simple, we will read all formats of RDF and transform each statement into a codon.  Each
codon can be indexed and retrieved individually.  In addition, we can assign certain metadata to each
codon that will be useful for analysis and processing in a later step.

## Input Formats
We will assume that input files come in various shapes and sizes containing RDF statements in 
some serialization format.  Here are some models we will expect to parse:
- single row with RDF blob (no key-value).  Assumes each blob is RDFXML or TTL.  NOT .nt.
- k-v format: each row has a key, a separator, and an RDF blob like (1).  NOT .nt.
- .nt format: each row is a .nt statement with a terminal (.).
- key + .nt : like .nt, but each row has a key associated with it.
 
## Key Handling
 If the input format you are reading is one of the key+ formats, the tool will forward the 
 key onto the final store of the item.  For example, when we store in HBase, this key will become 
 the rowkey (albeit salted for distribution).  For HDFS, we will replicate the input format and
 place the key with the value.
 
## Format Extensions
 .ext | handling
 ---- | --------
 .nt  | standard .nt handler; one stmt per line, no key
 .ntk | .nt handler, each line has key prefix (tab sep)
 .xml | standard RDFXML, no key expected
 .xmlk | each line is key, TAB, xml RDF
 .ttl | standard ttl file