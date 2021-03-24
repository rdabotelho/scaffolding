package com.m2r.scaffolding.console.dto;

public class ScaffoldingTemplate {

    private String name;
    private String scope;
    private String fileName;
    private String outputFileName;
    private String group;
    private String createIf = "true";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCreateIf() {
        return createIf;
    }

    public void setCreateIf(String createIf) {
        this.createIf = createIf;
    }

}
