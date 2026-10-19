package lt.insoft.training.bl.Repository.impl;

import lt.insoft.training.bl.Repository.FolderRepository;
import lt.insoft.training.model.*;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

import static java.lang.Math.toIntExact;

/**
 * Created by povilas.jegelevicius on 2017-03-02.
 */
@Repository
public class FolderRepositoryImpl implements FolderRepository {

    @PersistenceContext//(unitName="trainingPersistence")
    private EntityManager entityManager;

    @Override
    public boolean addFolder(Folder folder) {
        try {
            entityManager.persist(folder);
            entityManager.flush();
            return true;
        } catch (PersistenceException e) {
            return false;
        }
    }

    @Override
    public boolean deleteFolder(Folder folder) throws EntityNotFoundException {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Folder> cq = cb.createQuery(Folder.class);
        Root<Folder> folderRoot = cq.from(Folder.class);
        cq.where(cb.equal(folderRoot.get(Folder_.id), folder.getId()));
        cq.select(folderRoot);
        TypedQuery<Folder> q = entityManager.createQuery(cq);
        List<Folder> folders = q.getResultList();
        if (!folders.isEmpty()) {
            entityManager.remove(folders.get(0));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean updateFolder(Folder folder) {
        Folder fold = entityManager.find(Folder.class, folder.getId());
        if (fold != null) {
            fold.setFolderName(folder.getFolderName());
            try {
                entityManager.persist(fold);
                entityManager.flush();
                return true;
            } catch (PersistenceException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public Folder selectFolderById(long id) {
        return entityManager.find(Folder.class, id);
    }

    @Override
    public List<Folder> selectFolders(int from, int maxFolders) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Folder> cq = cb.createQuery(Folder.class);
        Root<Folder> folders = cq.from(Folder.class);
        cq.select(folders);
        cq.orderBy(cb.asc(folders.get(Folder_.insertDate)));
        TypedQuery<Folder> q = entityManager.createQuery(cq);
        q.setFirstResult(from);
        q.setMaxResults(maxFolders);
        return q.getResultList();
    }

    @Override
    public long countFolders() {
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        Root<Folder> folderRoot = cq.from(Folder.class);
        cq.select(qb.count(folderRoot));
        return entityManager.createQuery(cq).getSingleResult();
    }

    @Override
    public Folder selectFolderByName(String name) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Folder> cq = cb.createQuery(Folder.class);
        Root<Folder> folders = cq.from(Folder.class);
        cq.select(folders);
        cq.where(cb.equal(folders.get(Folder_.folderName), name));
        TypedQuery<Folder> q = entityManager.createQuery(cq);
        List<Folder> folderList = q.getResultList();
        if (folderList.isEmpty()) {
            return null;
        } else {
            return folderList.get(0);
        }
    }
}
