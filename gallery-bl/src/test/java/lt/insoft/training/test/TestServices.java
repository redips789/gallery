package lt.insoft.training.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lt.insoft.training.bl.Service.FolderService;
import lt.insoft.training.bl.Service.ImageService;
import lt.insoft.training.bl.Service.UserService;
import lt.insoft.training.bl.Service.UtilityService;
import lt.insoft.training.model.Folder;
import lt.insoft.training.model.Image;
import lt.insoft.training.model.ImageDetails;
import lt.insoft.training.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Each test runs in a transaction that is rolled back afterwards, unless it says otherwise.
 */
@ContextConfiguration(classes = TestConfiguration.class)
@ExtendWith(SpringExtension.class)
@Transactional
public class TestServices {

    @Autowired
    private FolderService folderService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;

    private final LocalDateTime insertDate = LocalDateTime.of(2017, 4, 6, 14, 30);

    private final UtilityService utilityService = new UtilityService();

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private ImageDetails newImage(Folder folder) {
        ImageDetails imageDetails = new ImageDetails();
        imageDetails.setImage(new Image());
        imageDetails.setThumbnail(new Image());
        imageDetails.setModalImage(new Image());
        imageDetails.setImageName("beach");
        imageDetails.setInsertDate(insertDate);
        imageDetails.setFolder(folder);
        imageDetails.setTags(new ArrayList<>(List.of("sea")));
        return imageDetails;
    }

    /**
     * Runs without a test transaction, the way the pages call the services: if the services' @Transactional
     * settings were not applied, there would be no transaction to write in and this would fail.
     */
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testServiceCommitsItsOwnTransaction() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        String countSql = "SELECT COUNT(*) FROM POV_FOLDER WHERE NAME = ?";
        Folder folder = new Folder("committed", insertDate);
        Assertions.assertTrue(folderService.addFolder(folder));
        try {
            Assertions.assertEquals(1, jdbc.queryForObject(countSql, Integer.class, "committed"));
        } finally {
            Assertions.assertTrue(folderService.deleteFolder(folder));
        }
        Assertions.assertEquals(0, jdbc.queryForObject(countSql, Integer.class, "committed"));
    }

    @Test
    public void testAddFolderWithSameName() {
        Assertions.assertTrue(folderService.addFolder(new Folder("test", insertDate)));
        Assertions.assertFalse(folderService.addFolder(new Folder("test", insertDate)));
    }

    @Test
    public void testUpdateFolderKeepingSameName() {
        Folder folder = new Folder("test", insertDate);
        folderService.addFolder(folder);
        flushAndClear();
        Assertions.assertTrue(folderService.updateFolder(folder));
    }

    @Test
    public void testFolderPaging() {
        for (int i = 0; i < 3; i++) {
            folderService.addFolder(new Folder("folder" + i, insertDate.plusDays(i)));
        }
        flushAndClear();
        Assertions.assertEquals(3, folderService.countFolders());
        Assertions.assertEquals(List.of("folder2"),
                folderService.selectFolders(2, 2).stream().map(Folder::getFolderName).toList());
    }

    @Test
    public void testImageLifecycle() {
        Folder folder = new Folder("holidays", insertDate);
        folderService.addFolder(folder);
        ImageDetails imageDetails = newImage(folder);
        Assertions.assertTrue(imageService.add(imageDetails));
        flushAndClear();

        Assertions.assertEquals(1, imageService.countImagesInFolder(folder));
        Assertions.assertEquals("beach", imageService.selectFolderImages(folder, 0, 8).get(0).getImageName());
        Assertions.assertEquals(1, imageService.countImagesBySearchCriteria("sea"));
        Assertions.assertEquals(1, imageService.selectImagesBySearchCriteria("2017-04-06", 0, 8).size());

        ImageDetails edited = imageService.getImageDetailsInitialized(imageDetails.getId());
        entityManager.detach(edited);
        edited.setImageName("sunset");
        Assertions.assertTrue(imageService.updateImage(edited));
        flushAndClear();
        Assertions.assertEquals("sunset", imageService.getImageDetailsInitialized(imageDetails.getId()).getImageName());

        // a folder with a picture in it is kept
        Assertions.assertFalse(folderService.deleteFolder(folder));
        Assertions.assertTrue(imageService.deleteImage(imageDetails));
        flushAndClear();
        Assertions.assertNull(imageService.getImageDetailsInitialized(imageDetails.getId()));
        Assertions.assertFalse(imageService.deleteImage(imageDetails));
        Assertions.assertTrue(folderService.deleteFolder(folder));
        flushAndClear();
        Assertions.assertNull(folderService.selectFolderById(folder.getId()));
    }

    @Test
    public void testAddUser() {
        Assertions.assertTrue(userService.addUser(new User("user", "password")));
        flushAndClear();
        UserDetails userDetails = userDetailsService.loadUserByUsername("user");
        Assertions.assertEquals("user", userDetails.getUsername());
        Assertions.assertEquals("password", userDetails.getPassword());
        Assertions.assertEquals(List.of("ROLE_USER"),
                userDetails.getAuthorities().stream().map(Object::toString).toList());
    }

    @Test
    public void testAddUserWithSameName() {
        Assertions.assertTrue(userService.addUser(new User("user", "password")));
        Assertions.assertFalse(userService.addUser(new User("user", "other")));
        flushAndClear();
        Assertions.assertEquals("password", userDetailsService.loadUserByUsername("user").getPassword());
    }

    @Test
    public void testLoadUnknownUser() {
        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nobody"));
    }

    @Test
    public void testParseDate() {
        LocalDate expected = LocalDate.of(2017, 4, 6);
        Assertions.assertEquals(expected, utilityService.parseDateService("2017-04-06"));
        Assertions.assertEquals(expected, utilityService.parseDateService("2017-4-6"));
        Assertions.assertEquals(expected, utilityService.parseDateService(" 2017-04-06 "));
        Assertions.assertNull(utilityService.parseDateService("2017-04-06 beach"));
        Assertions.assertNull(utilityService.parseDateService("2017-02-30"));
        Assertions.assertNull(utilityService.parseDateService("beach"));
    }

    @Test
    public void testEscapeSpecialCharacters() {
        Assertions.assertEquals("50!% off!_now!!", utilityService.escapeSpecialCharacters("50% off_now!"));
        Assertions.assertEquals("beach", utilityService.escapeSpecialCharacters("beach"));
    }
}
