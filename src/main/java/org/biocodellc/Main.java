package org.biocodellc;

import org.apache.commons.cli.*;

/**
 * A very simple class to take an input OWL file, run a SPARQL query over this data,
 * and return a resultset based on a SPARQL command that has been passed in.
 */
public class Main {
    // This is the default Ontology Model Specification, which does no reasoning
    public static String inputFormat = "RDF/XML";

    public static void main(String[] args) {
        // Some classes to help us
        CommandLineParser clp = new GnuParser();
        HelpFormatter helpf = new HelpFormatter();
        CommandLine cl;

        // The input file
        String inputData = "";
        String filename = "";

        String outputDirectory = "";


        // Define our commandline options
        Options options = new Options();
        options.addOption("h", "help", false, "print this help message and exit");
        options.addOption("o", "outputDirectory", true, "Output Directory");
        options.addOption("i", "inputData", true, "Input rdf file or directory of input files");
        options.addOption("sparql", true, "designate a sparql input file for processing.  " +
                "This option should have an inputData and outputDirectory specified.  " +
                "The output format is always CSV");
        options.addOption("inputFormat", true, "Available input formats: " +
                "RDF/XML, N-TRIPLE, TURTLE (or TTL) and N3.  Default is RDF/XML");

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
            inputData = cl.getOptionValue("i");
        }
        if (cl.hasOption("inputFormat")) {
            inputFormat = cl.getOptionValue("inputFormat");
        }


        // if the sparql option is specified then we are going to go ahead and just run the query and return
        // results.  No configuration file is necessary
        if (cl.hasOption("sparql")) {

            try {
                RDF2CSV converter = new RDF2CSV(inputData, outputDirectory, cl.getOptionValue("sparql"), inputFormat);
                converter.convert();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }


}
