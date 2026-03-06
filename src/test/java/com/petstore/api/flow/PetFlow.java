package com.petstore.api.flow;

import com.petstore.api.client.PetClient;
import com.petstore.api.data.model.Pet;

public class PetFlow {

    private final PetClient petClient = new PetClient();

    public Pet createAndReturnPet(Pet pet) {
        return petClient.createPet(pet)
                .then()
                .statusCode(200)
                .extract().as(Pet.class);
    }

    public void addPetAndVerify(Pet pet) {
        petClient.createPet(pet)
                .then()
                .statusCode(200);

        petClient.getPet(pet.getId())
                .then()
                .statusCode(200);
    }
}
