package org.springframework.samples.petclinic.vets.dto;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;
@Value
public class Studies {
    private List<StudyDetails> items = new ArrayList<>();
}
