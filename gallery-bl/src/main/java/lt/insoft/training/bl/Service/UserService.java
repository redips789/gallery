package lt.insoft.training.bl.Service;

import lt.insoft.training.bl.Repository.UserDetailsRepository;
import lt.insoft.training.model.User;
import org.springframework.beans.factory.annotation.Autowired;
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

    public boolean addUser(User user) {
        return userDetailsRepository.addUser(user);
    }
}
