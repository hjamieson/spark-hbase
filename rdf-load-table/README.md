# RDF Codons
Various experiments in RDF begin by reading raw RDF data and loading it either to HDFS or HBase.  Loading RDF
into a pre-digested format that can be easily ported to many storage formats makes it easier for
us to read, load, and examine RDF data.

To make this simple, we will read all formats of RDF and transform each statement into a **codon**.  A **codon** 
is an RDF statement (s-p-o) with a key and optional metadata.  Codons can be stored in either HDFS or HBase 
and can be addressed individually.  In addition, we can assign certain metadata to each
codon that will be useful for analysis and processing in a later step.

## Input Formats
We will assume that input files come in various shapes and sizes containing RDF statements in 
some serialization format.  Here are some models we will expect to parse.  There are basically 
2 common formats for receiving RDF data:

1. prescribed XML, TTL, & NT formats, where the entire file must be read whole, and
2. record-ized files, where each line in the file is an independent entity.  Each line is processed individually.

When reading normal form, we will read the entire dataset into a model and convert it into **codons**.  Each 
generated codon will receive a unique key by which it can be addressed.

When reading recordized files, each line will be read and modeled individually.  If the model portion of
each line contains mutiple statements a codon will be emmitted for each one.

- standard RDF file (xml, ttl, nt).  Assumes file cannot be split by InputFormat.
- single row with RDF blob (no key-value).  Assumes each blob is RDFXML or TTL.  NOT .nt.
- k-v format: each row has a key, a separator, and an RDF blob like (1).  NOT .nt.
- .nt format: each row is a .nt statement with a terminal (.).
- key + .nt : like .nt, but each row has a key associated with it.
 
## Key Handling
 If the input format you are reading is one of the key+ formats, the tool will forward the 
 key onto the final store of the item.  For example, when we store in HBase, this key will become 
 the rowkey (albeit salted for distribution).  For HDFS, we will replicate the input format and
 place the key with the value.
 
## Format Extension Handling
 .ext | handling
 ---- | --------
 .nt  | standard .nt handler; one stmt per line.  A new key will be generated.
 .xml | standard RDFXML.  Entire file will be loaded into a model and turned into statements with a new key.
 .ttl | standard ttl file.  Entire file will be loaded into a model and turned into statementss with a new key.
 .xmlk | each line is key, TAB, xml RDF
