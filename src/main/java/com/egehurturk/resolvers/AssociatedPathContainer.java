package com.egehurturk.resolvers;

import java.util.ArrayList;
import java.util.List;

public class AssociatedPathContainer {
    private String requestPath;
    private String mappedPath;
    public static List<String> mappedPaths = new ArrayList<>();
    public AssociatedPathContainer() {}
    public AssociatedPathContainer(String requestPath, String mappedPath) {
        this.requestPath = requestPath;
        this.mappedPath = mappedPath;
        mappedPaths.add(mappedPath);
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getMappedPath() {
        return mappedPath;
    }

    public void setMappedPath(String mappedPath) {
        this.mappedPath = mappedPath;
    }

}
