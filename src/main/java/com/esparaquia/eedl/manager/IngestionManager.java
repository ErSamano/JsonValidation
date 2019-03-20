package com.esparaquia.eedl.manager;

import com.esparaquia.eedl.bean.IngestionOutputEvent;
import com.esparaquia.eedl.bean.IngestionRequest;
import com.esparaquia.eedl.bean.IngestionResponse;
import com.esparaquia.eedl.bean.InputEventProperties;
import com.esparaquia.eedl.domain.IngestionPropertyNames;
import com.esparaquia.eedl.exception.IngestionRequestParsingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Properties;

public class IngestionManager {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private IngestionResponse ingestionResponse;
    private IngestionRequest ingestionRequest;
    private IngestionManager(IngestionRequest ingestionRequest, IngestionResponse ingestionResponse) {
        this.ingestionRequest = ingestionRequest;
        this.ingestionResponse = ingestionResponse;
    }

    public static IngestionManager getInstance(String ingestionRequestString) throws IngestionRequestParsingException {
        try {
            IngestionRequest ingestionReq = objectMapper.readValue(ingestionRequestString, IngestionRequest.class);
            IngestionOutputEvent ingestionOutputEvent = new IngestionOutputEvent();
            ingestionOutputEvent.setIngestionInputEvent(ingestionReq.getIngestionInputEvent());
            return new IngestionManager(ingestionReq, new IngestionResponse(ingestionOutputEvent));
        }
        catch (Exception e)
        {
            throw new IngestionRequestParsingException("Unable to parse the ingestion request. " + e.getMessage());
        }
    }

    public final Properties processIngestionRequest(Properties workflowProperties) throws IllegalArgumentException {
        Properties properties = new Properties();
        try {
            InputEventProperties inputEventProperties = ingestionRequest.getIngestionInputEvent().getProperties();
            properties.putAll(workflowProperties);


            //TODO: validations, check empty strings, check list size, check resource type

            properties.setProperty(IngestionPropertyNames.SMB_SHARE_SERVER, inputEventProperties.getSambaShareServer());
            properties.setProperty(IngestionPropertyNames.SMB_SHARE_NAME, inputEventProperties.getSambaShareName());
            properties.setProperty(IngestionPropertyNames.SMB_SHARE_DIRS_SOURCE, inputEventProperties.getSambaShareDirectories().get(0).getSource());
            properties.setProperty(IngestionPropertyNames.SMB_SHARE_DIRS_TARGET, (inputEventProperties.getSambaShareDirectories().get(0).getTarget()).replace("\\","/")+"\"");
            properties.setProperty(IngestionPropertyNames.REQUEST_TYPE, ingestionRequest.getIngestionInputEvent().getResource());
            ingestionResponse.getIngestionOutputEvent().setDataDestinationRoot(workflowProperties.getProperty(IngestionPropertyNames.OUTPUT_STORAGE_ROOT));
        }
        catch (Exception e) {
            throw new IllegalArgumentException(String.format("Error processing ingestion request: %s", e.getMessage()));
        }
        return properties;
    }

    public final IngestionResponse getIngestionResponse() {
        return ingestionResponse;
    }

    public static String createIngestionResponseJsonString(IngestionResponse ingestionResponse) {

        String resultJsonString = "";
        try {
            resultJsonString = objectMapper.writeValueAsString(ingestionResponse);
        }
        catch (Exception e) {
            //TODO: store hardcoded string outside
            resultJsonString = "{ \"ingestionOutputEvent\" : {\"status\" : 500, \"message\" : \"Unable to process an ingestion response.\" } }";
        }
        return resultJsonString;
    }

}
