package com.example.websecurity.api;

import com.example.websecurity.api.dto.MovieResponse;
import com.example.websecurity.api.dto.ReviewResponse;
import com.example.websecurity.api.dto.UpdateReviewRequest;
import com.example.websecurity.facade.ReviewFacade;
import com.example.websecurity.persistence.Review;
import com.example.websecurity.persistence.User;
import com.example.websecurity.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping()
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "Bearer Authentication")
public class ReviewController {

    private final ReviewFacade reviewFacade;
    private final ReviewService reviewService;

    @Operation(summary = "Get review by id", description = "Get review by id")
    @GetMapping("/user/{userId}/review/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(
            @PathVariable Long userId,
            @PathVariable Long reviewId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        if (!user.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        log.info("Review Controller: User {} requested a review with id {}", user.getEmail(), reviewId);
        ReviewResponse reviewResponse = reviewFacade.getReviewByIdForUser(reviewId, user.getId());
        return ResponseEntity.ok(reviewResponse);
    }

    @Operation(summary = "Update review by id", description = "Update review by id")
    @PutMapping("/user/{userId}/review/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReviewById(
            @PathVariable Long userId,
            @PathVariable Long reviewId,
            @RequestBody UpdateReviewRequest updateReviewRequest,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        if (!user.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        log.info("Review Controller: User {} requested an update for review with id {}", user.getEmail(), reviewId);
        Review updatedReview = new Review();
        updatedReview.setRating(updateReviewRequest.getRating());
        updatedReview.setReviewText(updateReviewRequest.getReviewText());
        ReviewResponse reviewResponse = reviewFacade.updateReviewForUser(reviewId, user.getId(), updateReviewRequest);
        return ResponseEntity.ok(reviewResponse);
    }

    @Operation(summary = "Get all reviews for user", description = "Get all reviews for user")
    @GetMapping("/user/{userId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviews(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        if (!user.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        log.info("Review Controller: User {} requested reviews", user.getEmail());
        List<ReviewResponse> reviewResponses = reviewFacade.getReviewsForUser(userId);
        return ResponseEntity.ok(reviewResponses);
    }
}
