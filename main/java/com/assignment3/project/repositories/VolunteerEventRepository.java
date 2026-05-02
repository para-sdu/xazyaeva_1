package com.assignment3.project.repositories;

import com.assignment3.project.entities.VolunteerEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VolunteerEventRepository extends JpaRepository<VolunteerEvent, Long> {
}

