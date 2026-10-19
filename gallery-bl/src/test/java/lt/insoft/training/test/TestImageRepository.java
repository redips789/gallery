package lt.insoft.training.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnitUtil;
import lt.insoft.training.bl.Repository.FolderRepository;
import lt.insoft.training.bl.Repository.ImageRepository;
import lt.insoft.training.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Each test runs in a transaction that is rolled back afterwards.
 *
 * Created by povilas.jegelevicius on 2017-04-05.
 */
@ContextConfiguration(classes = TestConfiguration.class)
@ExtendWith(SpringExtension.class)
@Transactional
public class TestImageRepository {

    private static final long MISSING_ID = -1;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;

    // has a time of day, so searching by date must match the whole day
    private final LocalDateTime insertDate = LocalDateTime.of(2017, 4, 6, 14, 30);
    private final String dateString = "2017-04-06";
    private final String folderName = "test";
    private final Folder folder = new Folder(folderName, insertDate);
    private final String imageName = "imageName";
    private final String tagName = "tagName";
    private final String quality = "Quality";
    private final String description = "testDescription";

    private ImageDetails imageDetails = getImageInitialized();

    /**
     * Writes pending changes and empties the persistence context, so the next read comes from the
     * database instead of returning the objects the test already holds.
     */
    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    /** Saves imageDetails and returns its id, with the persistence context cleared. */
    private long saveImage() {
        Assertions.assertTrue(imageRepository.addImage(imageDetails));
        flushAndClear();
        return imageDetails.getId();
    }

    private List<Long> ids(List<ImageDetails> images) {
        return images.stream().map(ImageDetails::getId).toList();
    }

    @Test
    public void testAddImage() {
        long id = saveImage();
        ImageDetails imageFromDB = imageRepository.getImageDetailsInitialized(id);
        Assertions.assertEquals(imageName, imageFromDB.getImageName());
        Assertions.assertEquals(insertDate, imageFromDB.getInsertDate());
        Assertions.assertEquals(List.of(tagName), imageFromDB.getTags());
    }

    @Test
    public void testAddImageWritesImmediately() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        String countSql = "SELECT COUNT(*) FROM POV_IMAGE_DETAILS WHERE IMAGE_NAME = ?";
        Assertions.assertTrue(imageRepository.addImage(imageDetails));
        Assertions.assertNotNull(imageDetails.getId());
        // plain JDBC on the same transaction only sees the row if it was flushed
        Assertions.assertEquals(1, jdbc.queryForObject(countSql, Integer.class, imageName));
    }

    @Test
    public void testAddImageTwice() {
        Assertions.assertTrue(imageRepository.addImage(imageDetails));
        Assertions.assertFalse(imageRepository.addImage(imageDetails));
    }

    @Test
    public void testAddImageToDetachedFolder() {
        folderRepository.addFolder(folder);
        // a separate copy with the same id, like the one the upload page holds
        Folder detached = new Folder(folderName, insertDate);
        ReflectionTestUtils.setField(detached, "id", folder.getId());
        imageDetails.setFolder(detached);
        Assertions.assertTrue(imageRepository.addImage(imageDetails));
        Assertions.assertEquals(folder.getId(), imageDetails.getFolder().getId());
    }

    @Test
    public void testAddImageToMissingFolder() {
        Folder missing = new Folder(folderName, insertDate);
        ReflectionTestUtils.setField(missing, "id", MISSING_ID);
        imageDetails.setFolder(missing);
        Assertions.assertFalse(imageRepository.addImage(imageDetails));
        imageDetails.setFolder(new Folder(folderName, insertDate));
        Assertions.assertFalse(imageRepository.addImage(imageDetails));
    }

    @Test
    public void testUpdateImage() {
        long id = saveImage();
        // detached copy, like the one the edit page holds between requests
        ImageDetails edited = imageRepository.getImageDetailsInitialized(id);
        entityManager.detach(edited);
        edited.setImageName("updated name");
        edited.setDescription("updated description");
        edited.setTags(new ArrayList<>(List.of("first", "second")));
        Assertions.assertTrue(imageRepository.updateImage(edited));
        flushAndClear();
        ImageDetails imageFromDB = imageRepository.getImageDetailsInitialized(id);
        Assertions.assertEquals("updated name", imageFromDB.getImageName());
        Assertions.assertEquals("updated description", imageFromDB.getDescription());
        Assertions.assertEquals(Set.of("first", "second"), new HashSet<>(imageFromDB.getTags()));
    }

    @Test
    public void testUpdateImageThatDoesntExist() {
        imageDetails.setId(MISSING_ID);
        imageDetails.setImageName("updated name");
        Assertions.assertFalse(imageRepository.updateImage(imageDetails));
        Assertions.assertTrue(imageRepository.selectImagesByName("updated name", 0, 1).isEmpty());
    }

    @Test
    public void testDeleteImage() {
        long id = saveImage();
        long imageId = imageDetails.getImage().getId();
        long thumbnailId = imageDetails.getThumbnail().getId();
        long modalImageId = imageDetails.getModalImage().getId();
        Assertions.assertTrue(imageRepository.deleteImage(imageDetails));
        flushAndClear();
        Assertions.assertNull(imageRepository.getImageDetailsInitialized(id));
        // the picture's own image data goes with it
        Assertions.assertNull(entityManager.find(Image.class, imageId));
        Assertions.assertNull(entityManager.find(Image.class, thumbnailId));
        Assertions.assertNull(entityManager.find(Image.class, modalImageId));
    }

    @Test
    public void testDeleteImageNotSaved() {
        Assertions.assertFalse(imageRepository.deleteImage(new ImageDetails()));
    }

    @Test
    public void testDeleteImageThatDoesntExist() {
        imageDetails.setId(MISSING_ID);
        Assertions.assertFalse(imageRepository.deleteImage(imageDetails));
    }

    @Test
    public void testGetImageDetailsInitialized() {
        long id = saveImage();
        PersistenceUnitUtil util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

        // a plain query leaves the large images unloaded...
        ImageDetails plain = imageRepository.selectImagesByName(imageName, 0, 1).get(0);
        Assertions.assertFalse(util.isLoaded(plain, "image"));
        entityManager.clear();

        // ...this one loads them, so they can be used after the transaction ends
        ImageDetails initialized = imageRepository.getImageDetailsInitialized(id);
        Assertions.assertTrue(util.isLoaded(initialized, "image"));
        Assertions.assertTrue(util.isLoaded(initialized, "modalImage"));
        Assertions.assertTrue(util.isLoaded(initialized, "tags"));
    }

    @Test
    public void testGetImageDetailsInitializedWithoutModalImage() {
        imageDetails.setModalImage(null);
        long id = saveImage();
        Assertions.assertNotNull(imageRepository.getImageDetailsInitialized(id));
    }

    @Test
    public void testGetImageDetailsInitializedThatDoesntExist() {
        Assertions.assertNull(imageRepository.getImageDetailsInitialized(MISSING_ID));
    }

    @Test
    public void testSelectFolderImagesOrderAndPaging() {
        folderRepository.addFolder(folder);
        ImageDetails newest = getImageInitialized();
        newest.setInsertDate(insertDate.plusHours(1));
        ImageDetails oldest = getImageInitialized();
        oldest.setInsertDate(insertDate.minusHours(1));
        ImageDetails otherFolder = getImageInitialized();
        for (ImageDetails image : List.of(newest, imageDetails, oldest)) {
            image.setFolder(folder);
            imageRepository.addImage(image);
        }
        imageRepository.addImage(otherFolder);
        flushAndClear();
        // oldest first, and only this folder's pictures
        Assertions.assertEquals(List.of(oldest.getId(), imageDetails.getId()),
                ids(imageRepository.selectFolderImages(folder, 0, 2)));
        Assertions.assertEquals(List.of(newest.getId()), ids(imageRepository.selectFolderImages(folder, 2, 2)));
    }

    @Test
    public void testCountImagesInFolder() {
        folderRepository.addFolder(folder);
        Assertions.assertEquals(0, imageRepository.countImagesInFolder(folder));
        imageDetails.setFolder(folder);
        imageRepository.addImage(imageDetails);
        imageRepository.addImage(getImageInitialized());
        Assertions.assertEquals(1, imageRepository.countImagesInFolder(folder));
    }

    @Test
    public void testSelectImagesByName() {
        long id = saveImage();
        Assertions.assertEquals(List.of(id), ids(imageRepository.selectImagesByName(imageName, 0, 1)));
        Assertions.assertTrue(imageRepository.selectImagesByName("other", 0, 1).isEmpty());
    }

    @Test
    public void testSearchByName() {
        long id = saveImage();
        Assertions.assertEquals(1, imageRepository.countImagesBySearchCriteria(imageName));
        Assertions.assertEquals(List.of(id), ids(imageRepository.selectImagesBySearchCriteria(imageName, 0, 1)));
        // case-insensitive
        Assertions.assertEquals(1, imageRepository.countImagesBySearchCriteria(imageName.toUpperCase()));
    }

    @Test
    public void testSearchByTag() {
        long id = saveImage();
        Assertions.assertEquals(1, imageRepository.countImagesBySearchCriteria(tagName));
        Assertions.assertEquals(List.of(id), ids(imageRepository.selectImagesBySearchCriteria(tagName, 0, 1)));
    }

    @Test
    public void testSearchByDescription() {
        long id = saveImage();
        // matches part of the description
        String part = description.substring(4, 10);
        Assertions.assertEquals(1, imageRepository.countImagesBySearchCriteria(part));
        Assertions.assertEquals(List.of(id), ids(imageRepository.selectImagesBySearchCriteria(part, 0, 1)));
    }

    @Test
    public void testSearchByDescriptionEscapesWildcards() {
        saveImage();
        Assertions.assertEquals(0, imageRepository.countImagesBySearchCriteria("%"));
        Assertions.assertEquals(0, imageRepository.countImagesBySearchCriteria("_"));
    }

    @Test
    public void testSearchByQuality() {
        long id = saveImage();
        Assertions.assertEquals(1, imageRepository.countImagesBySearchCriteria(quality));
        Assertions.assertEquals(List.of(id), ids(imageRepository.selectImagesBySearchCriteria(quality, 0, 1)));
    }

    @Test
    public void testSearchByDate() {
        long id = saveImage();
        Assertions.assertEquals(1, imageRepository.countImagesBySearchCriteria(dateString));
        Assertions.assertEquals(List.of(id), ids(imageRepository.selectImagesBySearchCriteria(dateString, 0, 1)));
        // without leading zeros
        Assertions.assertEquals(1, imageRepository.countImagesBySearchCriteria("2017-4-6"));
    }

    @Test
    public void testSearchByDateMatchesOnlyThatDay() {
        ImageDetails startOfDay = getImageInitialized();
        startOfDay.setInsertDate(insertDate.toLocalDate().atStartOfDay());
        ImageDetails endOfDay = getImageInitialized();
        endOfDay.setInsertDate(insertDate.toLocalDate().atTime(23, 59, 59));
        ImageDetails dayBefore = getImageInitialized();
        dayBefore.setInsertDate(startOfDay.getInsertDate().minusSeconds(1));
        ImageDetails nextDay = getImageInitialized();
        nextDay.setInsertDate(startOfDay.getInsertDate().plusDays(1));
        for (ImageDetails image : List.of(startOfDay, endOfDay, dayBefore, nextDay)) {
            imageRepository.addImage(image);
        }
        flushAndClear();
        Assertions.assertEquals(List.of(startOfDay.getId(), endOfDay.getId()),
                ids(imageRepository.selectImagesBySearchCriteria(dateString, 0, 10)));
        Assertions.assertEquals(2, imageRepository.countImagesBySearchCriteria(dateString));
    }

    @Test
    public void testSearchNoMatch() {
        saveImage();
        Assertions.assertEquals(0, imageRepository.countImagesBySearchCriteria("nothing like it"));
        Assertions.assertTrue(imageRepository.selectImagesBySearchCriteria("nothing like it", 0, 1).isEmpty());
    }

    @Test
    public void testSearchPaging() {
        imageRepository.addImage(imageDetails);
        imageRepository.addImage(getImageInitialized());
        imageRepository.addImage(getImageInitialized());
        flushAndClear();
        Assertions.assertEquals(3, imageRepository.countImagesBySearchCriteria(imageName));
        Assertions.assertEquals(2, imageRepository.selectImagesBySearchCriteria(imageName, 0, 2).size());
        Assertions.assertEquals(1, imageRepository.selectImagesBySearchCriteria(imageName, 2, 2).size());
    }

    @Test
    public void testImageDetailsEquality() {
        ImageDetails unsaved = new ImageDetails();
        Assertions.assertEquals(unsaved, unsaved);
        Assertions.assertNotEquals(unsaved, new ImageDetails());
        Set<ImageDetails> set = new HashSet<>();
        set.add(unsaved);
        unsaved.setId(42);
        // still found after the id was assigned
        Assertions.assertTrue(set.contains(unsaved));

        long id = saveImage();
        ImageDetails sameId = new ImageDetails();
        sameId.setId(id);
        Assertions.assertEquals(imageRepository.selectImagesByName(imageName, 0, 1).get(0), sameId);
    }

    private ImageDetails getImageInitialized() {
        ImageDetails imageDetails = new ImageDetails();
        imageDetails.setImage(new Image());
        imageDetails.setThumbnail(new Image());
        imageDetails.setModalImage(new Image());
        imageDetails.setDescription(description);
        imageDetails.setImageName(imageName);
        imageDetails.setInsertDate(insertDate);
        imageDetails.setQuality(quality);
        imageDetails.setTags(new ArrayList<>(List.of(tagName)));
        return imageDetails;
    }
}
