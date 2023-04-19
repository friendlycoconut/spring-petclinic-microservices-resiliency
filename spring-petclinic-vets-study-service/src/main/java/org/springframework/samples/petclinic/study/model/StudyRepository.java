package org.springframework.samples.petclinic.study.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface StudyRepository extends JpaRepository<StudyRecord, Integer> {
    List<StudyRecord> findByVetId(int vetId);
}
