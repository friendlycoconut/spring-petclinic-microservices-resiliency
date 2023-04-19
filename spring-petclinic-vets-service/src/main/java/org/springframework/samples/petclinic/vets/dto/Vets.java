package org.springframework.samples.petclinic.vets.dto;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class Vets {
    private List<VetDetails> items = new ArrayList<>();
}
