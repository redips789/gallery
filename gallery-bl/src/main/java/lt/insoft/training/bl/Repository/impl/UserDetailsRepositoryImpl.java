package lt.insoft.training.bl.Repository.impl;

import lt.insoft.training.bl.Repository.UserDetailsRepository;
import lt.insoft.training.model.User_;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by povilas.jegelevicius on 2017-04-10.
 */
@Repository
public class UserDetailsRepositoryImpl implements UserDetailsRepository, UserDetailsService {

    @PersistenceContext//(unitName = "trainingPersistence")
    private EntityManager entityManager;

    @Override
    public boolean addUser(lt.insoft.training.model.User user) {
        try {
            entityManager.persist(user);
            entityManager.flush();
            return true;
        } catch (PersistenceException e) {
            return false;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<lt.insoft.training.model.User> cq = qb.createQuery(lt.insoft.training.model.User.class);
        Root<lt.insoft.training.model.User> user = cq.from(lt.insoft.training.model.User.class);
        cq.select(user);
        cq.where(qb.equal(user.get(User_.username), s));
        TypedQuery<lt.insoft.training.model.User> q = entityManager.createQuery(cq);
        lt.insoft.training.model.User userEntity;
        try {
            userEntity = q.getSingleResult();
        } catch (NoResultException e) {
            throw new UsernameNotFoundException("Username: " + s + " not found");
        }
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new User(userEntity.getUsername(), userEntity.getPassword(), authorities);
        return userDetails;
    }
}
