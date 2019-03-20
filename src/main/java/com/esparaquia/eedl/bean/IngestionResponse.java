package com.esparaquia.eedl.bean;

public class IngestionResponse {

    private IngestionOutputEvent ingestionOutputEvent;

    public IngestionResponse(IngestionOutputEvent ingestionOutputEvent) {
        this.ingestionOutputEvent = ingestionOutputEvent;
    }

    public IngestionOutputEvent getIngestionOutputEvent() { return ingestionOutputEvent; }

    public void setIngestionOutputEvent(IngestionOutputEvent ingestionOutputEvent){
        this.ingestionOutputEvent = ingestionOutputEvent;
    }
}
