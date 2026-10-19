package lt.insoft.training.bl.Repository;

import lt.insoft.training.model.Folder;
import lt.insoft.training.model.ImageDetails;

import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

/**
 * Created by povilas.jegelevicius on 2017-02-22.
 */
public interface ImageRepository {

    List<ImageDetails> selectFolderImages(Folder folder, int from, int maxImages);

    boolean addImage(ImageDetails imageDetails);

    boolean updateImage(ImageDetails imageDetails);

    boolean deleteImage(ImageDetails imageDetails);

    ImageDetails getImageDetailsInitialized(long id);

    long countImagesInFolder(Folder folder);

    long countImagesBySearchCriteria(String searchQuery);

    List<ImageDetails> selectImagesByName(String name, int from, int maxResults);

    List<ImageDetails> selectImagesBySearchCriteria(String searchQuery, int from, int maxResults);
}
