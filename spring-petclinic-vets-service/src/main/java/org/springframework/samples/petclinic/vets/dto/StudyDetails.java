package org.springframework.samples.petclinic.vets.dto;

import lombok.Data;

@Data
public class StudyDetails {

    private int id;

    private String studyRecordName;

    private String diplomaTheme;

    private int vetId;
}
