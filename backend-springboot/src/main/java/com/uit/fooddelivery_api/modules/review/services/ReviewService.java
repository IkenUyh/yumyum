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

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final com.uit.fooddelivery_api.modules.loyalty.services.LoyaltyService loyaltyService;

    /**
     * Hệ thống Regex Engine nâng cao - Cập nhật danh sách từ thô tục mới
     * Sử dụng các quy tắc loại trừ thông minh để tránh chặn nhầm từ ngữ chính thống.
     */
    private static final Pattern BAD_WORDS_PATTERN = Pattern.compile(
            // Group 1: Các từ cũ + Biến thể mở rộng
            "\\b([dđ][uúùủũụưứừửữự]*[\\s._\\-*~]*m+[aáàảãạâấầẩẫậăắằẳẵặ4]*)\\b|" + // đm, dm, du ma, duma, dme
                    "\\bv[\\s._\\-*~]*[ck][\\s._\\-*~]*l+\\b|" +                     // vcl, vkl, v.c.l, v_c_l
                    "\\bn[\\s._\\-*~]*g+[\\s._\\-*~]*[uúùủũụưứừửữự]+\\b|" +           // ngu, n.g.u, nguuuuu
                    "\\bc[\\s._\\-*~]*[uúùủũụưứừửữự]+[\\s._\\-*~]*t+\\b|" +           // cút, c.u.t, cuuutt
                    "\\b[dđ][\\s._\\-*~]*[iíìỉĩị1!]+[\\s._\\-*~]*t+\\b|" +             // djt

                    // Group 2: Thằng chó, thieu nang
                    "\\b(th[ăắằẳẵặaáàảãạ]+ng+[\\s._\\-*~]*)?ch[oóòỏõọôốồổỗộơớờởỡợ0]+[s]*\\b|" + // chó, thằng chó, thằg ch0
                    "\\bth[i1!]+[e3]+u+[\\s._\\-*~]*n[a4@]+ng+\\b|" +                 // thieu nang, thiếu năng, th1eu n4ng

                    // Group 3: Xử lý cc, ncc, cl, ncl (Bắt cả viết liền lẫn cách dấu)
                    "\\b(như[\\s._\\-*~]*)?c+[\\s._\\-*~]*c+\\b|" +                   // cc, như cc, c.c
                    "\\bn+[\\s._\\-*~]*c+[\\s._\\-*~]*c+\\b|" +                       // ncc, n.c.c
                    "\\b(như[\\s._\\-*~]*)?c+[\\s._\\-*~]*l+\\b|" +                   // cl, như cl, c.l
                    "\\bn+[\\s._\\-*~]*c+[\\s._\\-*~]*l+\\b|" +                       // ncl, n.c.l

                    // Group 4: Xử lý an toàn cho "con cac", "cac" (Loại trừ dấu SẮC 'á' để không chặn nhầm từ "CÁC")
                    "\\bcon[\\s._\\-*~]*c+[aáàảãạ4]+c+\\b|" +                         // Cụm "con cac" thì có dấu gì cũng chặn
                    "\\bc+[aàảãạ4]+c+\\b|" +                                         // Đi lẻ thì chặn "cac",  "cạcc" nhưng THẢ "các" đi tự do

                    // Group 5: Xử lý an toàn cho "vai lon", "con di" (Bảo vệ từ "lon nước", "lon bia" và từ "đi lại")
                    "\\bl[ồốổỗộờớởỡợ]+n+\\b|" +                                       // Chặn tuyệt đối nếu viết đúng dấu nhạy cảm: lo`n
                    "\\b(v[aáàảãạ]*i+|con|thằng)[\\s._\\-*~]*l[oóòỏõọôốồổỗộơớờởỡợ0]+n+\\b|" + // Đi kèm bổ ngữ: vãi lon, vai lon, con lon thì chặn
                    "\\b[dđ][íỉĩị]+\\b|" +                                           // di~ (Có dấu nhạy cảm thì chặn)
                    "\\bcon[\\s._\\-*~]*[dđ][iíìỉĩị1!]+\\b|" +                         // Cụm "con di",

                    // Group 6: English Bad words
                    "\\bf+[\\s._\\-*~]*[uuvV4]+[\\s._\\-*~]*c+[\\s._\\-*~]*k+\\b|" +   // f*ck
                    "\\bs+[\\s._\\-*~]*h+[\\s._\\-*~]*[i1!]+[\\s._\\-*~]*t+\\b|" +     //  sh1t
                    "\\bb+[\\s._\\-*~]*[i1!]+[\\s._\\-*~]*t+[\\s._\\-*~]*c+[\\s._\\-*~]*h+\\b|" + // bitch
                    "\\b[a4@]+s+s+h+[o0]+l+[e3]+\\b|" +                               // *sshole
                    "\\bd+i+c+k+\\b",                                                 // d1ck
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    @Transactional
    public Review createReview(CreateReviewDTO dto, User customer) {
        // 1. Kiểm tra đơn hàng[cite: 1]
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!")); //[cite: 1]

        // 2. Bảo mật: Chỉ người đặt đơn mới được đánh giá[cite: 1]
        if (!order.getUser().getId().equals(customer.getId())) {
            throw new RuntimeException("Bạn không có quyền đánh giá đơn hàng của người khác!"); //[cite: 1]
        }

        // 3. Logic: Chỉ được đánh giá khi đơn đã giao xong
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new RuntimeException("Chỉ có thể đánh giá khi đơn hàng đã hoàn tất giao hàng!");
        }

        // 3.5. Logic: Đơn hàng chỉ được đánh giá trong vòng 7 ngày kể từ khi đặt hàng
        if (order.getCreatedAt() != null && java.time.LocalDateTime.now().isAfter(order.getCreatedAt().plusDays(7))) {
            throw new RuntimeException("Đơn hàng này đã quá hạn 7 ngày để đánh giá!");
        }

        // 4. Logic: Mỗi đơn chỉ đánh giá 1 lần
        if (reviewRepository.existsByOrderId(order.getId())) {
            throw new RuntimeException("Đơn hàng này đã được đánh giá rồi!"); //[cite: 1]
        }

        // 5. Kiểm tra số sao hợp lệ (1-5)[cite: 1]
        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new RuntimeException("Điểm đánh giá phải từ 1 đến 5 sao!"); //[cite: 1]
        }

        // 6. KIỂM DUYỆT COMMENT NÂNG CAO (Issue #21) - Đã nâng cấp bảo mật từ ngữ[cite: 1]
        if (dto.getComment() != null && !dto.getComment().trim().isEmpty()) {
            if (BAD_WORDS_PATTERN.matcher(dto.getComment()).find()) {
                throw new RuntimeException("Bình luận của bạn chứa từ ngữ không phù hợp vi phạm tiêu chuẩn cộng đồng!"); //[cite: 1]
            }
        }

        // 7. Lưu Review
        Review review = Review.builder()
                .order(order)
                .restaurant(order.getRestaurant())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);

        // Thưởng xu cho khách hàng sau khi đánh giá thành công
        loyaltyService.rewardPointsForReview(customer);

        return savedReview;
    }

    public List<Review> getRestaurantReviews(Long restaurantId) {
        return reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId); //[cite: 1]
    }

    @Transactional
    public Review replyReview(Long reviewId, String reply, User merchant) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá!"));

        // Check if the merchant owns the restaurant that got the review
        if (!review.getRestaurant().getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("Bạn không có quyền trả lời đánh giá của cửa hàng khác!");
        }

        review.setMerchantReply(reply);
        return reviewRepository.save(review);
    }
}