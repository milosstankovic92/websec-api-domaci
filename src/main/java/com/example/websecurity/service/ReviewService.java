package com.example.websecurity.service;

import com.example.websecurity.exception.WebSecMissingDataException;
import com.example.websecurity.persistence.Review;
import com.example.websecurity.persistence.ReviewRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static lombok.AccessLevel.PACKAGE;

@Service
@AllArgsConstructor(access = PACKAGE)
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;


    public Review getReviewById(Long id) {
        return reviewRepository.findById(id).orElseThrow(() -> new WebSecMissingDataException("Review with id " + id + " not found"));
    }

    public Review updateReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    public Review getReviewByIdForUser(Long reviewId, Long userId) {
        return reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new WebSecMissingDataException("Forbidden"));
    }

    public Review updateReviewForUser(Long reviewId, Long userId, Review updatedReview) {
        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new WebSecMissingDataException("Forbidden"));

        // Update fields
        review.setRating(updatedReview.getRating());
        review.setReviewText(updatedReview.getReviewText());

        return reviewRepository.save(review);
    }
}
