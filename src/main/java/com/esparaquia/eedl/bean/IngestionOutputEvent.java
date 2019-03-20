package com.esparaquia.eedl.bean;

public class IngestionOutputEvent {

    private int status = 200;
    private String message = "OK";
    private String dataDestinationRoot;
    private IngestionInputEvent ingestionInputEvent;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDataDestinationRoot() {
        return dataDestinationRoot;
    }

    public void setDataDestinationRoot(String dataDestinationRoot) {
        this.dataDestinationRoot = dataDestinationRoot;
    }

    public IngestionInputEvent getIngestionInputEvent() {
        return ingestionInputEvent;
    }

    public static void setIngestionInputEvent(IngestionInputEvent ingestionInputEvent) {
        this.ingestionInputEvent = ingestionInputEvent;
    }
}
