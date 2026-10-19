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

    /**
     * @return false if a folder with the same name already exists
     */
    public boolean addFolder(Folder folder) {
        return folderRepository.addFolder(folder);
    }

    @Transactional(readOnly = true)
    public long countFolders() {
        return folderRepository.countFolders();
    }

    @Transactional(readOnly = true)
    public List<Folder> selectFolders(int from, int maxFolders) {
        return folderRepository.selectFolders(from, maxFolders);
    }

    @Transactional(readOnly = true)
    public Folder selectFolderById(long id) {
        return folderRepository.selectFolderById(id);
    }

    /**
     * @return false if the folder still contains images (it is not deleted) or no longer exists
     */
    public boolean deleteFolder(Folder folder) {
        return folderRepository.deleteFolder(folder);
    }

    /**
     * @return false if another folder already has this name or the folder no longer exists
     */
    public boolean updateFolder(Folder folder) {
        return folderRepository.updateFolder(folder);
    }
}
