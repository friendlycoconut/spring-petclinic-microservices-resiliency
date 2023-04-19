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
package org.springframework.samples.petclinic.vets.web;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.samples.petclinic.vets.dto.Studies;
import org.springframework.samples.petclinic.vets.dto.VetDetails;
import org.springframework.samples.petclinic.vets.dto.Vets;
import org.springframework.samples.petclinic.vets.model.Vet;
import org.springframework.samples.petclinic.vets.model.VetRepository;
import org.springframework.samples.petclinic.vets.services.StudyService;
import org.springframework.samples.petclinic.vets.services.VetsServiceClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Maciej Szarlinski
 */
@RequestMapping("/vets")
@RestController
@RequiredArgsConstructor
class VetResource {

    private final VetRepository vetRepository;

    private final StudyService studyService;

    private final VetsServiceClient vetsServiceClient;

    @GetMapping
    @Cacheable("vets")
    public List<Vet> showResourcesVetList() {
        return vetRepository.findAll();
    }

    @GetMapping(value = "/{vetId}")
    public Optional<Vet> getVetDetailsById(@PathVariable("vetId") @Min(1) int vetId){
        return vetRepository.findById(vetId);
    }



    private Function<Studies, VetDetails> addStudiesToVet(VetDetails vet) {

        return studies -> {
            vet.getStudyDetailsList()
                .addAll(studies.getItems().stream()
                    .filter(v -> v.getVetId() == vet.getId())
                    .toList());
            return vet;
        };
    }
}
