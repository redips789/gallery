package lt.insoft.training.test;

import java.time.LocalDateTime;
import lt.insoft.training.bl.Repository.FolderRepository;
import lt.insoft.training.bl.Repository.ImageRepository;
import lt.insoft.training.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.*;

/**
 * Created by povilas.jegelevicius on 2017-04-05.
 */
@ContextConfiguration(classes = TestConfiguration.class)
@ExtendWith(SpringExtension.class)
public class TestImageRepository {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private DataSource dataSource;

    // has a time of day, so searching by date must match the whole day
    private LocalDateTime insertDate = LocalDateTime.of(2017, 4, 6, 14, 30);
    private String folderName = "test";
    private Folder folder = new Folder(folderName, insertDate);
    private String imageName = "imageName";
    private String tagName = "tagName";
    private String quality = "Quality";
    private String description = "testDescription";

    private String dateToSting = insertDate.toLocalDate().toString();
    private ImageDetails imageDetails = getImageInitialized();

    @Test
    @Transactional
    @Rollback
    public void testAddImage() {
        imageRepository.addImage(imageDetails);
        ImageDetails imageDetails1 = imageRepository.selectImagesByName(imageName, 0, 1).get(0);
        Assertions.assertEquals(imageDetails1.getImageName(), imageName);
    }

    @Test
    @Transactional
    @Rollback
    public void testSelectFolderImages() {
        folderRepository.addFolder(folder);
        imageDetails.setFolder(folder);
        imageRepository.addImage(imageDetails);
        ImageDetails imageDetails1 = imageRepository.selectFolderImages(folder, 0, 1).get(0);
        Assertions.assertEquals(imageDetails1.getImageName(), imageName);
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateImages() {
        imageRepository.addImage(imageDetails);
        imageDetails = imageRepository.selectImagesByName(imageName, 0, 1).get(0);
        imageDetails.setImageName("testas atnaujintas");
        imageRepository.updateImage(imageDetails);
        imageDetails = imageRepository.selectImagesByName("testas atnaujintas", 0, 1).get(0);
        Assertions.assertEquals("testas atnaujintas", imageDetails.getImageName());
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateImagesWithTags() {
        imageRepository.addImage(imageDetails);
        List<ImageDetails> imageDetails1 = imageRepository.selectImagesByName(imageName, 0, 1);
        imageDetails.setId(imageDetails1.get(0).getId());
        imageDetails.setImageName("testas atnaujintas");
        imageRepository.updateImage(imageDetails);
        imageDetails = imageRepository.selectImagesByName("testas atnaujintas", 0, 1).get(0);
        Assertions.assertEquals("testas atnaujintas", imageDetails.getImageName());
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateImageThatDoesntExist() {
        //imageRepository.addImage(imageDetails);
        imageDetails.setImageName("testas atnaujintas");
        imageDetails.setId(1);
        Assertions.assertTrue(!imageRepository.updateImage(imageDetails));
        List<ImageDetails> imageDetailsList = imageRepository.selectImagesByName("testas atnaujintas", 0, 1);
        Assertions.assertTrue(imageDetailsList.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    public void testDeleteImage() {
        imageRepository.addImage(imageDetails);
        imageDetails = imageRepository.selectImagesByName(imageName, 0, 1).get(0);
        imageRepository.deleteImage(imageDetails);
        List<ImageDetails> imageDetailsList = imageRepository.selectImagesByName(imageName, 0, 1);
        Assertions.assertTrue(imageDetailsList.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    public void testDeleteImageNoImage() {
        imageRepository.deleteImage(imageDetails);
        List<ImageDetails> imageDetailsList = imageRepository.selectImagesByName(imageName, 0, 1);
        Assertions.assertEquals(imageDetailsList.size(), 0);
    }

    @Test
    @Transactional
    @Rollback
    public void testGetImageDetailsInitialized() {
        imageRepository.addImage(imageDetails);
        imageDetails = imageRepository.selectImagesByName(imageName, 0, 1).get(0);
        imageDetails = imageRepository.getImageDetailsInitialized(imageDetails.getId());
        Assertions.assertNotEquals(null, imageDetails.getImage().getId());
    }

    @Test
    @Transactional
    @Rollback
    public void testCountImagesInFolder() {
        folderRepository.addFolder(folder);
        folder = folderRepository.selectFolderByName(folderName);
        imageDetails.setFolder(folder);
        imageRepository.addImage(imageDetails);
        Assertions.assertEquals(1, imageRepository.countImagesInFolder(folder));
    }

    @Test
    @Transactional
    @Rollback
    public void testCountImagesBySearchCriteriaByName() {
        imageRepository.addImage(imageDetails);
        long count = imageRepository.countImagesBySearchCriteria(imageName);
        Assertions.assertEquals(1, count);
    }

    @Test
    @Transactional
    @Rollback
    public void testCountImagesBySearchCriteriaParseDate() {
        imageRepository.addImage(imageDetails);
        long count = imageRepository.countImagesBySearchCriteria(dateToSting);
        Assertions.assertEquals(1, count);
    }

    @Test
    @Transactional
    @Rollback
    public void testSelectImagesBySearchCriteriaParseDate() {
        imageRepository.addImage(imageDetails);
        imageDetails.setId(imageRepository.selectImagesByName(imageName, 0, 1).get(0).getId());
        long count = imageRepository.countImagesBySearchCriteria(dateToSting);
        List<ImageDetails> imageDetailsList = imageRepository.selectImagesBySearchCriteria(imageName, 0, 1);
        Assertions.assertEquals(imageDetails.getId(), imageDetailsList.get(0).getId());
    }

    @Test
    @Transactional
    @Rollback
    public void testSelectImagesByName() {
        imageRepository.addImage(imageDetails);
        imageDetails = imageRepository.selectImagesByName(imageName, 0, 1).get(0);
        Assertions.assertNotEquals(null, imageDetails);
    }

    @Test
    @Transactional
    @Rollback
    public void selectImagesBySearchCriteriaByName() {
        imageRepository.addImage(imageDetails);
        imageDetails = imageRepository.selectImagesByName(imageName, 0, 1).get(0);
        List<ImageDetails> imageDetailsList = imageRepository.selectImagesBySearchCriteria(imageName, 0, 1);
        Assertions.assertEquals(imageDetails.getId(), imageDetailsList.get(0).getId());
    }

    @Test
    @Transactional
    @Rollback
    public void selectImagesBySearchCriteriaByTag() {
        imageRepository.addImage(imageDetails);
        imageDetails = imageRepository.selectImagesByName(imageName, 0, 1).get(0);
        List<ImageDetails> imageDetailsList = imageRepository.selectImagesBySearchCriteria(tagName, 0, 1);
        Assertions.assertEquals(imageDetails.getId(), imageDetailsList.get(0).getId());
    }

    @Test
    @Transactional
    @Rollback
    public void selectImagesBySearchCriteriaByDescription() {
        imageRepository.addImage(imageDetails);
        imageDetails = imageRepository.selectImagesByName(imageName, 0, 1).get(0);
        List<ImageDetails> imageDetailsList = imageRepository.selectImagesBySearchCriteria(imageDetails.getDescription(), 0, 1);
        Assertions.assertEquals(imageDetails.getId(), imageDetailsList.get(0).getId());
    }

    @Test
    @Transactional
    @Rollback
    public void selectImagesBySearchCriteriaByQuality() {
        imageRepository.addImage(imageDetails);
        imageDetails = imageRepository.selectImagesByName(imageName, 0, 1).get(0);
        List<ImageDetails> imageDetailsList = imageRepository.selectImagesBySearchCriteria(quality, 0, 1);
        Assertions.assertEquals(imageDetails.getId(), imageDetailsList.get(0).getId());
    }

    @Test
    @Transactional
    @Rollback
    public void countImagesBySearchCriteriaByTag() {
        imageRepository.addImage(imageDetails);
        imageDetails = imageRepository.selectImagesByName(imageName, 0, 1).get(0);
        long count = imageRepository.countImagesBySearchCriteria(tagName);
        Assertions.assertEquals(1, count);
    }

    @Test
    @Transactional
    @Rollback
    public void countImagesBySearchCriteriaByDescription() {
        imageRepository.addImage(imageDetails);
        imageDetails = imageRepository.selectImagesByName(imageName, 0, 1).get(0);
        long count = imageRepository.countImagesBySearchCriteria(imageDetails.getDescription());
        Assertions.assertEquals(1, count);
    }

    @Test
    @Transactional
    @Rollback
    public void countImagesBySearchCriteriaByQuality() {
        imageRepository.addImage(imageDetails);
        imageDetails = imageRepository.selectImagesByName(imageName, 0, 1).get(0);
        long count = imageRepository.countImagesBySearchCriteria(quality);
        Assertions.assertEquals(1, count);
    }

    @Test
    @Transactional
    @Rollback
    public void selectImagesBySearchCriteriaRespectsMaxResults() {
        imageRepository.addImage(imageDetails);
        imageRepository.addImage(getImageInitialized());
        imageRepository.addImage(getImageInitialized());
        Assertions.assertEquals(3, imageRepository.countImagesBySearchCriteria(imageName));
        Assertions.assertEquals(2, imageRepository.selectImagesBySearchCriteria(imageName, 0, 2).size());
        Assertions.assertEquals(1, imageRepository.selectImagesBySearchCriteria(imageName, 2, 2).size());
    }

    @Test
    @Transactional
    @Rollback
    public void testGetImageDetailsInitializedWithoutModalImage() {
        imageDetails.setModalImage(null);
        imageRepository.addImage(imageDetails);
        long id = imageRepository.selectImagesByName(imageName, 0, 1).get(0).getId();
        Assertions.assertNotNull(imageRepository.getImageDetailsInitialized(id));
    }

    @Test
    @Transactional
    @Rollback
    public void testGetImageDetailsInitializedNoImage() {
        Assertions.assertNull(imageRepository.getImageDetailsInitialized(12345));
    }

    @Test
    @Transactional
    @Rollback
    public void testDeleteImageNotSaved() {
        Assertions.assertFalse(imageRepository.deleteImage(new ImageDetails()));
    }

    @Test
    @Transactional
    @Rollback
    public void testAddImageWritesImmediately() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        String countSql = "SELECT COUNT(*) FROM POV_IMAGE_DETAILS WHERE IMAGE_NAME = ?";
        Assertions.assertTrue(imageRepository.addImage(imageDetails));
        Assertions.assertNotNull(imageDetails.getId());
        // plain JDBC on the same transaction only sees the row if it was flushed
        Assertions.assertEquals(1, jdbc.queryForObject(countSql, Integer.class, imageName));
    }

    @Test
    @Transactional
    @Rollback
    public void testAddImageTwice() {
        Assertions.assertTrue(imageRepository.addImage(imageDetails));
        Assertions.assertFalse(imageRepository.addImage(imageDetails));
    }

    @Test
    @Transactional
    @Rollback
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
    @Transactional
    @Rollback
    public void testAddImageToMissingFolder() {
        Folder missing = new Folder(folderName, insertDate);
        ReflectionTestUtils.setField(missing, "id", -1L);
        imageDetails.setFolder(missing);
        Assertions.assertFalse(imageRepository.addImage(imageDetails));
        imageDetails.setFolder(new Folder(folderName, insertDate));
        Assertions.assertFalse(imageRepository.addImage(imageDetails));
    }

    @Test
    @Transactional
    @Rollback
    public void testImageDetailsEquality() {
        ImageDetails unsaved = new ImageDetails();
        Assertions.assertEquals(unsaved, unsaved);
        Assertions.assertNotEquals(unsaved, new ImageDetails());
        Set<ImageDetails> set = new HashSet<>();
        set.add(unsaved);
        unsaved.setId(42);
        // still found after the id was assigned
        Assertions.assertTrue(set.contains(unsaved));

        imageRepository.addImage(imageDetails);
        ImageDetails saved = imageRepository.selectImagesByName(imageName, 0, 1).get(0);
        ImageDetails sameId = new ImageDetails();
        sameId.setId(saved.getId());
        Assertions.assertEquals(saved, sameId);
    }

    public ImageDetails getImageInitialized() {
        ImageDetails imageDetails = new ImageDetails();
        imageDetails.setModalImage(new Image());
//        imageDetails.setFolder(new Folder(folderName,insertDate));
        imageDetails.setDescription(description);
        imageDetails.setImage(new Image());
        imageDetails.setImageName("imageName");
        imageDetails.setInsertDate(insertDate);
        imageDetails.setQuality(quality);
        imageDetails.setTags(new ArrayList<>(Collections.singletonList(tagName)));

        return imageDetails;
    }
}
