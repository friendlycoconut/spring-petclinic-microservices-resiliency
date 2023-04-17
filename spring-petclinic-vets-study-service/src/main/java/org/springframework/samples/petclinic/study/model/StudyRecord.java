package org.springframework.samples.petclinic.study.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.core.style.ToStringCreator;

import java.util.*;


/**
 * Simple JavaBean domain object representing a study record of vet.
 *
 */
@Entity
@Table(name = "study")
public class StudyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Integer id;

    @Getter
    @Setter
    @Column(name = "record_name")
    @NotBlank
    private String recordName;

    @Getter
    @Setter
    @Column(name = "diploma_theme")
    @NotBlank
    private String diplomaTheme;


    @Getter
    @Setter
    @Column(name = "vet_id")
    @NotBlank
    private int vet_id;


    @Override
    public String toString() {
        return "StudyRecord{" +
            "id=" + id +
            ", recordName='" + recordName + '\'' +
            ", diplomaTheme='" + diplomaTheme + '\'' +
            ", vet_id=" + vet_id +
            '}';
    }
}
