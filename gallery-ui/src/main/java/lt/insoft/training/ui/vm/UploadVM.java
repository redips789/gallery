package lt.insoft.training.ui.vm;

import lt.insoft.training.bl.Service.ImageService;
import lt.insoft.training.model.*;
import lt.insoft.training.ui.vm.util.ImageScaler;
import lt.insoft.training.ui.vm.validators.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.image.*;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by povilas.jegelevicius on 2017-02-24.
 */
@ToServerCommand("update")
@VariableResolver({DelegatingVariableResolver.class})
public class UploadVM {

    @WireVariable
    private ImageService imageService;

    Log logger = LogFactory.getLog(UploadVM.class);

    private byte[] uploadedPicture;

    // scaled once on upload; the getter is called on every render
    private byte[] uploadedPicturePreview;

    private String description;

    private String quality;

    private LocalDateTime insertDate;

    private Folder selectedFolder;

    private String tags;

    private String imageName;

    private String imageValidationMessage;

    private byte[] placeholderImage;

    private NameValidator nameValidator = new NameValidator();

    private ImageDescriptionValidator imageDescriptionValidator = new ImageDescriptionValidator();

    private ImageTagsValidator imageTagsValidator = new ImageTagsValidator();

    private ImageInsertDateValidator imageInsertDateValidator = new ImageInsertDateValidator();

    private ImageValidator imageValidator = new ImageValidator();

    private final int thumbnailMarginLength = 370;

    private final int addPaddingToBottomOfImage = 60;

    private final int modalMarginLength = 800;

    private boolean imageVisible;

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public LocalDateTime getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(LocalDateTime insertDate) {
        this.insertDate = insertDate;
    }

    public ImageService getImageService() {
        return imageService;
    }

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    public byte[] getUploadedPicture() {
        if (uploadedPicture == null) {
            return placeholderImage;
        }
        return uploadedPicturePreview;
    }

    public void setUploadedPicture(byte[] uploadedPicture) {
        this.uploadedPicture = uploadedPicture;
    }

    public Folder getSelectedFolder() {
        return selectedFolder;
    }

    public void setSelectedFolder(Folder selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public NameValidator getNameValidator() {
        return nameValidator;
    }

    public ImageDescriptionValidator getImageDescriptionValidator() {
        return imageDescriptionValidator;
    }

    public ImageTagsValidator getImageTagsValidator() {
        return imageTagsValidator;
    }

    public ImageInsertDateValidator getImageInsertDateValidator() {
        return imageInsertDateValidator;
    }

    public ImageValidator getImageValidator() {
        return imageValidator;
    }

    public String getImageValidationMessage() {
        return imageValidationMessage;
    }

    public void setImageValidationMessage(String imageValidationMessage) {
        this.imageValidationMessage = imageValidationMessage;
    }

    public boolean isImageVisible() {
        return imageVisible;
    }

    public void setImageVisible(boolean imageVisible) {
        this.imageVisible = imageVisible;
    }

    public byte[] getPlaceholderImage() {
        return placeholderImage;
    }

    public void setPlaceholderImage(byte[] placeholderImage) {
        this.placeholderImage = placeholderImage;
    }

    @Init
    public void init(@ExecutionArgParam("folder") Folder folder) {
        //uploadedPicture = null;
        selectedFolder = folder;
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        //Messagebox.show("The backup has been done.");
        try (InputStream is = classloader.getResourceAsStream("Placeholder.png")) {
            placeholderImage = IOUtils.toByteArray(is);
            //uploadedPicture = placeholderImage;
        } catch (IOException e) {
            logger.error("Error occured, returning folderimg==null", e);
        }
        return;
    }

    @Command
    @NotifyChange({"uploadedPicture", "quality", "imageVisible", "insertDate", "imageValidationMessage"})
    public void doUploadedPicture(@BindingParam("media") Media uploadedPicture) {
        byte[] preview = uploadedPicture instanceof org.zkoss.image.Image
                ? ImageScaler.scaleImage(uploadedPicture.getByteData(), thumbnailMarginLength, 0)
                : null;
        if (preview != null) {
            this.uploadedPicture = uploadedPicture.getByteData();
            this.uploadedPicturePreview = preview;
            AImage im = (AImage) uploadedPicture;
            quality = Integer.toString(im.getHeight()) + 'x' + Integer.toString(im.getWidth());
            insertDate = LocalDateTime.now();
            imageVisible = true;
            imageValidationMessage = "";
        } else {
            // not an image, or a format Java can't read (ImageScaler returns null)
            this.uploadedPicture = null;
            this.uploadedPicturePreview = null;
            imageVisible = false;
            imageValidationMessage = "Please upload a picture";
        }
    }

    @Command
    @NotifyChange({"imageValidationMessage"})
    public void submit() {
        if (uploadedPicture == null) {
            imageValidationMessage = "Please upload a picture";
            return;
        }
        ImageDetails imageDetails = new ImageDetails();
        imageDetails.setModalImage(new lt.insoft.training.model.Image(ImageScaler.scaleImage(uploadedPicture, modalMarginLength, 0)));
        imageDetails.setFolder(selectedFolder);
        imageDetails.setThumbnail(new lt.insoft.training.model.Image(ImageScaler.scaleImage(uploadedPicture, thumbnailMarginLength, addPaddingToBottomOfImage)));
        imageDetails.setDescription(description);
        imageDetails.setImage(new lt.insoft.training.model.Image(uploadedPicture));
        imageDetails.setImageName(imageName);
        imageDetails.setInsertDate(insertDate);
        imageDetails.setQuality(quality);
        if (tags != null && !tags.isEmpty()) {
            imageDetails.setTags(new ArrayList<>(Arrays.asList(tags.split(","))));
        }
        if (!imageService.add(imageDetails)) {
            imageValidationMessage = "The picture could not be saved";
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("folder", selectedFolder);
        map.put("page", "folder.zul");
        BindUtils.postGlobalCommand(null, EventQueues.DESKTOP, "selectFolder", map);
    }
}
