package com.m2r.scaffolding.console.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

public class ScaffoldingProject {

    private String projectName;
    private String basePackage;
    private String configDir;
    private String outputDir;
    private Boolean createOneToManyClass = true;
    private List<ScaffoldingTemplate> templates = new ArrayList<>();

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getConfigDir() {
        return configDir;
    }

    public void setConfigDir(String configDir) {
        this.configDir = configDir;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public Boolean getCreateOneToManyClass() {
        return createOneToManyClass;
    }

    public void setCreateOneToManyClass(Boolean createOneToManyClass) {
        this.createOneToManyClass = createOneToManyClass;
    }

    public List<ScaffoldingTemplate> getTemplates() {
        return templates;
    }

    public void setTemplates(List<ScaffoldingTemplate> templates) {
        this.templates = templates;
    }

}
