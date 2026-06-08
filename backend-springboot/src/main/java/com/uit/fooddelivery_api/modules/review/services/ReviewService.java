package com.uit.fooddelivery_api.modules.review.services;

import com.uit.fooddelivery_api.modules.order.entities.Order;
import com.uit.fooddelivery_api.modules.order.repositories.OrderRepository;
import com.uit.fooddelivery_api.modules.review.dtos.CreateReviewDTO;
import com.uit.fooddelivery_api.modules.review.entities.Review;
import com.uit.fooddelivery_api.modules.review.repositories.ReviewRepository;
import com.uit.fooddelivery_api.modules.user.entities.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    // Bộ lọc từ ngữ thô tục đơn giản (Blacklist)
    private static final List<String> BAD_WORDS = Arrays.asList("đm", "vcl", "ngu", "chó", "cút");

    @Transactional
    public Review createReview(CreateReviewDTO dto, User customer) {
        // 1. Kiểm tra đơn hàng
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

        // 2. Bảo mật: Chỉ người đặt đơn mới được đánh giá
        if (!order.getUser().getId().equals(customer.getId())) {
            throw new RuntimeException("Bạn không có quyền đánh giá đơn hàng của người khác!");
        }

        // 3. Logic: Chỉ được đánh giá khi đơn đã giao xong
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new RuntimeException("Chỉ có thể đánh giá khi đơn hàng đã hoàn tất giao hàng!");
        }

        // 4. Logic: Mỗi đơn chỉ đánh giá 1 lần
        if (reviewRepository.existsByOrderId(order.getId())) {
            throw new RuntimeException("Đơn hàng này đã được đánh giá rồi!");
        }

        // 5. Kiểm tra số sao hợp lệ (1-5)
        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new RuntimeException("Điểm đánh giá phải từ 1 đến 5 sao!");
        }

        // 6. KIỂM DUYỆT COMMENT (Issue #21 - AI Filter cơ bản)
        if (dto.getComment() != null && !dto.getComment().trim().isEmpty()) {
            String lowerComment = dto.getComment().toLowerCase();
            for (String badWord : BAD_WORDS) {
                if (lowerComment.contains(badWord)) {
                    throw new RuntimeException("Bình luận của bạn chứa từ ngữ không phù hợp vi phạm tiêu chuẩn cộng đồng!");
                }
            }
        }

        // 7. Lưu Review
        Review review = Review.builder()
                .order(order)
                .restaurant(order.getRestaurant())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();

        return reviewRepository.save(review);
    }

    // Lấy danh sách đánh giá của 1 quán ăn để hiển thị trên app Khách
    public List<Review> getRestaurantReviews(Long restaurantId) {
        return reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId);
    }
}