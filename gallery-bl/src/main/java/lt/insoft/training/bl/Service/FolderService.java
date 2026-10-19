package lt.insoft.training.bl.Service;

import lt.insoft.training.bl.Repository.FolderRepository;
import lt.insoft.training.model.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by povilas.jegelevicius on 2017-03-02.
 */
@Component("folderService")
@Transactional
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;

    public boolean addFolder(Folder folder) {
        if (folderRepository.selectFolderByName(folder.getFolderName()) == null) {
            folderRepository.addFolder(folder);
            return true;
        }
        return false;
    }

    public long countFolders() {
        return folderRepository.countFolders();
    }

    public List<Folder> selectFolders(int from, int maxFolders) {
        return folderRepository.selectFolders(from, maxFolders);
    }

    public Folder selectFolderById(int id) {
        return folderRepository.selectFolderById(id);
    }

    /**
     * @return false if the folder still contains images (it is not deleted) or no longer exists
     */
    public boolean deleteFolder(Folder folder) {
        return folderRepository.deleteFolder(folder);
    }

    public boolean updateFolder(Folder folder) {
        if (folderRepository.selectFolderByName(folder.getFolderName()) == null) {
            folderRepository.updateFolder(folder);
            return true;
        }
        return false;
    }
}
