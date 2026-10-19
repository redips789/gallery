package lt.insoft.training.bl.Service;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import lt.insoft.training.model.Folder;
import lt.insoft.training.model.ImageDetails;
import lt.insoft.training.bl.Repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by povilas.jegelevicius on 2017-02-22.
 */
@Component("imageService")
@Transactional
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public void add(ImageDetails imageDetails) {
        imageRepository.addImage(imageDetails);
    }

    public void updateImage(ImageDetails imageDetails) {
        imageRepository.updateImage(imageDetails);
    }

    public List<ImageDetails> selectFolderImages(Folder folder, int from, int maxImages) {
        return imageRepository.selectFolderImages(folder, from, maxImages);
    }

    public long countImagesInFolder(Folder folder) {
        return imageRepository.countImagesInFolder(folder);
    }

    public ImageDetails getImageDetailsInitialized(long id) {
        return imageRepository.getImageDetailsInitialized(id);
    }

    public void deleteImage(ImageDetails imageDetails) {
        imageRepository.deleteImage(imageDetails);
    }

    public long countImagesBySearchCriteria(String searchInput) {
        return imageRepository.countImagesBySearchCriteria(searchInput);
    }

    public List<ImageDetails> selectImagesBySearchCriteria(String searchInput, int from, int to) {
        return imageRepository.selectImagesBySearchCriteria(searchInput, from, to);
    }


}
