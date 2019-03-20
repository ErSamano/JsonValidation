package com.esparaquia.eedl.bean;

public class IngestionInputEvent {

    private String name;
    private String resource;
    private String operation;
    private ResourceId resourceId;
    private InputEventProperties properties;
    private DomainEntity domainEntity;
    private MetaData metadata;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public ResourceId getResourceId() {
        return resourceId;
    }

    public void setResourceId(ResourceId resourceId) {
        this.resourceId = resourceId;
    }

    public InputEventProperties getProperties() {
        return properties;
    }

    public void setProperties(InputEventProperties properties) {
        this.properties = properties;
    }

    public DomainEntity getDomainEntity() {
        return domainEntity;
    }

    public void setDomainEntity(DomainEntity domainEntity) {
        this.domainEntity = domainEntity;
    }

    public MetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(MetaData metadata) {
        this.metadata = metadata;
    }

}
