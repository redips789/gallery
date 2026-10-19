package lt.insoft.training.bl.Repository;

import lt.insoft.training.model.User;

import java.util.List;

/**
 * Created by povilas.jegelevicius on 2017-04-10.
 */
public interface UserDetailsRepository {

    /**
     * @param user with the password already hashed
     * @return false if the username is taken
     */
    boolean addUser(User user);

    List<User> selectUsers();
}
