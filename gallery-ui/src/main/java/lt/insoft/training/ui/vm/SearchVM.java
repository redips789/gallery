package lt.insoft.training.ui.vm;

import lt.insoft.training.bl.Service.ImageService;
import lt.insoft.training.model.ImageDetails;
import lt.insoft.training.ui.vm.validators.SearchValidator;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Messagebox;

import java.util.*;

/**
 * Created by povilas.jegelevicius on 2017-03-28.
 */
@VariableResolver({DelegatingVariableResolver.class})
public class SearchVM {

    private static final int PAGE_SIZE = 8;

    @WireVariable
    private ImageService imageService;

    private ImageDetails selectedImage;

    private List<ImageDetails> searchResults = new ArrayList<ImageDetails>();

    private SearchValidator searchValidator = new SearchValidator();

    // bound to the textbox, so it changes while typing
    private String searchInput;

    // the query the current results belong to; paging keeps using it until the next search
    private String currentQuery;

    private int activePage = 0;

    private int totalResults = 0;

    private String resultsMessage;

    public List<ImageDetails> getSearchResults() {
        return searchResults;
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

    public ImageDetails getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(ImageDetails selectedImage) {
        this.selectedImage = selectedImage;
    }

    public int getActivePage() {
        return activePage;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public int getPageSize() {
        return PAGE_SIZE;
    }

    public String getResultsMessage() {
        return resultsMessage;
    }

    @Command
    @NotifyChange({"searchResults", "activePage", "totalResults", "resultsMessage"})
    public void search() {
        if (searchInput == null || searchInput.isEmpty()) {
            return;
        }
        currentQuery = searchInput;
        activePage = 0;
        loadPage();
    }

    @Command
    @NotifyChange({"searchResults", "activePage", "totalResults", "resultsMessage"})
    public void changePage(@BindingParam("page") int page) {
        activePage = page;
        loadPage();
    }

    @Command
    @NotifyChange({"selectedImage"})
    public void selectImage(@BindingParam("image") ImageDetails image) {
        selectedImage = image;
    }

    @Command
    @NotifyChange({"searchResults", "activePage", "totalResults", "resultsMessage"})
    public void deleteImage() {
        if (selectedImage == null) {
            return;
        }
        if (!imageService.deleteImage(selectedImage)) {
            Messagebox.show("This picture no longer exists.", "Picture not found",
                    Messagebox.OK, Messagebox.EXCLAMATION);
        }
        selectedImage = null;
        // reload either way: the next page's first result moves up into this one
        loadPage();
    }

    /**
     * Recounts the results for currentQuery, keeps activePage within range and loads that page.
     */
    private void loadPage() {
        totalResults = Math.toIntExact(imageService.countImagesBySearchCriteria(currentQuery));
        int pageCount = (totalResults + PAGE_SIZE - 1) / PAGE_SIZE;
        if (activePage >= pageCount) {
            activePage = Math.max(pageCount - 1, 0);
        }
        searchResults = imageService.selectImagesBySearchCriteria(currentQuery, activePage * PAGE_SIZE, PAGE_SIZE);
        if (totalResults == 0) {
            resultsMessage = "No pictures found";
        } else {
            int from = activePage * PAGE_SIZE + 1;
            resultsMessage = from + "-" + (from + searchResults.size() - 1) + " of " + totalResults + " pictures";
        }
    }
}
