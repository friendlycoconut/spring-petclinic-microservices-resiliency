package org.springframework.samples.petclinic.visits.dto;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class Pets {

    private List<PetDetails> items = new ArrayList<>();

}
