package org.springframework.samples.petclinic.visits.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
public class VisitDetails {

    private Integer id = null;

    private Integer petId = null;

    private String date = null;

    private String description = null;

    private PetDetails pet = null;

}
