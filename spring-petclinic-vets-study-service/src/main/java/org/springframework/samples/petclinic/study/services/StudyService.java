package org.springframework.samples.petclinic.study.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.samples.petclinic.study.model.StudyRecord;
import org.springframework.samples.petclinic.study.model.StudyRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudyService {
    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String SERVICE_NAME = "study-service";



    public List<StudyRecord> getAllStudyRecords() {
        System.out.println(" Making a request to " + SERVICE_NAME + " at :" + LocalDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<StudyRecord> studyRecordList = new ArrayList<>();
            studyRecordList = studyRepository.findAll();
        return studyRecordList;
    }


    public Optional<StudyRecord> getStudyRecordById(int studyRecordId) {
        System.out.println(" Making a request to " + SERVICE_NAME + " at :" + LocalDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final Optional<StudyRecord> optionalStudyRecord = studyRepository.findById(studyRecordId);

        return optionalStudyRecord;
    }


    public List<StudyRecord> getDefaultLoans(Exception e) {
        System.out.println("Retry exception");
        return new ArrayList<>();
    }
}
