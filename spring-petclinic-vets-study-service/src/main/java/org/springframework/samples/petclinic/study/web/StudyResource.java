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
package org.springframework.samples.petclinic.study.web;


import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.samples.petclinic.study.model.StudyRecord;
import org.springframework.samples.petclinic.study.model.StudyRepository;
import org.springframework.samples.petclinic.study.services.StudyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 */
@RequestMapping("/studies")
@RestController
@Timed("petclinic.study")
@RequiredArgsConstructor
@Slf4j
class StudyResource {

    private final StudyRepository studyRepository;

    @Autowired
    private StudyService studyService;

    /**
     * Create study record
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudyRecord createStudyRecord(@Valid @RequestBody StudyRecord studyRecord) {
        return studyRepository.save(studyRecord);
    }

    /**
     * Read single study record
     */
    @GetMapping(value = "/{studyRecordId}")
    public Optional<StudyRecord> findById(@PathVariable("studyRecordId") @Min(1) int studyRecordId) {
        return studyRepository.findById(studyRecordId);
    }
    public String fallbackAfterRetry(Exception ex) {
        return "all retries have exhausted";
    }
    /**
     * Read List of Study records
     */
    @GetMapping
    public List<StudyRecord> findAll() {
        return studyService.getAllStudyRecords();
    }

    /**
     * Update Record
     */
    @PutMapping(value = "/{studyRecordId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRecord(@PathVariable("recordId") @Min(1) int recordId, @Valid @RequestBody StudyRecord recordRequest) {
        final Optional<StudyRecord> record = studyRepository.findById(recordId);
        final StudyRecord studyModel = record.orElseThrow(() -> new ResourceNotFoundException("Record "+recordId+" not found"));

        // This is done by hand for simplicity purpose. In a real life use-case we should consider using MapStruct.
        studyModel.setRecordName(recordRequest.getRecordName());
        studyModel.setDiplomaTheme(recordRequest.getDiplomaTheme());
        studyModel.setVet_id(studyModel.getVet_id());

        log.info("Saving studyModel {}", studyModel);
        studyRepository.save(studyModel);
    }




}
