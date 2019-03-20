package com.esparaquia.eedl.bean;

import java.util.List;

public class InputEventProperties {

    private String sambaShareServer;

    public String getSambaShareServer() {
        return sambaShareServer;
    }

    public void setSambaShareServer(String sambaShareServer) {
        this.sambaShareServer = sambaShareServer;
    }

    public String getSambaShareName() {
        return sambaShareName;
    }

    public void setSambaShareName(String sambaShareName) {
        this.sambaShareName = sambaShareName;
    }

    public List<DataSourceTargetPair> getSambaShareDirectories() {
        return sambaShareDirectories;
    }

    public void setSambaShareDirectories(List<DataSourceTargetPair> sambaShareDirectories) {
        this.sambaShareDirectories = sambaShareDirectories;
    }

    private String sambaShareName;
    private List<DataSourceTargetPair> sambaShareDirectories;

}
