package lt.insoft.training.ui.vm;

import lt.insoft.training.bl.Service.FolderService;
import lt.insoft.training.bl.Service.ImageService;
import lt.insoft.training.model.ImageDetails;
import lt.insoft.training.ui.vm.validators.SearchValidator;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.event.PagingEvent;

import java.util.*;

/**
 * Created by povilas.jegelevicius on 2017-03-28.
 */
@VariableResolver({DelegatingVariableResolver.class})
public class SearchVM {

    @WireVariable
    private ImageService imageService;

    @WireVariable
    private FolderService folderService;

    private ImageDetails selectedImage;

    // LinkedHashSet keeps the order the repository returned
    Set<ImageDetails> searchResults = new LinkedHashSet<ImageDetails>();

    private SearchValidator searchValidator = new SearchValidator();

    private String searchInput;

    private int activePage = 0;

    public Set<ImageDetails> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(Set<ImageDetails> searchResults) {
        this.searchResults = searchResults;
    }

    public String getSearchInput() {
        return searchInput;
    }

    public void setSearchInput(String searchInput) {
        this.searchInput = searchInput;
    }

    public SearchValidator getSearchValidator() {
        return searchValidator;
    }

    public void setSearchValidator(SearchValidator searchValidator) {
        this.searchValidator = searchValidator;
    }

    public ImageDetails getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(ImageDetails selectedImage) {
        this.selectedImage = selectedImage;
    }

    public int getActivePage() {
        return activePage;
    }

    public void setActivePage(int activePage) {
        this.activePage = activePage;
    }

    @Command
    @NotifyChange("searchResults")
    public void search() {
        if (searchInput == null || searchInput.isEmpty()) {
            return;
        }
        searchResults.clear();
        // TODO: only the first 10 results are shown until the paging components on search.zul are wired up
        searchResults.addAll(imageService.selectImagesBySearchCriteria(searchInput, 0, 10));
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        Date searchInsertDate;
//        try{
//            df.setLenient(false);
//            searchInsertDate = df.parse(searchInput);
//            searchResults.addAll(imageService.selectImagesByInsertDate(searchInsertDate,0,10));
//        } catch (ParseException e){
//            int a = 1;
//        }
//        searchResults.addAll(imageService.selectImagesByDescription(searchInput,0,10));
//        searchResults.addAll(imageService.selectImagesByQuality(searchInput,0,10));
//        //searchResults.addAll(imageService.selectImagesByName(searchInput,0,10));
//        for (Tag item: tagService.selectTagsByName(searchInput,0,10)) {
//            searchResults.add(item.getImageDetails());
//        }
    }

    @Command
    @NotifyChange({"selectedImage"})
    public void selectImage(@BindingParam("image") ImageDetails image) {
        selectedImage = image;
    }

    @Command
    @NotifyChange("searchResults")
    public void deleteImage() {
        if (selectedImage == null) {
            return;
        }
        if (!imageService.deleteImage(selectedImage)) {
            Messagebox.show("This picture no longer exists.", "Picture not found",
                    Messagebox.OK, Messagebox.EXCLAMATION);
        }
        // remove it either way: it is gone from the database
        searchResults.remove(selectedImage);
        selectedImage = null;
    }

    @Command
    public void test (@ContextParam(ContextType.TRIGGER_EVENT) PagingEvent event) {
        int activePage = 0;
    }
}
