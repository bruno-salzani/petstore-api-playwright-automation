package com.petstore.api.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pet {
    private Long id;
    private Category category;
    private String name;
    private List<String> photoUrls;
    private List<Tag> tags;
    private String status;

    public Pet() {}

    public Pet(Long id, Category category, String name, List<String> photoUrls, List<Tag> tags, String status) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.photoUrls = photoUrls;
        this.tags = tags;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static PetBuilder builder() {
        return new PetBuilder();
    }

    public static class PetBuilder {
        private Long id;
        private Category category;
        private String name;
        private List<String> photoUrls;
        private List<Tag> tags;
        private String status;

        public PetBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PetBuilder category(Category category) {
            this.category = category;
            return this;
        }

        public PetBuilder name(String name) {
            this.name = name;
            return this;
        }

        public PetBuilder photoUrls(List<String> photoUrls) {
            this.photoUrls = photoUrls;
            return this;
        }

        public PetBuilder tags(List<Tag> tags) {
            this.tags = tags;
            return this;
        }

        public PetBuilder status(String status) {
            this.status = status;
            return this;
        }

        public Pet build() {
            return new Pet(id, category, name, photoUrls, tags, status);
        }
    }
}
