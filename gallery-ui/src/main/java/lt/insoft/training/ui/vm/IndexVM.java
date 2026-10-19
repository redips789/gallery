package lt.insoft.training.ui.vm;

import lt.insoft.training.bl.Service.FolderService;
import lt.insoft.training.model.Folder;
import lt.insoft.training.model.ImageDetails;
import lt.insoft.training.bl.Service.ImageService;
import lt.insoft.training.ui.vm.util.ImageToZkImageConverter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Messagebox;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@VariableResolver({DelegatingVariableResolver.class})
public class IndexVM {

	Log logger = LogFactory.getLog(ImageToZkImageConverter.class);

	@WireVariable
	private ImageService imageService;

	@WireVariable
	private FolderService folderService;

	private byte[] folderImage;

	private List<Folder> folders;

	private int pageSize;

	private int currentPage;

	private  long numberOfPages;

	private boolean previousButtonVisible;

	private boolean nextButtonVisible;

	private Folder selectedFolder;

	private ImageDetails selectedImage;

	private String currentPageName;

	private Folder folderToDelete;

	public ImageService getImageService() {
		return imageService;
	}

	public void setImageService(ImageService imageService) {
		this.imageService = imageService;
	}

	public boolean isPreviousButtonVisible() {
		return previousButtonVisible;
	}

	public void setPreviousButtonVisible(boolean previousButtonVisible) {
		this.previousButtonVisible = previousButtonVisible;
	}

	public boolean isNextButtonVisible() {
		return nextButtonVisible;
	}

	public void setNextButtonVisible(boolean nextButtonVisible) {
		this.nextButtonVisible = nextButtonVisible;
	}

	public byte[] getFolderImage() {
		return folderImage;
	}

	public void setFolderImage(byte[] folderImage) {
		this.folderImage = folderImage;
	}

	public List<Folder> getFolders() {
		return folders;
	}

	public void setFolders(List<Folder> folders) {
		this.folders = folders;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public String getCurrentPageName() {
		return currentPageName;
	}

	public void setCurrentPageName(String currentPageName) {
		this.currentPageName = currentPageName;
	}

	public Folder getSelectedFolder() {
		return selectedFolder;
	}

	public void setSelectedFolder(Folder selectedFolder) {
		this.selectedFolder = selectedFolder;
	}

	public ImageDetails getSelectedImage() {
		return selectedImage;
	}

	public void setSelectedImage(ImageDetails selectedImage) {
		this.selectedImage = selectedImage;
	}

	public Folder getFolderToDelete() {
		return folderToDelete;
	}

	public void setFolderToDelete(Folder folderToDelete) {
		this.folderToDelete = folderToDelete;
	}

	@Init
	public void init() {
		currentPage = 0;
		currentPageName = "folders.zul";
		pageSize = 3;
		numberOfPages = (long)Math.ceil((double)folderService.countFolders()/(double)pageSize);
		if(currentPage+1<numberOfPages){
			nextButtonVisible = true;
		}
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		try (InputStream is = classloader.getResourceAsStream("folder.png")){
			folderImage = IOUtils.toByteArray(is);
		}
		catch (IOException e) {
			logger.error("Error occured, returning folderimg==null", e);
		}
//		selectedImage = new ImageDetails();
//		selectedImage.setModalImage(new Image(folderImage));
//		selectedImage.setThumbnail(new Thumbnail(folderImage));
		folders=loadPage();
		return;
	}

	public List<Folder> loadPage(){
		int from = currentPage*pageSize;
		List<Folder> result = folderService.selectFolders(from,pageSize);
		return result;
	}

	@Command
	@NotifyChange({"previousButtonVisible","nextButtonVisible","folders"})
	public void previous(){
		currentPage--;
		nextButtonVisible = true;
		numberOfPages = (long)Math.ceil((double)folderService.countFolders()/(double)pageSize);
		if(currentPage == 0){
			previousButtonVisible = false;
		}
		folders = loadPage();
	}


	@Command
	@NotifyChange({"previousButtonVisible","nextButtonVisible","folders"})
	public void next(){
		previousButtonVisible = true;
		currentPage++;
		numberOfPages = (long)Math.ceil((double)folderService.countFolders()/(double)pageSize);
		folders = loadPage();
		if(currentPage >= numberOfPages-1){
			nextButtonVisible = false;
		}
	}

	@GlobalCommand
	@NotifyChange({"selectedImage","currentPageName"})
	public  void selectImage(@BindingParam("image")ImageDetails image, @BindingParam("pageName") String pageName){
		selectedImage = imageService.getImageDetailsInitialized(image.getId());
		currentPageName = pageName;
	}

	@GlobalCommand
	@NotifyChange({"selectedFolder","currentPageName"})
	public void selectFolder(@BindingParam("folder")Folder folder, @BindingParam("page") String page){
		this.selectedFolder = folder;
		currentPageName = page;
	}

	@Command
	public void selectFolderToDelete(@BindingParam("folder")Folder folder){
		folderToDelete = folder;
	}

	@Command
	@NotifyChange("folders")
	public void deleteFolder(){
		if (folderService.deleteFolder(folderToDelete)) {
			folders.remove(folderToDelete);
		} else {
			Messagebox.show("Folder \"" + folderToDelete.getFolderName()
					+ "\" is not empty. Delete its pictures first.", "Cannot delete folder",
					Messagebox.OK, Messagebox.EXCLAMATION);
		}
	}

	@GlobalCommand
	@NotifyChange({"selectedFolder","selectedImage","currentPageName"})
	public void changePage(@BindingParam("page") String page){
		selectedFolder = null;
		selectedImage = null;
		currentPageName = page;
	}
}
