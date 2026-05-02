package com.assignment3.project.controllers;

import com.assignment3.project.dto.requests.DonationCreateRequest;
import com.assignment3.project.dto.responses.DonationResponse;
import com.assignment3.project.services.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.version}/donations")
public class DonationController {
    private final DonationService donationService;

    @PostMapping()
    public ResponseEntity<DonationResponse> createDonation(@RequestBody @Valid DonationCreateRequest request) {
        DonationResponse donation = donationService.createDonation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(donation);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalDonationsCount() {
        long count = donationService.getTotalDonationsCount();
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/month-sum")
    public ResponseEntity<Map<String, Long>> getDonationsSumForCurrentMonth() {
        long sum = donationService.getDonationsSumForCurrentMonth();
        return ResponseEntity.ok(Map.of("sum", sum));
    }

    @GetMapping("/last-year-sum")
    public ResponseEntity<Map<String, Long>> getDonationsSumForLastYear() {
        long sum = donationService.getDonationsSumForLastYear();
        return ResponseEntity.ok(Map.of("sum", sum));
    }
}

