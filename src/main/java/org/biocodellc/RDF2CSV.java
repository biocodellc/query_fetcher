package org.biocodellc;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.*;
import java.nio.file.Paths;

/**
 * @author rjewing
 */
public class RDF2CSV {
    // TODO: add capability to run additional models
    private static OntModelSpec modelSpec = OntModelSpec.OWL_MEM;

    private final String sparql;
    private final String inputFormat;
    private final String inputData;
    private final String outputDir;

    public RDF2CSV(String inputData, String outputDir, String sparqlFile, String inputFormat) throws IOException {
        this.inputData = inputData;
        this.outputDir = outputDir;
        this.sparql = this.readSparqlFile(sparqlFile);
        this.inputFormat = inputFormat;
    }

    public void convert() throws Exception {
        File input = new File(inputData);

        if (!input.exists()) {
            throw new Exception("Could not find input data at " + inputData);
        }

        if (input.isDirectory()) {
            for (File f : input.listFiles()) {
                switch (FilenameUtils.getExtension(f.getName()).toLowerCase()) {
                    case "ttl":
                    case "turtle":
                    case "n3":
                    case "nt":
                    case "rdf":
                        this.runQuery(f.getCanonicalPath(), this.getOutputFile(f.getCanonicalPath()));
                        break;
                    default:
                        System.out.println("skipping unknown file type " + f.getName());

                }
            }
        } else {
            this.runQuery(inputData, this.getOutputFile(inputData));
        }
    }

    private String readSparqlFile(String sparqlFile) throws IOException {
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
        return sb.toString();
    }

    /**
     * Simple method to run a query without involving entailments of the FIMSQueryBuilder
     *
     * @param inputFile
     * @param outputFile
     * @throws Exception
     */
    private void runQuery(String inputFile, String outputFile) throws IOException {
        // Create an input Stream to read input File
        InputStream in = new FileInputStream(new File(inputFile));

        // Create a file output stream to store file output
        File file = new File(outputFile);
        FileOutputStream fop = new FileOutputStream(file);


        // Create model
        // No results
        Model model = ModelFactory.createOntologyModel(modelSpec, null);
        // SLOW but works
        //Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, null);
        model.read(in, null, this.inputFormat);

        in.close();

        // Run query
        Query query = QueryFactory.create(this.sparql);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        System.out.println("    writing " + outputFile);
        ResultSetFormatter.outputAsCSV(fop, results);

        // Close up
        fop.close();
        qe.close();
    }

    private String getOutputFile(String inputData) {
        File f = new File(inputData);

        return Paths.get(this.outputDir, f.getName() + ".csv").toString();
    }
}
