package org.springframework.samples.petclinic.vets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VetDetails {
    private Integer id = null;

    private String firstName = null;

    private String secondName = null;

    private Set<SpecialtyDetails> specialtyDetailsSet = new HashSet<>();

    private List<StudyDetails> studyDetailsList = new ArrayList<>();

}
