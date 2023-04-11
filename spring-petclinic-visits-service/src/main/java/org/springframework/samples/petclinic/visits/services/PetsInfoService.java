package org.springframework.samples.petclinic.visits.services;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.visits.dto.PetDetails;
import org.springframework.samples.petclinic.visits.dto.Pets;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;
import static java.util.stream.Collectors.joining;
@Component
@RequiredArgsConstructor
public class PetsInfoService {
    private String hostname = "http://customers-service/";

    private final WebClient.Builder webClientBuilder;

    public Mono<PetDetails> getAdditionalPetDetails(final int petId) {
        return webClientBuilder.build()
            .get()
            .uri(hostname + "owners/*/pets/{petId}", petId)
            .retrieve()
            .bodyToMono(PetDetails.class);
    }

    private String joinIds(List<Integer> petIds) {
        return petIds.stream().map(Object::toString).collect(joining(","));
    }

    void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
