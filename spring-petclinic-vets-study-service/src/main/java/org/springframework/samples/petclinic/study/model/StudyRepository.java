package org.springframework.samples.petclinic.study.model;

import org.springframework.data.jpa.repository.JpaRepository;


public interface StudyRepository extends JpaRepository<StudyRecord, Integer> {
}
