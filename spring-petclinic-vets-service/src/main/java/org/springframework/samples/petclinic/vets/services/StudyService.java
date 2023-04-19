package org.springframework.samples.petclinic.vets.services;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.vets.dto.Studies;
import org.springframework.samples.petclinic.vets.dto.StudyDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;


@Component
@RequiredArgsConstructor
public class StudyService {
    private final WebClient.Builder webClientBuilder;

    public Mono<StudyDetails> getStudyDetails(final int studyRecordId) {
        return webClientBuilder.build().get()
            .uri("http://study-service/studies/{studyRecordId}", studyRecordId)
            .retrieve()
            .bodyToMono(StudyDetails.class);
    }

    public Mono<Studies> getStudyDetailsByVetId(final int vetId) {


        return webClientBuilder.build().get()
            .uri("http://study-service/studies/vets/{studyRecordId}", vetId)
            .retrieve()
            .bodyToMono(Studies.class);
    }

}
