package com.petstore.api.http;

public enum Endpoint {
    PET("/pet"),
    PET_BY_ID("/pet/{petId}"),
    FIND_BY_STATUS("/pet/findByStatus"),
    FIND_BY_TAGS("/pet/findByTags");

    private final String path;

    Endpoint(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
