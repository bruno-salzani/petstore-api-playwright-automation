package com.petstore.api.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Category {
    private Long id;
    private String name;

    public Category() {}

    public Category(Long id, String name) {
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

    public static CategoryBuilder builder() {
        return new CategoryBuilder();
    }

    public static class CategoryBuilder {
        private Long id;
        private String name;

        public CategoryBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CategoryBuilder name(String name) {
            this.name = name;
            return this;
        }

        public Category build() {
            return new Category(id, name);
        }
    }
}
