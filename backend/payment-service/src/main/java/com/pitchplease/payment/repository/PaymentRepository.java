package com.pitchplease.payment.repository;

import com.pitchplease.payment.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * Find payment by booking ID
     * @param bookingId The booking ID
     * @return Optional of Payment
     */
    Optional<Payment> findByBookingId(Long bookingId);
    
    /**
     * Find all payments for a user
     * @param userId The user ID
     * @return List of payments
     */
    List<Payment> findByUserId(Long userId);
    /**
     * Find payments by facility ID
     * @param facilityId
     * @return
     */
    List<Payment> findByFacilityId(Long facilityId);
    /**
     * Find payments by status
     * @param paymentStatus The payment status
     * @return List of payments
     */
    List<Payment> findByPaymentStatus(String paymentStatus);
    
    /**
     * Find payments by user ID and status
     * @param userId The user ID
     * @param paymentStatus The payment status
     * @return List of payments
     */
    List<Payment> findByUserIdAndPaymentStatus(Long userId, String paymentStatus);

    // List<Payment> findByFacilityId(Long facilityId);
    /**
     * Find by transaction ID
     * @param transactionId The external transaction ID
     * @return Optional of Payment
     */
    Optional<Payment> findByTransactionId(String transactionId);
    @Modifying
    @Query("UPDATE Payment p SET p.paymentStatus = :newStatus, p.updatedAt = :updatedAt WHERE p.bookingId = :bookingId")
    int updatePaymentStatusByBookingId(@Param("bookingId") Long bookingId, 
                                     @Param("newStatus") String newStatus,
                                     @Param("updatedAt") LocalDateTime updatedAt);


    @Modifying
    @Query("UPDATE Payment p SET p.paymentStatus = :newStatus, p.updatedAt = :updatedAt WHERE p.facilityId = :facilityId")
    int updatePaymentStatusByFacilityId(@Param("facilityId") Long facilityId, 
                                    @Param("newStatus") String newStatus,
                                    @Param("updatedAt") LocalDateTime updatedAt);
                                                 
}