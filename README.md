# query_fetcher

A lightweight SPARQL interface which enables query against an input File (imports are allowed)
using a text file containing SPARQL syntax, and outputs a CSV format file.  The query itself
does not use reasoning (though i may add this in the future).  A working Jar of this code
can be obtained by downloaded sources, loading into Intellij or Eclipse and pointing the artificat
to the only class in the repository.  

Command line syntax and help looks like:

```
%: java -jar query-fetcher.jar 
usage: java -jar query-fetcher.jar  [-h] [-i <arg>] [-o <arg>] [-sparql <arg>]
 -h,--help                    print this help message and exit
 -i,--inputFile <arg>         Input Spreadsheet
 -o,--outputDirectory <arg>   Output Directory
 -sparql <arg>                designate a sparql input file for
                              processing.  This option should have an
                              inputFile and outputDirectory specified.
                              The output format is always CSV
```
