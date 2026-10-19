package lt.insoft.training.bl.Repository.impl;

import lt.insoft.training.bl.Repository.ImageRepository;
import lt.insoft.training.bl.Service.UtilityService;
import lt.insoft.training.model.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.toIntExact;

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
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteImage(ImageDetails imageDetails) {
        if (imageDetails.getId() == null) {
            return false;
        }
        ImageDetails imageFromDB = entityManager.find(ImageDetails.class, imageDetails.getId());
        if (imageFromDB == null) {
            return false;
        }
        entityManager.remove(imageFromDB);
        return true;
    }

    @Override
    public List<ImageDetails> selectFolderImages(Folder folder, int from, int maxImages) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ImageDetails> cq = cb.createQuery(ImageDetails.class);
        Root<ImageDetails> imageDetails = cq.from(ImageDetails.class);
        cq.where(cb.equal(imageDetails.get(ImageDetails_.folder).get(Folder_.id), folder.getId()));
        cq.select(imageDetails);
        // INSERT_DATE has no time part, so id keeps the order stable between pages
        cq.orderBy(cb.asc(imageDetails.get(ImageDetails_.insertDate)), cb.asc(imageDetails.get(ImageDetails_.id)));
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
        cq.where(qb.equal(imageDetails.get(ImageDetails_.folder).get(Folder_.id), folder.getId()));
        return entityManager.createQuery(cq).getSingleResult();
    }

    @Override
    public ImageDetails getImageDetailsInitialized(long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ImageDetails> q = cb.createQuery(ImageDetails.class);
        Root<ImageDetails> o = q.from(ImageDetails.class);
        o.fetch(ImageDetails_.modalImage, JoinType.LEFT);
        o.fetch(ImageDetails_.tags, JoinType.LEFT);
        o.fetch(ImageDetails_.image, JoinType.LEFT);
        q.select(o);
        q.where(cb.equal(o.get(ImageDetails_.id), id));
        List<ImageDetails> result = entityManager.createQuery(q).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<ImageDetails> selectImagesByName(String name, int from, int maxResults) {
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ImageDetails> cq = qb.createQuery(ImageDetails.class);
        Root<ImageDetails> imageDetails = cq.from(ImageDetails.class);
        cq.select(imageDetails);
        cq.where(qb.equal(imageDetails.get(ImageDetails_.imageName), name));
        cq.orderBy(qb.asc(imageDetails.get(ImageDetails_.id)));
        TypedQuery<ImageDetails> q = entityManager.createQuery(cq);
        q.setFirstResult(from);
        q.setMaxResults(maxResults);
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
        Join<ImageDetails, String> tags = imageDetails.join("tags", JoinType.LEFT);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.addAll(buildSearchQueryPredicate(searchInput, qb, tags, imageDetails));
        cq.select(qb.countDistinct(imageDetails));
        cq.where(qb.or(predicates.toArray(new Predicate[]{})));
        return entityManager.createQuery(cq).getSingleResult();
    }

    @Override
    public List<ImageDetails> selectImagesBySearchCriteria(String searchQuery, int from, int maxResults) {
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ImageDetails> cq = qb.createQuery(ImageDetails.class);
        Root<ImageDetails> imageDetails = cq.from(ImageDetails.class);
        Join<ImageDetails, String> tags = imageDetails.join("tags", JoinType.LEFT);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.addAll(buildSearchQueryPredicate(searchQuery, qb, tags, imageDetails));
        cq.select(imageDetails).distinct(true);
        cq.where(qb.or(predicates.toArray(new Predicate[]{})));
        cq.orderBy(qb.asc(imageDetails.get(ImageDetails_.insertDate)), qb.asc(imageDetails.get(ImageDetails_.id)));
        TypedQuery<ImageDetails> q = entityManager.createQuery(cq);
        q.setFirstResult(from);
        q.setMaxResults(maxResults);
        return q.getResultList();
    }

    public List<Predicate> buildSearchQueryPredicate(String searchQuery, CriteriaBuilder qb
            , Join<ImageDetails, String> tags, Root<ImageDetails> imageDetails) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(qb.equal(qb.upper(imageDetails.get(ImageDetails_.imageName)), searchQuery.toUpperCase()));
        predicates.add(qb.equal(qb.upper(imageDetails.get(ImageDetails_.quality)), searchQuery.toUpperCase()));
        LocalDate parsedDate = utilityService.parseDateService(searchQuery);
        if (parsedDate != null) {
            // insertDate has a time of day, so match anything within that day
            Path<LocalDateTime> insertDate = imageDetails.get(ImageDetails_.insertDate);
            predicates.add(qb.and(
                    qb.greaterThanOrEqualTo(insertDate, parsedDate.atStartOfDay()),
                    qb.lessThan(insertDate, parsedDate.plusDays(1).atStartOfDay())));
        }
        String searchQueryWithEscapedCharacters = utilityService.escapeSpecialCharacters(searchQuery);
        predicates.add(qb.like(qb.upper(imageDetails.get(ImageDetails_.description)), "%" + searchQueryWithEscapedCharacters.toUpperCase() + "%", '!'));
        predicates.add(qb.equal(qb.upper(tags), searchQuery.toUpperCase()));
        return predicates;
    }
}
