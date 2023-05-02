/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.api.boundary.web;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.samples.petclinic.api.application.CustomersServiceClient;
import org.springframework.samples.petclinic.api.application.VisitsServiceClient;
import org.springframework.samples.petclinic.api.dto.OwnerDetails;
import org.springframework.samples.petclinic.api.dto.PetDetails;
import org.springframework.samples.petclinic.api.dto.Visits;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Maciej Szarlinski
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gateway")
public class ApiGatewayController {

    private final CustomersServiceClient customersServiceClient;

    private final VisitsServiceClient visitsServiceClient;

    @Autowired
    private final ReactiveCircuitBreakerFactory cbFactory;

    private final CircuitBreakerConfig config = CircuitBreakerConfig
        .custom()
        .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
        .slidingWindowSize(3)
        .failureRateThreshold(20.0f)
        .build();

    @GetMapping(value = "owners/{ownerId}")
    public Mono<OwnerDetails> getOwnerDetails(final @PathVariable int ownerId) {
        return customersServiceClient.getOwner(ownerId)
            .flatMap(owner ->
                visitsServiceClient.getVisitsForPets(owner.getPetIds())
                    .transform(it -> {
                        ReactiveCircuitBreaker cb = cbFactory.create("getOwnerDetails");
                        return cb.run(it, throwable -> emptyVisitsForPets());
                    })
                    .map(addVisitsToOwner(owner))
            );

    }

    @GetMapping(value = "owners/experiment/{ownerId}")
    public Mono<OwnerDetails> getOwnerDetailsExperiment(final @PathVariable int ownerId) {
        return customersServiceClient.getOwnerExperiment(ownerId)
            .flatMap(owner ->
                visitsServiceClient.getVisitsForPets(owner.getPetIds())
                    .map(addVisitsToOwner(owner))
            );
    }

    @GetMapping(value = "owners/experiment2/{ownerId}")
    public Mono<OwnerDetails> getOwnerDetailsExperiment2(final @PathVariable int ownerId) {
        return customersServiceClient.getOwnerExperiment2(ownerId)
            .flatMap(owner ->
                visitsServiceClient.getVisitsForPets(owner.getPetIds())
                    .map(addVisitsToOwner(owner))
            );
    }

    @GetMapping(value = "owners/experiment3/{ownerId}")
    public Mono<OwnerDetails> getOwnerDetailsExperiment3(final @PathVariable int ownerId) {
        return customersServiceClient.getOwnerExperiment3(ownerId)
            .flatMap(owner ->
                visitsServiceClient.getVisitsForPets(owner.getPetIds())
                    .map(addVisitsToOwner(owner))
            );
    }

    @GetMapping(value = "owners/*/pets/{petId}")
    public Mono<PetDetails> getPetDetails(final @PathVariable int petId) {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        CircuitBreaker circuitBreaker = registry.circuitBreaker("ownerPetService");

        Supplier<Mono<PetDetails>> petDetailsSupplier = circuitBreaker.decorateSupplier(() -> customersServiceClient.getPetById(petId));


        return petDetailsSupplier.get()
            .flatMap(pet ->
                            visitsServiceClient.getVisitsForOnePet(pet.getId())
            .map(addVisitToPet(pet)));

    }

    private Function<Visits, OwnerDetails> addVisitsToOwner(OwnerDetails owner) {
        return visits -> {
            owner.getPets()
                .forEach(pet -> pet.getVisits()
                    .addAll(visits.getItems().stream()
                        .filter(v -> v.getPetId() == pet.getId())
                        .toList())
                );
            return owner;
        };
    }

    private Function<Visits, PetDetails> addVisitToPet(PetDetails pet){
        return visit -> {
           pet.getVisits()
                    .addAll(visit.getItems().stream()
                        .filter(v -> v.getPetId() == pet.getId())
                        .toList());
            return pet;
        };
    }

    private Mono<Visits> emptyVisitsForPets() {
        return Mono.just(new Visits());
    }

    private Mono<OwnerDetails> emptyOwnerDetails() {
        return Mono.just(new OwnerDetails());
    }

    private void decorateOwnerPets(){

    }
}
