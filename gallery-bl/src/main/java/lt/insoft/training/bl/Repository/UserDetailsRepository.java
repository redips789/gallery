package lt.insoft.training.bl.Repository;

import lt.insoft.training.model.User;

/**
 * Created by povilas.jegelevicius on 2017-04-10.
 */
public interface UserDetailsRepository {

    boolean addUser(User user);
}
