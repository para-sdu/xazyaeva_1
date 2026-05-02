package com.assignment3.project.repositories;

import com.assignment3.project.entities.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByProjectIdOrderByCreatedAtDesc(Long projectId);
    
    long count();
    
    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.createdAt >= :startDate AND d.createdAt <= :endDate")
    long sumAmountByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}

