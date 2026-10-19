package lt.insoft.training.bl.Repository.impl;

import lt.insoft.training.bl.Repository.ImageRepository;
import lt.insoft.training.bl.Service.UtilityService;
import lt.insoft.training.model.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.toIntExact;
import static lt.insoft.training.model.ImageDetails_.tags;

/**
 * Created by povilas.jegelevicius on 2017-02-22.
 */
@Repository
public class ImageRepositoryImpl implements ImageRepository {

    @PersistenceContext
    private EntityManager entityManager;


    private UtilityService utilityService = new UtilityService();

    @Override
    public boolean addImage(ImageDetails imageDetails) {
        try {
            entityManager.merge(imageDetails);
            return true;
        } catch (PersistenceException e) {
            return false;
        }
    }

    @Override
    public boolean updateImage(ImageDetails updatedImage) {
        ImageDetails imageDetaislFormDB = entityManager.find(ImageDetails.class, updatedImage.getId());
        if(imageDetaislFormDB != null) {
            if (updatedImage.getTags() != null) {
                imageDetaislFormDB.setTags(updatedImage.getTags());
            }
            imageDetaislFormDB.setImageName(updatedImage.getImageName());
            imageDetaislFormDB.setDescription(updatedImage.getDescription());
            entityManager.persist(imageDetaislFormDB);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteImage(ImageDetails imageDetails) throws EntityNotFoundException {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ImageDetails> cq = cb.createQuery(ImageDetails.class);
        Root<ImageDetails> imageDetailsRoot = cq.from(ImageDetails.class);
        cq.where(cb.equal(imageDetailsRoot.get(ImageDetails_.id), imageDetails.getId()));
        cq.select(imageDetailsRoot);
        TypedQuery<ImageDetails> q = entityManager.createQuery(cq);
        List<ImageDetails> images = q.getResultList();
        if (!images.isEmpty()) {
            try {
                entityManager.remove(images.get(0));
                return true;
            } catch (PersistenceException e) {
                entityManager.remove(images.get(0));
            }
        }
        return false;
    }

    @Override
    public List<ImageDetails> selectFolderImages(Folder folder, int from, int maxImages) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ImageDetails> cq = cb.createQuery(ImageDetails.class);
        Root<ImageDetails> imageDetails = cq.from(ImageDetails.class);
        cq.where(cb.equal(imageDetails.get(ImageDetails_.folder), folder.getId()));
        cq.select(imageDetails);
        cq.orderBy(cb.asc(imageDetails.get(ImageDetails_.insertDate)));
        TypedQuery<ImageDetails> q = entityManager.createQuery(cq);
        q.setFirstResult(from);
        q.setMaxResults(maxImages);
        return q.getResultList();
    }

    @Override
    public long countImagesInFolder(Folder folder) {
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        Root<ImageDetails> imageDetails = cq.from(ImageDetails.class);
        cq.select(qb.count(imageDetails));
        cq.where(qb.equal(imageDetails.get(ImageDetails_.folder), folder.getId()));
        return entityManager.createQuery(cq).getSingleResult();
    }

    @Override
    public ImageDetails getImageDetailsInitialized(long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ImageDetails> q = cb.createQuery(ImageDetails.class);
        Root o = q.from(ImageDetails.class);
        o.fetch("modalImage", JoinType.INNER);
        o.fetch("tags", JoinType.LEFT);
        o.fetch("image", JoinType.INNER);
        q.select(o);
        q.where(cb.equal(o.get("id"), Long.valueOf(id)));
        return entityManager.createQuery(q).getSingleResult();
    }

    @Override
    public List<ImageDetails> selectImagesByName(String name, int from, int to) {
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ImageDetails> cq = qb.createQuery(ImageDetails.class);
        Root<ImageDetails> imageDetails = cq.from(ImageDetails.class);
        cq.select(imageDetails);
        cq.where(qb.equal(imageDetails.get(ImageDetails_.imageName), name));
        TypedQuery<ImageDetails> q = entityManager.createQuery(cq);
        q.setFirstResult(from);
        q.setMaxResults(to);
        return q.getResultList();
    }

//    @Override
//    public List<ImageDetails> selectImagesByDescription(String input, int from, int to) {
//        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<ImageDetails> cq = qb.createQuery(ImageDetails.class);
//        Root<ImageDetails> imageDetails = cq.from(ImageDetails.class);
//        cq.select(imageDetails);
//        cq.where(qb.like(imageDetails.get(ImageDetails_.description), "%" + input + "%"));
//        TypedQuery<ImageDetails> q = entityManager.createQuery(cq);
//        q.setFirstResult(from);
//        q.setMaxResults(to);
//        return q.getResultList();
//    }
//
//    @Override
//    public List<ImageDetails> selectImagesByQuality(String quality, int from, int to) {
//        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<ImageDetails> cq = qb.createQuery(ImageDetails.class);
//        Root<ImageDetails> imageDetails = cq.from(ImageDetails.class);
//        cq.select(imageDetails);
//        cq.where(qb.equal(imageDetails.get(ImageDetails_.quality), quality));
//        TypedQuery<ImageDetails> q = entityManager.createQuery(cq);
//        q.setFirstResult(from);
//        q.setMaxResults(to);
//        return q.getResultList();
//    }
//
//    @Override
//    public List<ImageDetails> selectImagesByInsertDate(Date date, int from, int to) {
//        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<ImageDetails> cq = qb.createQuery(ImageDetails.class);
//        Root<ImageDetails> imageDetails = cq.from(ImageDetails.class);
//        cq.select(imageDetails);
//        Date maxDate = new Date(date.getTime() + TimeUnit.DAYS.toMillis(1));
//        cq.where(qb.between(imageDetails.get(ImageDetails_.insertDate), date, maxDate));
//        TypedQuery<ImageDetails> q = entityManager.createQuery(cq);
//        q.setFirstResult(from);
//        q.setMaxResults(to);
//        List<ImageDetails> ad = q.getResultList();
//        return q.getResultList();
//    }

    @Override
    public long countImagesBySearchCriteria(String searchInput) {
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        Root<ImageDetails> imageDetails = cq.from(ImageDetails.class);
//        Join<ImageDetails, Tag> tags = imageDetails.join(ImageDetails_.tags, JoinType.LEFT);
        //imageDetails.get(ImageDetails_.)
        //Join<ImageDetails, String> tags = imageDetails.join("tags", JoinType.LEFT);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.addAll(buildSearchQueryPredicate(searchInput, qb, tags, imageDetails));
        cq.select(qb.countDistinct(imageDetails));
        cq.where(qb.or(predicates.toArray(new Predicate[]{})));
        return entityManager.createQuery(cq).getSingleResult();
    }

    @Override
    public List<ImageDetails> selectImagesBySearchCriteria(String searchQuery, int from, int to) {
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ImageDetails> cq = qb.createQuery(ImageDetails.class);
        Root<ImageDetails> imageDetails = cq.from(ImageDetails.class);
        Join<ImageDetails, String> tags = imageDetails.join("tags", JoinType.LEFT);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.addAll(buildSearchQueryPredicate(searchQuery, qb, tags, imageDetails));
        cq.select(imageDetails).distinct(true);
        cq.where(qb.or(predicates.toArray(new Predicate[]{})));
        TypedQuery<ImageDetails> q = entityManager.createQuery(cq);
        q.setFirstResult(from);
        q.setMaxResults(to);
        return entityManager.createQuery(cq).getResultList();
    }

    public List<Predicate> buildSearchQueryPredicate(String searchQuery, CriteriaBuilder qb
            , Join<ImageDetails, String> tags, Root<ImageDetails> imageDetails) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(qb.equal(qb.upper(imageDetails.get(ImageDetails_.imageName)), searchQuery.toUpperCase()));
        predicates.add(qb.equal(qb.upper(imageDetails.get(ImageDetails_.quality)), searchQuery.toUpperCase()));
        Date parsedDate = utilityService.parseDateService(searchQuery);
        //parsedDate != null ? predicates.add(qb.equal((imageDetails.get(ImageDetails_.insertDate))),parsedDate) : ;
        if (parsedDate != null) {
            predicates.add(qb.equal(imageDetails.get(ImageDetails_.insertDate), parsedDate));
        }
        String searchQueryWithEscapedCharacters = utilityService.escapeSpecialCharacters(searchQuery);
        predicates.add(qb.like(qb.upper(imageDetails.get(ImageDetails_.description)), "%" + searchQueryWithEscapedCharacters.toUpperCase() + "%", '!'));
        //predicates.add(qb.equal(qb.upper(.get("NAME")), searchQuery.toUpperCase()));
        return predicates;
    }
}
