package lt.insoft.training.bl.Repository;

import lt.insoft.training.model.Folder;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by povilas.jegelevicius on 2017-03-02.
 */
public interface FolderRepository {

    List<Folder> selectFolders(int from, int maxFolders);

    boolean addFolder(Folder folder);

    boolean deleteFolder(Folder folder);

    boolean updateFolder(Folder folder);

    long countFolders();

    Folder selectFolderById(long id);

    Folder selectFolderByName(String name);
}
