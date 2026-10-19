package lt.insoft.training.bl.Service;

import lt.insoft.training.model.Folder;
import lt.insoft.training.model.ImageDetails;
import lt.insoft.training.bl.Repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by povilas.jegelevicius on 2017-02-22.
 */
@Component("imageService")
@Transactional
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public boolean add(ImageDetails imageDetails) {
        return imageRepository.addImage(imageDetails);
    }

    /**
     * @return false if the image no longer exists
     */
    public boolean updateImage(ImageDetails imageDetails) {
        return imageRepository.updateImage(imageDetails);
    }

    @Transactional(readOnly = true)
    public List<ImageDetails> selectFolderImages(Folder folder, int from, int maxImages) {
        return imageRepository.selectFolderImages(folder, from, maxImages);
    }

    @Transactional(readOnly = true)
    public long countImagesInFolder(Folder folder) {
        return imageRepository.countImagesInFolder(folder);
    }

    /**
     * @return the image with its full-size image, modal image and tags loaded, or null if it no longer exists
     */
    @Transactional(readOnly = true)
    public ImageDetails getImageDetailsInitialized(long id) {
        return imageRepository.getImageDetailsInitialized(id);
    }

    /**
     * @return false if the image no longer exists
     */
    public boolean deleteImage(ImageDetails imageDetails) {
        return imageRepository.deleteImage(imageDetails);
    }

    @Transactional(readOnly = true)
    public long countImagesBySearchCriteria(String searchInput) {
        return imageRepository.countImagesBySearchCriteria(searchInput);
    }

    @Transactional(readOnly = true)
    public List<ImageDetails> selectImagesBySearchCriteria(String searchInput, int from, int maxResults) {
        return imageRepository.selectImagesBySearchCriteria(searchInput, from, maxResults);
    }
}
