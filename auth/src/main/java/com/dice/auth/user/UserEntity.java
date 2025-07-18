package com.dice.auth.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.List;


@Data
@Entity
@Table(schema = "public", name = "user", indexes = {
        @Index(columnList = "username", unique = true),
        @Index(columnList = "email", unique = true)
})
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * External google ID.
     */
    @Column(unique = true)
    private String googleId;
    /**
     * External facebook ID.
     */
    @Column(unique = true)
    private String facebookId;
    /**
     * User's hashed password.
     */
    @ToString.Exclude
    private String password;
    /**
     * Username means internal account name that is hidden for other users
     */
    @Column(unique = true)
    private String username;
    /**
     * User's email.
     */
    @Column(unique = true)
    private String email;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    /**
     * Indicates that user's email is verified.
     */
    private boolean emailVerified;
    /**
     * Indicates that user is fully registered.
     */
    private boolean registered;
    /**
     * User's authorities
     */
    @ElementCollection
    @CollectionTable(name = "user_authorities", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "authority")
    private List<String> authorities;
}
