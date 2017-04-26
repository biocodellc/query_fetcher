package org.biocodellc;

import org.apache.commons.cli.*;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;

import java.io.*;

/**
 * A very simple class to take an input OWL file, run a SPARQL query over this data,
 * and return a resultset based on a SPARQL command that has been passed in.
 */
public class Main {
    // This is the default Ontology Model Specification, which does no reasoning
    // TODO: add capability to run additional models
    public static OntModelSpec modelSpec = OntModelSpec.OWL_MEM;
    public static void main(String[] args) {
        // Some classes to help us
        CommandLineParser clp = new GnuParser();
        HelpFormatter helpf = new HelpFormatter();
        CommandLine cl;

        // The input file
        String inputFile = "";
        String filename = "";

        String outputDirectory = "";


        // Define our commandline options
        Options options = new Options();
        options.addOption("h", "help", false, "print this help message and exit");
        options.addOption("o", "outputDirectory", true, "Output Directory");
        options.addOption("i", "inputFile", true, "Input Spreadsheet");
        options.addOption("sparql", true, "designate a sparql input file for processing.  This option should have an inputFile and outputDirectory specified.  The output format is always CSV");

        // Create the commands parser and parse the command line arguments.
        try {
            cl = clp.parse(options, args);
        } catch (UnrecognizedOptionException e) {
            System.err.println("Error: " + e.getMessage());
            return;
        } catch (ParseException e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }

        // Help
        if (cl.hasOption("h")) {
            helpf.printHelp("java -jar query-fetcher.jar ", options, true);
            return;
        }

        // No options returns help message
        if (cl.getOptions().length < 1) {
            helpf.printHelp("java -jar query-fetcher.jar ", options, true);
            return;
        }

        // Sanitize project specification
        if (cl.hasOption("o")) {
            outputDirectory = cl.getOptionValue("o");
        }


        if (cl.hasOption("i")) {
            inputFile = cl.getOptionValue("i");
            File inputFileFile = new File(inputFile);
            filename = inputFileFile.getName();
        }


        // if the sparql option is specified then we are going to go ahead and just run the query and return
        // results.  No configuration file is necessary
        if (cl.hasOption("sparql")) {

            String outputFile = outputDirectory + filename + ".csv";
            try {
                runQuery(inputFile, outputFile, cl.getOptionValue("sparql"));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            System.out.println("    writing " + outputFile);
            return;
        }
    }

    /*
        Simple method to run a query without involving entailments of the FIMSQueryBuilder
         */
    private static void runQuery(String inputFile, String outputFile, String sparqlFile) throws Exception {
        // Create an input Stream to read input File
        InputStream in = new FileInputStream(new File(inputFile));

        // Create a file output stream to store file output
        File file = new File(outputFile);
        FileOutputStream fop = new FileOutputStream(file);

        // Read sparqlFile into String
        InputStream is = new FileInputStream(sparqlFile);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();
        while (line != null) {
            sb.append(line).append("\n");
            line = buf.readLine();
        }
        is.close();
        String queryString = sb.toString();

        // Create model
        // No results
        Model model = ModelFactory.createOntologyModel(modelSpec, null);
        // SLOW but works
        //Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, null);
        model.read(in, null);
        in.close();


        // Run query
        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();
        //ResultSetFormatter.outputAsCSV(results);
        ResultSetFormatter.outputAsCSV(fop, results);
        //ResultSetFormatter.out(System.out, results);

        // Close up
        fop.close();
        qe.close();
          
    }
}
