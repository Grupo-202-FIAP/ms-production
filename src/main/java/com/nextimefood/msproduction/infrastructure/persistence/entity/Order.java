package com.nextimefood.msproduction.infrastructure.persistence.entity;

import com.nextimefood.msproduction.domain.enums.OrderStatus;
import com.nextimefood.msproduction.domain.enums.PaymentStatus;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Order {
    private static final int END_ID = 4;
    @Id
    private UUID id;
    private UUID transactionId;
    private String identifier;
    private UUID customerId;
    private PaymentStatus paymentStatus;
    private OrderStatus status;
    private List<OrderItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String generateOrderId() {
        final String shortUUID = UUID.randomUUID().toString().substring(0, END_ID).toUpperCase();
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        final String datetime = LocalDateTime.now().format(dtf);
        final String shuffledDateTime = shuffleString(datetime);
        return "ORD-" + shortUUID + "-" + shuffledDateTime.substring(0, END_ID);
    }

    private String shuffleString(String input) {
        final List<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }
        Collections.shuffle(characters);
        final StringBuilder output = new StringBuilder(input.length());
        for (char c : characters) {
            output.append(c);
        }
        return output.toString();
    }


}
