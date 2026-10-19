package lt.insoft.training.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lt.insoft.training.bl.Repository.FolderRepository;
import lt.insoft.training.bl.Repository.ImageRepository;
import lt.insoft.training.model.Folder;
import lt.insoft.training.model.ImageDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Each test runs in a transaction that is rolled back afterwards.
 *
 * Created by povilas.jegelevicius on 2017-04-04.
 */
@ContextConfiguration(classes = TestConfiguration.class)
@ExtendWith(SpringExtension.class)
@Transactional
public class TestFolderRepository {

    private static final long MISSING_ID = -1;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private ImageRepository imageRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final String folderName = "test";

    private final LocalDateTime insertDate = LocalDateTime.of(2017, 4, 6, 14, 30);

    private final Folder folder = new Folder(folderName, insertDate);

    /**
     * Writes pending changes and empties the persistence context, so the next read comes from the
     * database instead of returning the objects the test already holds.
     */
    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void testAddFolder() {
        Assertions.assertTrue(folderRepository.addFolder(folder));
        Assertions.assertNotNull(folder.getId());
        flushAndClear();
        Folder folderFromDB = folderRepository.selectFolderByName(folderName);
        Assertions.assertEquals(folder.getId(), folderFromDB.getId());
        Assertions.assertEquals(insertDate, folderFromDB.getInsertDate());
    }

    @Test
    public void testAddFolderWithSameName() {
        Assertions.assertTrue(folderRepository.addFolder(folder));
        Assertions.assertFalse(folderRepository.addFolder(new Folder(folderName, insertDate)));
        Assertions.assertEquals(1, folderRepository.countFolders());
    }

    @Test
    public void testDeleteFolder() {
        folderRepository.addFolder(folder);
        flushAndClear();
        Assertions.assertTrue(folderRepository.deleteFolder(folder));
        flushAndClear();
        Assertions.assertNull(folderRepository.selectFolderByName(folderName));
    }

    @Test
    public void testDeleteFolderWithImages() {
        folderRepository.addFolder(folder);
        ImageDetails imageDetails = new ImageDetails();
        imageDetails.setImageName("imageName");
        imageDetails.setFolder(folder);
        imageRepository.addImage(imageDetails);
        flushAndClear();
        Assertions.assertFalse(folderRepository.deleteFolder(folder));
        flushAndClear();
        Assertions.assertNotNull(folderRepository.selectFolderByName(folderName));
    }

    @Test
    public void testDeleteFolderNotSaved() {
        Assertions.assertFalse(folderRepository.deleteFolder(folder));
    }

    @Test
    public void testUpdateFolder() {
        folderRepository.addFolder(folder);
        flushAndClear();
        // detached copy, like the one a page holds between requests
        Folder renamed = folderRepository.selectFolderById(folder.getId());
        entityManager.detach(renamed);
        renamed.setFolderName("renamed");
        Assertions.assertTrue(folderRepository.updateFolder(renamed));
        flushAndClear();
        Assertions.assertNotNull(folderRepository.selectFolderByName("renamed"));
        Assertions.assertNull(folderRepository.selectFolderByName(folderName));
    }

    @Test
    public void testUpdateFolderKeepingSameName() {
        folderRepository.addFolder(folder);
        flushAndClear();
        Assertions.assertTrue(folderRepository.updateFolder(folder));
    }

    @Test
    public void testUpdateFolderToNameOfAnotherFolder() {
        folderRepository.addFolder(folder);
        Folder other = new Folder("other", insertDate);
        folderRepository.addFolder(other);
        flushAndClear();
        other.setFolderName(folderName);
        Assertions.assertFalse(folderRepository.updateFolder(other));
        flushAndClear();
        Assertions.assertEquals("other", folderRepository.selectFolderById(other.getId()).getFolderName());
    }

    @Test
    public void testUpdateDeletedFolder() {
        folderRepository.addFolder(folder);
        flushAndClear();
        Assertions.assertTrue(folderRepository.deleteFolder(folder));
        flushAndClear();
        folder.setFolderName("renamed");
        Assertions.assertFalse(folderRepository.updateFolder(folder));
        Assertions.assertNull(folderRepository.selectFolderByName("renamed"));
    }

    @Test
    public void testSelectFolderById() {
        folderRepository.addFolder(folder);
        flushAndClear();
        Assertions.assertEquals(folderName, folderRepository.selectFolderById(folder.getId()).getFolderName());
    }

    @Test
    public void testSelectFolderByIdThatDoesntExist() {
        Assertions.assertNull(folderRepository.selectFolderById(MISSING_ID));
    }

    @Test
    public void testSelectFoldersOrderAndPaging() {
        Folder newest = new Folder("newest", insertDate.plusDays(2));
        Folder oldest = new Folder("oldest", insertDate.minusDays(2));
        folderRepository.addFolder(newest);
        folderRepository.addFolder(folder);
        folderRepository.addFolder(oldest);
        flushAndClear();
        // oldest first
        List<Folder> firstPage = folderRepository.selectFolders(0, 2);
        Assertions.assertEquals(List.of("oldest", folderName),
                firstPage.stream().map(Folder::getFolderName).toList());
        List<Folder> secondPage = folderRepository.selectFolders(2, 2);
        Assertions.assertEquals(List.of("newest"), secondPage.stream().map(Folder::getFolderName).toList());
    }

    @Test
    public void testSelectFoldersNoFolders() {
        Assertions.assertTrue(folderRepository.selectFolders(0, 1).isEmpty());
    }

    @Test
    public void testCountFolders() {
        Assertions.assertEquals(0, folderRepository.countFolders());
        folderRepository.addFolder(folder);
        Assertions.assertEquals(1, folderRepository.countFolders());
    }

    @Test
    public void testSelectFolderByName() {
        folderRepository.addFolder(folder);
        flushAndClear();
        Assertions.assertEquals(folder.getId(), folderRepository.selectFolderByName(folderName).getId());
    }

    @Test
    public void testSelectFolderByNameThatDoesntExist() {
        Assertions.assertNull(folderRepository.selectFolderByName(folderName));
    }
}
