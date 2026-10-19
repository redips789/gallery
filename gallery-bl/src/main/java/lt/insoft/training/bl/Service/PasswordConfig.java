package lt.insoft.training.bl.Service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Passwords are stored hashed with BCrypt, as "{bcrypt}$2a$...". The "{bcrypt}" prefix lets the algorithm be
 * changed later without breaking existing passwords.
 */
@Configuration
public class PasswordConfig {

    public static final String ENCODED_PREFIX = "{bcrypt}";

    @Bean
    public PasswordEncoder passwordEncoder() {
        DelegatingPasswordEncoder encoder =
                (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
        // a stored value without a known prefix (e.g. plain text) never matches, so login fails normally
        // instead of with an error
        encoder.setDefaultPasswordEncoderForMatches(new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                throw new UnsupportedOperationException("only used for matching");
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return false;
            }
        });
        return encoder;
    }
}
