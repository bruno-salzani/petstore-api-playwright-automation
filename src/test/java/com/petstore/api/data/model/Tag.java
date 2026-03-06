package com.petstore.api.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tag {
    private Long id;
    private String name;

    public Tag() {}

    public Tag(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static TagBuilder builder() {
        return new TagBuilder();
    }

    public static class TagBuilder {
        private Long id;
        private String name;

        public TagBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TagBuilder name(String name) {
            this.name = name;
            return this;
        }

        public Tag build() {
            return new Tag(id, name);
        }
    }
}
