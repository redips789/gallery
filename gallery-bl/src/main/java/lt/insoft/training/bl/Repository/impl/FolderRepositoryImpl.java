package lt.insoft.training.bl.Repository.impl;

import lt.insoft.training.bl.Repository.FolderRepository;
import lt.insoft.training.model.*;
import org.springframework.stereotype.Repository;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 * Created by povilas.jegelevicius on 2017-03-02.
 */
@Repository
public class FolderRepositoryImpl implements FolderRepository {

    @PersistenceContext//(unitName="trainingPersistence")
    private EntityManager entityManager;

    @Override
    public boolean addFolder(Folder folder) {
        // checked up front: a failed flush would mark the whole transaction rollback-only
        if (selectFolderByName(folder.getFolderName()) != null) {
            return false;
        }
        entityManager.persist(folder);
        entityManager.flush();
        return true;
    }

    @Override
    public boolean deleteFolder(Folder folder) {
        if (folder.getId() == null) {
            return false;
        }
        Folder folderFromDB = entityManager.find(Folder.class, folder.getId());
        if (folderFromDB == null || countImagesInFolder(folderFromDB) > 0) {
            return false;
        }
        entityManager.remove(folderFromDB);
        return true;
    }

    @Override
    public boolean updateFolder(Folder folder) {
        if (folder.getId() == null) {
            return false;
        }
        Folder fold = entityManager.find(Folder.class, folder.getId());
        if (fold == null) {
            return false;
        }
        Folder sameName = selectFolderByName(folder.getFolderName());
        if (sameName != null && !sameName.getId().equals(fold.getId())) {
            return false;
        }
        fold.setFolderName(folder.getFolderName());
        entityManager.flush();
        return true;
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
        // INSERT_DATE has no time part, so id keeps the order stable between pages
        cq.orderBy(cb.asc(folders.get(Folder_.insertDate)), cb.asc(folders.get(Folder_.id)));
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

    private long countImagesInFolder(Folder folder) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ImageDetails> imageDetails = cq.from(ImageDetails.class);
        cq.select(cb.count(imageDetails));
        cq.where(cb.equal(imageDetails.get(ImageDetails_.folder).get(Folder_.id), folder.getId()));
        return entityManager.createQuery(cq).getSingleResult();
    }
}
