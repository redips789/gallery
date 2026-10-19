package lt.insoft.training.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Math.toIntExact;

/**
 * Created by povilas.jegelevicius on 2017-02-22.
 */
@Entity
@Table (name="POV_IMAGE_DETAILS")
public class ImageDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernateSequence")
    @SequenceGenerator(name = "hibernateSequence", sequenceName = "HIBERNATE_SEQUENCE", allocationSize = 1)
    @Column(name="ID")
    private Long id;

    @Column(name="INSERT_DATE")
    private LocalDateTime insertDate;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="QUALITY")
    private String quality;

    @Column(name="IMAGE_NAME")
    private String imageName;

    @ManyToOne(fetch = FetchType.LAZY, cascade= CascadeType.ALL)
    @JoinColumn(name = "IMAGE_IMAGEID", referencedColumnName = "ID")
    private Image image;

    @ManyToOne(fetch = FetchType.EAGER, cascade= CascadeType.ALL)
    @JoinColumn(name = "THUMBNAIL_IMAGEID", referencedColumnName = "ID")
    private Image thumbnail;

    @ManyToOne
    @JoinColumn(name = "FOLDER_FOLDERID", referencedColumnName = "ID")
    private Folder folder;

    @ElementCollection
    @CollectionTable(name="POV_TAG",joinColumns = @JoinColumn(name= "IMAGE_DETAILS"))
    @Column(name="NAME")
    private List<String> tags;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "MODAL_IMAGEID")
    private Image modalImage;

    public ImageDetails(LocalDateTime date, String description, String quality, String name, Image image, Image thumbnail
            , Folder folder, List<String> tags, Image modalImage) {
        this.insertDate = date;
        this.description = description;
        this.quality = quality;
        this.imageName = name;
        this.image = image;
        this.thumbnail = thumbnail;
        this.folder = folder;
        this.tags = tags;
        this.modalImage = modalImage;
    }

    public ImageDetails(){}

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(LocalDateTime date) {
        this.insertDate = date;
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

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }


    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public Image getModalImage() {
        return modalImage;
    }

    public void setModalImage(Image modalImage) {
        this.modalImage = modalImage;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageDetails)) return false;

        ImageDetails that = (ImageDetails) o;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getId());
    }
}
