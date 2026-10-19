package lt.insoft.training.test;

import java.time.LocalDateTime;
import lt.insoft.training.bl.Service.FolderService;
import lt.insoft.training.bl.Service.UserService;
import lt.insoft.training.bl.Service.UtilityService;
import lt.insoft.training.model.Folder;
import lt.insoft.training.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


@ContextConfiguration(classes = TestConfiguration.class)
@ExtendWith(SpringExtension.class)
public class TestServices {

    @Autowired
    private FolderService folderService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    // has a time of day, so searching by date must match the whole day
    private LocalDateTime insertDate = LocalDateTime.of(2017, 4, 6, 14, 30);

    @Test
    @Transactional
    @Rollback
    public void testUpdateFolderKeepingSameName() {
        Folder folder = new Folder("test", insertDate);
        folderService.addFolder(folder);
        Assertions.assertTrue(folderService.updateFolder(folder));
    }

    @Test
    @Transactional
    @Rollback
    public void testAddFolderWithSameName() {
        Assertions.assertTrue(folderService.addFolder(new Folder("test", insertDate)));
        Assertions.assertFalse(folderService.addFolder(new Folder("test", insertDate)));
    }

    @Test
    @Transactional
    @Rollback
    public void testAddUser() {
        Assertions.assertTrue(userService.addUser(new User("user", "password")));
        Assertions.assertEquals("user", userDetailsService.loadUserByUsername("user").getUsername());
    }

    @Test
    public void testParseDate() {
        UtilityService utilityService = new UtilityService();
        Assertions.assertNotNull(utilityService.parseDateService("2017-04-06"));
        Assertions.assertNotNull(utilityService.parseDateService("2017-4-6"));
        Assertions.assertNull(utilityService.parseDateService("2017-04-06 beach"));
        Assertions.assertNull(utilityService.parseDateService("2017-02-30"));
        Assertions.assertNull(utilityService.parseDateService("beach"));
    }
}
