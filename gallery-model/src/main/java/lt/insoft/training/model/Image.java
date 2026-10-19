package lt.insoft.training.model;

import jakarta.persistence.*;

/**
 * Created by povilas.jegelevicius on 2017-03-01.
 */
@Entity
@Table(name="POV_IMAGE")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernateSequence")
    @Column(name="ID")
    private Long id;

    @Lob
    @Column(name="IMAGE")
    private byte[] image;

    public Image(byte[] image) {
        this.image = image;
    }

    public Image(){

    }

    public Long getId() {
        return id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
