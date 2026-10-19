package lt.insoft.training.test;

import java.time.LocalDateTime;
import lt.insoft.training.bl.Repository.FolderRepository;
import lt.insoft.training.bl.Repository.ImageRepository;
import lt.insoft.training.model.Folder;
import lt.insoft.training.model.ImageDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.Math.toIntExact;

/**
 * Created by povilas.jegelevicius on 2017-04-04.
 */
@ContextConfiguration(classes = TestConfiguration.class)
@ExtendWith(SpringExtension.class)
public class TestFolderRepository {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private ImageRepository imageRepository;

    private String folderName="test";

    // has a time of day, so searching by date must match the whole day
    private LocalDateTime insertDate = LocalDateTime.of(2017, 4, 6, 14, 30);
    private Folder folder = new Folder(folderName, insertDate);

    @Test
    @Transactional
    @Rollback
    public void testDeleteFolderWithImages() {
        folderRepository.addFolder(folder);
        ImageDetails imageDetails = new ImageDetails();
        imageDetails.setImageName("imageName");
        imageDetails.setFolder(folder);
        imageRepository.addImage(imageDetails);
        Assertions.assertFalse(folderRepository.deleteFolder(folder));
        Assertions.assertNotNull(folderRepository.selectFolderByName(folderName));
    }


    @Test
    @Transactional
    @Rollback
    public void testAddFolder() {
        folderRepository.addFolder(folder);
        Folder folderFromDB = folderRepository.selectFolderByName(folderName);
        Assertions.assertEquals(folder.getFolderName(), folderFromDB.getFolderName());
    }

    @Test
    @Transactional
    @Rollback
    public void testAddFolderAddFolderWithSameName() {
        Assertions.assertEquals(true, folderRepository.addFolder(folder));
        folder = new Folder(folderName, insertDate);
        Assertions.assertEquals(false, folderRepository.addFolder(folder));
    }

    @Test
    @Transactional
    @Rollback
    public void testDeleteFolder() {
        folderRepository.addFolder(folder);
        Folder folderFromDB = folderRepository.selectFolderByName(folderName);
        folderRepository.deleteFolder(folderFromDB);
        folderFromDB = folderRepository.selectFolderByName(folderName);
        Assertions.assertEquals(null, folderFromDB);
    }

    @Test
    @Transactional
    @Rollback
    public void testDeleteFolderThatDoesntExist() {
        Assertions.assertEquals(false, folderRepository.deleteFolder(folder));
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateFolder() {
        folderRepository.addFolder(folder);
        Folder folderFromDB = folderRepository.selectFolderByName(folderName);
        folderFromDB.setFolderName("testas2");
        Assertions.assertEquals(folderRepository.updateFolder(folderFromDB), true);
        folderFromDB = folderRepository.selectFolderByName("testas2");
        Assertions.assertNotEquals(null, folderFromDB);
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateFolderThatDoesntExist() {
        folderRepository.addFolder(folder);
        Folder folderFromDB = folderRepository.selectFolderByName(folderName);
        folderRepository.deleteFolder(folder);
        folderFromDB.setFolderName("testas2");
        Assertions.assertEquals(folderRepository.updateFolder(folderFromDB), false);
        folderFromDB = folderRepository.selectFolderByName("testas2");
        Assertions.assertEquals(null, folderFromDB);
    }

    @Test
    @Transactional
    @Rollback
    public void testSelectFolderById() {
        folderRepository.addFolder(folder);
        Folder folderFromDB = folderRepository.selectFolderByName(folderName);
        folderRepository.selectFolderById(folderFromDB.getId());
        Assertions.assertEquals(folderFromDB, folderRepository.selectFolderById(toIntExact(folderFromDB.getId())));
    }

    @Test
    @Transactional
    @Rollback
    public void testSelectFolderByIdThatDoesntExist() {
        Folder folder = folderRepository.selectFolderById(toIntExact(5));
        Assertions.assertEquals(null, folder);
    }


    @Test
    @Transactional
    @Rollback
    public void testSelectFolders() {
        folderRepository.addFolder(folder);
        Folder folderFromDB = folderRepository.selectFolderByName(folderName);
        Assertions.assertEquals(folderFromDB, folderRepository.selectFolders(0, 1).get(0));
        //Assertions.assertEquals(folderRepository.countFolders(), 1);
    }

    @Test
    @Transactional
    @Rollback
    public void testSelectFoldersNoFolders() {
        Assertions.assertEquals(true, folderRepository.selectFolders(0, 1).isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    public void countFolders() {
        folderRepository.addFolder(folder);
        Assertions.assertEquals(folderRepository.countFolders(), 1);
    }

    @Test
    @Transactional
    @Rollback
    public void selectFolderByName() {
        folderRepository.addFolder(folder);
        Assertions.assertEquals(folderName, folderRepository.selectFolderByName(folderName).getFolderName());
    }

    @Test
    @Transactional
    @Rollback
    public void selectFolderByNameNotExist() {
        Assertions.assertEquals(null, folderRepository.selectFolderByName(folderName));
    }
}
