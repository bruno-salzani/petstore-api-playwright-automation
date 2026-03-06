package com.petstore.api.assertions;

import com.petstore.api.data.model.Pet;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class PetAssertions extends AbstractAssert<PetAssertions, Pet> {

    public PetAssertions(Pet actual) {
        super(actual, PetAssertions.class);
    }

    public static PetAssertions assertThat(Pet actual) {
        return new PetAssertions(actual);
    }

    public PetAssertions isAvailable() {
        isNotNull();
        Assertions.assertThat(actual.getStatus()).isEqualTo("available");
        return this;
    }

    public PetAssertions hasName(String name) {
        isNotNull();
        Assertions.assertThat(actual.getName()).isEqualTo(name);
        return this;
    }
}
