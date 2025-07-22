package com.dice.auth.email.verification;

import com.dice.auth.core.access.Roles;
import com.dice.auth.core.properties.AuthConfigurationProperties;
import com.dice.auth.user.UserService;
import com.dice.auth.user.domain.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class EmailVerificationService {

    private final Clock clock;
    private final UserService userService;
    private final JavaMailSender javaMailSender;
    private final AuthConfigurationProperties authProperties;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public EmailVerificationResult verifyEmail(String tokenId) {
        Optional<EmailVerificationTokenEntity> emailVerificationTokenOpt = emailVerificationTokenRepository.findByTokenId(tokenId);
        if (emailVerificationTokenOpt.isEmpty()) {
            log.info("Email verification for token id '{}' not successful because token is not found", tokenId);
            return EmailVerificationResult.error(EmailVerificationResult.Error.TOKEN_NOT_FOUND);
        }

        EmailVerificationTokenEntity emailVerificationToken = emailVerificationTokenOpt.get();
        User user = userService.getUserById(emailVerificationToken.getUserId());

        if (user.isEmailVerified()) {
            log.warn("Email verification for token id '{}' not successful because user '{}' already has verified email", tokenId, user.getId());
            return EmailVerificationResult.error(EmailVerificationResult.Error.EMAIL_ALREADY_VERIFIED);
        }

        User verifiedUser = userService.save(user.toBuilder()
                .authority(Roles.USER.getRoleWithPrefix())
                .emailVerified(true)
                .build());

        emailVerificationTokenRepository.deleteById(emailVerificationToken.getId());
        return EmailVerificationResult.successful(verifiedUser);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void createOrRecreateEmailVerificationTokenForUser(Long userId) {
        User user = userService.getUserById(userId);
        if (user.isEmailVerified()) {
            throw new IllegalArgumentException(String.format("User %d already has verified email!", user.getId()));
        }

        Optional<EmailVerificationTokenEntity> emailVerificationTokenOpt = emailVerificationTokenRepository.findByUserId(userId);
        if (emailVerificationTokenOpt.isPresent()) {
            EmailVerificationTokenEntity verificationToken = emailVerificationTokenOpt.get();
            handleExistingVerificationToken(user, verificationToken);
        } else {
            String newTokenId = UUID.randomUUID().toString();
            emailVerificationTokenRepository.save(new EmailVerificationTokenEntity(
                    newTokenId, userId, clock.instant()
            ));
            sendVerificationEmail(user, newTokenId);
        }
    }

    private void handleExistingVerificationToken(User user, EmailVerificationTokenEntity emailVerificationToken) {
        Instant now = clock.instant();
        Instant emailTokenCreatedAt = emailVerificationToken.getCreatedAt();
        Instant allowedForRecreationAfter = emailTokenCreatedAt.plus(authProperties.getVerificationEmailRateLimitInMinutesPerUser(), ChronoUnit.MINUTES);

        if (now.isAfter(allowedForRecreationAfter)) {
            String newTokenId = UUID.randomUUID().toString();
            emailVerificationTokenRepository.save(new EmailVerificationTokenEntity(
                    emailVerificationToken.getId(), newTokenId, emailVerificationToken.getUserId(), now
            ));

            sendVerificationEmail(user, newTokenId);
        }
    }

    @Async
    void sendVerificationEmail(User user, String tokenId) {
        try {
            String emailHtmlText = String.format("""
                    <html>
                      <body>
                        <p>Hello,</p>
                        <p>Please confirm your email by clicking the button below:</p>
                        <a href="http://localhost:9000/auth/email/verification/%s"
                           style="display: inline-block; padding: 12px 24px; font-size: 16px; color: #fff; background-color: #007bff; text-decoration: none; border-radius: 4px;">
                          Confirm Email
                        </a>
                        <p>If you did not request this, you can ignore this email.</p>
                      </body>
                    </html>
                    """, tokenId);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setTo(user.getEmail());
            messageHelper.setSubject("Verify email");
            messageHelper.setText(emailHtmlText, true);

            javaMailSender.send(messageHelper.getMimeMessage());
        } catch (MessagingException e) {
            String message = String.format("Couldn't send verification email for user %d to email %s",
                    user.getId(), user.getEmail());
            log.error(message, e);
        }
    }
}
