package com.esparaquia.eedl;


import com.esparaquia.eedl.bean.IngestionOutputEvent;
import com.esparaquia.eedl.bean.IngestionResponse;
import com.esparaquia.eedl.domain.IngestionPropertyNames;
import com.esparaquia.eedl.exception.IngestionRequestParsingException;
import com.esparaquia.eedl.manager.IngestionManager;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

    /*
        Main class to take two operations
        1) Parse input event json file and generate a properties file with samba share properties
            -p -i input_event_json_file
            -c configuration_properties_file
            -o output_properties_file
        2) Create an ingestion response json file with original input and ingestion status properties
            -s -i input-event_json_file
            -c ingestion_status_properties_file
            -o output_status_json_file
        In case of error, the program will print out ingestion outout string with a status and message

     */

public class App
{
    public static final String WORKFLOW_ROOT_NAME="WF_ROOT";
    public static final String REQUEST_ID_NAME="REQUEST_ID";

    public static void main( String[] args ) {
        Options options = new Options();

        options.addOption( "p", false,"Process an ingestion request.")
                .addOption("s", false,"Generate an ingestion response.")
                .addOption("i", true, "Input request file.")
                .addOption("c", true, "Input config properties file.")
                .addOption("o", true, "Output file.");

        CommandLineParser parser = new DefaultParser();
        IngestionManager ingestionManager = null;
        try {

            CommandLine cmd = parser.parse(options, args);
            if (!cmd.hasOption("i")) {
                throw new IllegalArgumentException("Must specify an ingestion input file.");
            }

            String inputFilePath = cmd.getOptionValue("i");
            String inputFileString = new String(Files.readAllBytes(Paths.get(inputFilePath)));
            ingestionManager = IngestionManager.getInstance(inputFileString);

            if (!cmd.hasOption("c")) {
                throw new IllegalArgumentException("Must specify a config properties file.");
            }

            String configurationFilePath = cmd.getOptionValue("c");
            Properties configProperties = new Properties();
            try (InputStream in = new FileInputStream(configurationFilePath)) {
                configProperties.load(in);
            } catch (IOException e) {
                throw new Exception("Unable to load configuration properties file. " + e.getMessage());
            }

            if (!cmd.hasOption("o")) {
                throw new IllegalArgumentException("Must specify an ingestion output file.");
            }

            String outputFile = cmd.getOptionValue("o");
            try (OutputStream outStream = new FileOutputStream(outputFile);
                 Writer writer = new OutputStreamWriter(outStream)) {
                if (cmd.hasOption("p")) {
                    //TODO: refactor
                    String wfRootDir = System.getProperty(WORKFLOW_ROOT_NAME);
                    String requestId = System.getProperty(REQUEST_ID_NAME);

                    if (wfRootDir == null || wfRootDir.isEmpty()) {
                        throw new IllegalArgumentException("Must specify a system property " + WORKFLOW_ROOT_NAME);
                    }
                    if (requestId == null || requestId.isEmpty()) {
                        throw new IllegalArgumentException("Must specify a system property " + REQUEST_ID_NAME);
                    }

                    Properties out = ingestionManager.processIngestionRequest(configProperties);
                    String requestType;
                    requestType = out.getProperty(IngestionPropertyNames.REQUEST_TYPE);
                    if ("data".equals(requestType)) {
                        requestType="data_ingestion";
                    }
                    else {
                        requestType="metadata_update";
                    }
                    String outputStorageRoot = String.format("%s/%s/%s", wfRootDir, requestType, requestId);
                    out.setProperty(IngestionPropertyNames.SMB_DISTCP_ROOT, outputStorageRoot);
                    out.store(writer, "");
                }
                else if (cmd.hasOption("s")) {
                    //TODO: validate status code
                    int status = Integer.parseInt(configProperties.getProperty(IngestionPropertyNames.OUTPUT_STATUS));
                    String message = configProperties.getProperty(IngestionPropertyNames.OUTPUT_MESSAGE);
                    String rootDir = configProperties.getProperty(IngestionPropertyNames.OUTPUT_STORAGE_ROOT);
                    if (message == null) {
                        throw new IllegalArgumentException("Input configuration file has invalid content.");
                    }
                    IngestionOutputEvent outputEvent = ingestionManager.getIngestionResponse().getIngestionOutputEvent();
                    outputEvent.setStatus(status);
                    outputEvent.setMessage(message);
                    if (rootDir != null) {
                        outputEvent.setDataDestinationRoot(rootDir);
                    }
                    String outJson = IngestionManager.createIngestionResponseJsonString(new IngestionResponse(outputEvent));
                    writer.write(outJson);
                }
                else {
                    throw new IllegalArgumentException("Must specify correct operation to proceed.");
                }
            }
        }
        catch (Exception e) {
            // Create output in output event format
            IngestionOutputEvent errOutEvent = new IngestionOutputEvent();
            if (!(e instanceof IngestionRequestParsingException) && ingestionManager != null) {
                errOutEvent = ingestionManager.getIngestionResponse().getIngestionOutputEvent();
            }
            errOutEvent.setStatus(400);
            errOutEvent.setMessage(e.getMessage());
            String errStr = IngestionManager.createIngestionResponseJsonString(new IngestionResponse(errOutEvent));
            System.err.println(errStr);
            System.exit(1);
        }
    }
}

