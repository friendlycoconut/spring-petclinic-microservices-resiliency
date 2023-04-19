package org.springframework.samples.petclinic.vets.services;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.vets.dto.VetDetails;
import org.springframework.samples.petclinic.vets.dto.Vets;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VetsServiceClient {
    private String hostname = "http://vets-service/";

    private final WebClient.Builder webClientBuilder;

    public Mono<Vets> getAllVets() {
        return webClientBuilder.build()
            .get()
            .uri(hostname + "vets")
            .retrieve()
            .bodyToMono(Vets.class);
    }

    public Mono<VetDetails> getVetById(final int vetId) {
        return webClientBuilder.build().get()
            .uri("http://vets-service/vets/{vetId}", vetId)
            .retrieve()
            .bodyToMono(VetDetails.class);
    }

}
