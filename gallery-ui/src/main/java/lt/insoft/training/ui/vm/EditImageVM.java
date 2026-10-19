package lt.insoft.training.ui.vm;

import lt.insoft.training.bl.Service.FolderService;
import lt.insoft.training.bl.Service.ImageService;
import lt.insoft.training.model.ImageDetails;
import lt.insoft.training.ui.vm.validators.ImageDescriptionValidator;
import lt.insoft.training.ui.vm.validators.NameValidator;
import lt.insoft.training.ui.vm.validators.ImageTagsValidator;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Messagebox;

import java.util.*;


/**
 * Created by povilas.jegelevicius on 2017-03-13.
 */
@VariableResolver({DelegatingVariableResolver.class})
public class EditImageVM {

    @WireVariable
    private ImageService imageService;

    private ImageDetails selectedImage;

    private NameValidator nameValidator = new NameValidator();

    private ImageDescriptionValidator imageDescriptionValidator = new ImageDescriptionValidator();

    private ImageTagsValidator imageTagsValidator = new ImageTagsValidator();

    private String tags;

    public ImageDetails getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(ImageDetails selectedImage) {
        this.selectedImage = selectedImage;
    }

    public NameValidator getNameValidator() {
        return nameValidator;
    }

    public void setNameValidator(NameValidator nameValidator) {
        this.nameValidator = nameValidator;
    }

    public ImageDescriptionValidator getImageDescriptionValidator() {
        return imageDescriptionValidator;
    }

    public void setImageDescriptionValidator(ImageDescriptionValidator imageDescriptionValidator) {
        this.imageDescriptionValidator = imageDescriptionValidator;
    }

    public ImageTagsValidator getImageTagsValidator() {
        return imageTagsValidator;
    }

    public void setImageTagsValidator(ImageTagsValidator imageTagsValidator) {
        this.imageTagsValidator = imageTagsValidator;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Init
    public void init(@ExecutionArgParam("image") ImageDetails imageDetails) {
        selectedImage = imageDetails;
        tags = imageDetails.getTags() == null ? "" : String.join(",", imageDetails.getTags());
    }

    @Command
    public void submit(){
        List<String> tagsAfterEdit = new ArrayList<String>();
        if(tags != null && !tags.isEmpty()) {
            tagsAfterEdit.addAll(Arrays.asList(tags.split(",")));
        }
        selectedImage.setTags(tagsAfterEdit);
        if (!imageService.updateImage(selectedImage)) {
            Messagebox.show("This picture no longer exists.", "Picture not found",
                    Messagebox.OK, Messagebox.EXCLAMATION);
        }
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("folder",selectedImage.getFolder());
        map.put("page","folder.zul");
        BindUtils.postGlobalCommand(null, EventQueues.DESKTOP,"selectFolder",map);
    }
}
