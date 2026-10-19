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
        // checked up front: a failed flush would mark the whole transaction rollback-only
        if (selectUserByName(user.getUsername()) != null) {
            return false;
        }
        entityManager.persist(user);
        entityManager.flush();
        return true;
    }

    @Override
    public List<lt.insoft.training.model.User> selectUsers() {
        CriteriaQuery<lt.insoft.training.model.User> cq =
                entityManager.getCriteriaBuilder().createQuery(lt.insoft.training.model.User.class);
        cq.select(cq.from(lt.insoft.training.model.User.class));
        return entityManager.createQuery(cq).getResultList();
    }

    private lt.insoft.training.model.User selectUserByName(String username) {
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<lt.insoft.training.model.User> cq = qb.createQuery(lt.insoft.training.model.User.class);
        Root<lt.insoft.training.model.User> user = cq.from(lt.insoft.training.model.User.class);
        cq.select(user);
        cq.where(qb.equal(user.get(User_.username), username));
        TypedQuery<lt.insoft.training.model.User> q = entityManager.createQuery(cq);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        lt.insoft.training.model.User userEntity = selectUserByName(s);
        if (userEntity == null) {
            throw new UsernameNotFoundException("Username: " + s + " not found");
        }
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new User(userEntity.getUsername(), userEntity.getPassword(), authorities);
        return userDetails;
    }
}
