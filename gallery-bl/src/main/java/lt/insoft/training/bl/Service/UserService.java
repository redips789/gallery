package lt.insoft.training.bl.Service;

import lt.insoft.training.bl.Repository.UserDetailsRepository;
import lt.insoft.training.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by povilas.jegelevicius on 2017-04-10.
 */
@Component("userService")
@Transactional
public class UserService {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Saves the user with the given plain-text password hashed.
     *
     * @return false if the username is taken
     */
    public boolean addUser(User user) {
        User toSave = new User(user.getUsername(), passwordEncoder.encode(user.getPassword()));
        return userDetailsRepository.addUser(toSave);
    }

    /**
     * Hashes passwords still stored as plain text, as they were before BCrypt was introduced.
     *
     * @return how many passwords were hashed
     */
    public int encodePlainTextPasswords() {
        int count = 0;
        for (User user : userDetailsRepository.selectUsers()) {
            String password = user.getPassword();
            if (password != null && !password.startsWith(PasswordConfig.ENCODED_PREFIX)) {
                // the entity is managed, so the new value is saved when the transaction commits
                user.setPassword(passwordEncoder.encode(password));
                count++;
            }
        }
        return count;
    }
}
