package com.assignment3.project.repositories;

import com.assignment3.project.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByIsVerifiedTrue();
    
    @Query("""
        SELECT p FROM Project p
        WHERE
            p.isVerified = true
            AND (:categoryId IS NULL OR p.category.id = :categoryId)
            AND (:name IS NULL OR :name = '' OR UPPER(p.title) LIKE CONCAT('%', UPPER(:name), '%'))
    """)
    List<Project> findProjectsByCategoryAndTitleContaining(
            Long categoryId,
            String name
    );
}
