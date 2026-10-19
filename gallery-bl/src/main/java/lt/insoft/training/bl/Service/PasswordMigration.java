package lt.insoft.training.bl.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Hashes any passwords still stored as plain text when the application starts, so they keep working with the
 * BCrypt encoder. Once every password is hashed this finds nothing to do.
 */
@Component
public class PasswordMigration implements ApplicationListener<ContextRefreshedEvent> {

    private final Log logger = LogFactory.getLog(PasswordMigration.class);

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() != null) {
            return;
        }
        int count = userService.encodePlainTextPasswords();
        if (count > 0) {
            logger.info("Hashed " + count + " plain-text password(s) with BCrypt");
        }
    }
}
