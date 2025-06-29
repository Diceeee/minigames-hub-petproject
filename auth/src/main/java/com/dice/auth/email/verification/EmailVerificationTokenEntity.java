package com.dice.auth.email.verification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(schema = "public", name = "email_verification_token", indexes = {
        @Index(columnList = "user_id", unique = true)
})
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerificationTokenEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "user_id", updatable = false, nullable = false)
    private Long userId;
}
