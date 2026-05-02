package com.assignment3.project.services;

import com.assignment3.project.dto.requests.DonationCreateRequest;
import com.assignment3.project.dto.responses.DonationResponse;
import com.assignment3.project.entities.Donation;
import com.assignment3.project.entities.Project;
import com.assignment3.project.entities.User;
import com.assignment3.project.mappers.DonationMapper;
import com.assignment3.project.repositories.DonationRepository;
import com.assignment3.project.repositories.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationService {
    private final DonationRepository donationRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final DonationMapper donationMapper;

    @Transactional
    public DonationResponse createDonation(DonationCreateRequest request) {
        log.info("DonationService.createDonation called projectId={} amount={}", request.getProjectId(), request.getAmount());
        
        var token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        var email = (String) token.getToken().getClaim("email");
        User donor = userService.getUserByIdOrThrow(
                userService.getUserByEmail(email).getId()
        );

        if (donor.getRole() != com.assignment3.project.enums.UserRole.DONOR) {
            throw new IllegalArgumentException("Only donors can make donations");
        }

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project with id " + request.getProjectId() + " not found"));

        Donation donation = new Donation();
        donation.setAmount(request.getAmount());
        donation.setDonor(donor);
        donation.setProject(project);

        Donation saved = donationRepository.save(donation);

        project.setCollectedAmount(project.getCollectedAmount() + request.getAmount());
        projectRepository.save(project);

        log.info("Donation created id={}, project collectedAmount updated to {}", saved.getId(), project.getCollectedAmount());
        return donationMapper.toDto(saved);
    }

    public List<DonationResponse> getDonationsByProjectId(Long projectId) {
        log.info("DonationService.getDonationsByProjectId called projectId={}", projectId);
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project with id " + projectId + " not found");
        }
        return donationRepository.findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(donationMapper::toDto)
                .toList();
    }

    public long getTotalDonationsCount() {
        log.info("DonationService.getTotalDonationsCount called");
        return donationRepository.count();
    }

    public long getDonationsSumForCurrentMonth() {
        log.info("DonationService.getDonationsSumForCurrentMonth called");
        LocalDate now = LocalDate.now();
        LocalDate monthAgo = now.minusMonths(1);
        
        LocalDateTime startDate = LocalDateTime.of(monthAgo, LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(now, LocalTime.MAX);
        
        return donationRepository.sumAmountByDateRange(startDate, endDate);
    }

    public long getDonationsSumForLastYear() {
        log.info("DonationService.getDonationsSumForLastYear called");
        LocalDate now = LocalDate.now();
        int lastYear = now.getYear() - 1;
        
        LocalDateTime startDate = LocalDateTime.of(lastYear, 1, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(lastYear, 12, 31, 23, 59, 59);
        
        return donationRepository.sumAmountByDateRange(startDate, endDate);
    }
}

