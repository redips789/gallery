package lt.insoft.training.test;

import lt.insoft.training.bl.Repository.FolderRepository;
import lt.insoft.training.model.Folder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.Math.toIntExact;

/**
 * Created by povilas.jegelevicius on 2017-04-04.
 */
@ContextConfiguration(classes = TestConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class TestFolderRepository {

    @Autowired
    private FolderRepository folderRepository;
    private String folderName="test";

    private Calendar cal = new GregorianCalendar(2017, 03, 06);
    private Folder folder = new Folder(folderName, cal.getTime());


    @Test
    @Transactional
    @Rollback
    public void testAddFolder() {
        folderRepository.addFolder(folder);
        Folder folderFromDB = folderRepository.selectFolderByName(folderName);
        Assert.assertEquals(folder.getFolderName(), folderFromDB.getFolderName());
    }

    @Test
    @Transactional
    @Rollback
    public void testAddFolderAddFolderWithSameName() {
        Assert.assertEquals(true, folderRepository.addFolder(folder));
        folder = new Folder(folderName, cal.getTime());
        Assert.assertEquals(false, folderRepository.addFolder(folder));
    }

    @Test
    @Transactional
    @Rollback
    public void testDeleteFolder() {
        folderRepository.addFolder(folder);
        Folder folderFromDB = folderRepository.selectFolderByName(folderName);
        folderRepository.deleteFolder(folderFromDB);
        folderFromDB = folderRepository.selectFolderByName(folderName);
        Assert.assertEquals(null, folderFromDB);
    }

    @Test
    @Transactional
    @Rollback
    public void testDeleteFolderThatDoesntExist() {
        Assert.assertEquals(false, folderRepository.deleteFolder(folder));
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateFolder() {
        folderRepository.addFolder(folder);
        Folder folderFromDB = folderRepository.selectFolderByName(folderName);
        folderFromDB.setFolderName("testas2");
        Assert.assertEquals(folderRepository.updateFolder(folderFromDB), true);
        folderFromDB = folderRepository.selectFolderByName("testas2");
        Assert.assertNotEquals(null, folderFromDB);
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateFolderThatDoesntExist() {
        folderRepository.addFolder(folder);
        Folder folderFromDB = folderRepository.selectFolderByName(folderName);
        folderRepository.deleteFolder(folder);
        folderFromDB.setFolderName("testas2");
        Assert.assertEquals(folderRepository.updateFolder(folderFromDB), false);
        folderFromDB = folderRepository.selectFolderByName("testas2");
        Assert.assertEquals(null, folderFromDB);
    }

    @Test
    @Transactional
    @Rollback
    public void testSelectFolderById() {
        folderRepository.addFolder(folder);
        Folder folderFromDB = folderRepository.selectFolderByName(folderName);
        folderRepository.selectFolderById(folderFromDB.getId());
        Assert.assertEquals(folderFromDB, folderRepository.selectFolderById(toIntExact(folderFromDB.getId())));
    }

    @Test
    @Transactional
    @Rollback
    public void testSelectFolderByIdThatDoesntExist() {
        Folder folder = folderRepository.selectFolderById(toIntExact(5));
        Assert.assertEquals(null, folder);
    }


    @Test
    @Transactional
    @Rollback
    public void testSelectFolders() {
        folderRepository.addFolder(folder);
        Folder folderFromDB = folderRepository.selectFolderByName(folderName);
        Assert.assertEquals(folderFromDB, folderRepository.selectFolders(0, 1).get(0));
        //Assert.assertEquals(folderRepository.countFolders(), 1);
    }

    @Test
    @Transactional
    @Rollback
    public void testSelectFoldersNoFolders() {
        Assert.assertEquals(true, folderRepository.selectFolders(0, 1).isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    public void countFolders() {
        folderRepository.addFolder(folder);
        Assert.assertEquals(folderRepository.countFolders(), 1);
    }

    @Test
    @Transactional
    @Rollback
    public void selectFolderByName() {
        folderRepository.addFolder(folder);
        Assert.assertEquals(folderName, folderRepository.selectFolderByName(folderName).getFolderName());
    }

    @Test
    @Transactional
    @Rollback
    public void selectFolderByNameNotExist() {
        Assert.assertEquals(null, folderRepository.selectFolderByName(folderName));
    }
}
