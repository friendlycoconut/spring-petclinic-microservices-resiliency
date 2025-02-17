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
package org.springframework.samples.petclinic.api.application;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.api.dto.OwnerDetails;
import org.springframework.samples.petclinic.api.dto.PetDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author Maciej Szarlinski
 */
@Component
@RequiredArgsConstructor
public class CustomersServiceClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<OwnerDetails> getOwner(final int ownerId) {
        return webClientBuilder.build().get()
            .uri("http://customers-service/owners/{ownerId}", ownerId)
            .retrieve()
            .bodyToMono(OwnerDetails.class);
    }

    public Mono<PetDetails> getPetById(final int petId) {
        return webClientBuilder.build().get()
            .uri("http://customers-service/owners/*/pets/{petId}", petId)
            .retrieve()
            .bodyToMono(PetDetails.class);
    }


    public Mono<OwnerDetails> getOwnerExperiment(final int ownerId) {
        return webClientBuilder.build().get()
            .uri("http://customers-service/owners/experiment/{ownerId}", ownerId)
            .retrieve()
            .bodyToMono(OwnerDetails.class);
    }

    public Mono<OwnerDetails> getOwnerExperiment2(final int ownerId) {
        return webClientBuilder.build().get()
            .uri("http://customers-service/owners/experiment2/{ownerId}", ownerId)
            .retrieve()
            .bodyToMono(OwnerDetails.class);
    }

    @Retry(name = "retryExp3Delay")
    public Mono<OwnerDetails> getOwnerExperiment3(final int ownerId) {
        return webClientBuilder.build().get()
            .uri("http://customers-service/owners/experiment3/{ownerId}", ownerId)
            .retrieve()
            .bodyToMono(OwnerDetails.class);
    }


}
