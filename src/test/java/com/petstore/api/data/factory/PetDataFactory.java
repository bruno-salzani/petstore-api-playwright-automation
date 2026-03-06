package com.petstore.api.data.factory;

import com.github.javafaker.Faker;
import com.petstore.api.data.model.Category;
import com.petstore.api.data.model.Pet;
import com.petstore.api.data.model.Tag;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class PetDataFactory {
    private static final ThreadLocal<Faker> FAKER = ThreadLocal.withInitial(Faker::new);

    private PetDataFactory() {
    }

    public static Pet newPet() {
        long petId = generateId();
        long epoch = System.currentTimeMillis() / 1000;
        return Pet.builder()
                .id(petId)
                .name(FAKER.get().dog().name())
                .status("available")
                .category(Category.builder().id(1L).name("dogs").build())
                .photoUrls(List.of(FAKER.get().internet().url()))
                .tags(List.of(
                        Tag.builder().id(1L).name("friendly").build(),
                        Tag.builder().id(2L).name("automation-" + epoch).build()
                ))
                .build();
    }

    public static Pet minimalValidPet() {
        long epoch = System.currentTimeMillis() / 1000;
        return Pet.builder()
                .id(generateId())
                .name("pet-" + FAKER.get().animal().name())
                .photoUrls(List.of("https://example.com/pet.jpg"))
                .status("available")
                .tags(List.of(Tag.builder().id(2L).name("automation-" + epoch).build()))
                .build();
    }

    public static Pet invalidMissingName() {
        long epoch = System.currentTimeMillis() / 1000;
        return Pet.builder()
                .id(generateId())
                .photoUrls(List.of("https://example.com/pet.jpg"))
                .status("available")
                .tags(List.of(Tag.builder().id(2L).name("automation-" + epoch).build()))
                .build();
    }

    public static Pet boundaryLongName() {
        String longName = FAKER.get().lorem().characters(128);
        long epoch = System.currentTimeMillis() / 1000;
        return Pet.builder()
                .id(generateId())
                .name(longName)
                .photoUrls(List.of("https://example.com/pet.jpg"))
                .status("available")
                .tags(List.of(Tag.builder().id(2L).name("automation-" + epoch).build()))
                .build();
    }

    public static Pet updatedPet(Pet basePet) {
        return Pet.builder()
                .id(basePet.getId())
                .name(basePet.getName())
                .status("sold")
                .category(basePet.getCategory())
                .photoUrls(basePet.getPhotoUrls())
                .tags(basePet.getTags())
                .build();
    }

    public static long nonExistentPetId() {
        return ThreadLocalRandom.current().nextLong(900_000_000, 999_999_999);
    }

    private static long generateId() {
        String seed = System.getProperty("petstore.fakerSeed");
        long rand;
        if (seed != null) {
            long s = Long.parseLong(seed);
            java.util.Random r = new java.util.Random(s);
            rand = 100_000L + Math.abs(r.nextLong() % 900_000L);
        } else {
            rand = ThreadLocalRandom.current().nextLong(100_000, 999_999);
        }
        long time = System.currentTimeMillis() % 1_000_000;
        return Long.parseLong(String.valueOf(rand).substring(0, 3) + String.valueOf(time).substring(0, 3));
    }
}
