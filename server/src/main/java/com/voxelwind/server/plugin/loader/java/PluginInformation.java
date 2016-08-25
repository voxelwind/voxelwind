package com.voxelwind.server.plugin.loader.java;

import java.util.ArrayList;
import java.util.List;

public class PluginInformation {
    private String id;
    private String author;
    private String version;
    private String website;
    private final String className;
    private final List<String> dependencies = new ArrayList<>();
    private final List<String> softDependencies = new ArrayList<>();

    public PluginInformation(String className) {
        this.className = className;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public List<String> getSoftDependencies() {
        return softDependencies;
    }

    public String getClassName() {
        return className;
    }
}
