package lt.insoft.training.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by povilas.jegelevicius on 2017-03-01.
 */
@Entity
@Table(name="POV_FOLDER")
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernateSequence")
    @SequenceGenerator(name = "hibernateSequence", sequenceName = "HIBERNATE_SEQUENCE", allocationSize = 1)
    @Column(name="ID")
    private Long id;

    @Column(name="NAME")
    private String folderName;

    @Temporal(TemporalType.DATE)
    @Column(name="INSERT_DATE")
    private Date insertDate;

    public Folder() {
    }

    public Folder (String folderName, Date insertDate){
        this.folderName = folderName;
        this.insertDate = insertDate;
    }

    public Long getId() {
        return id;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

}
