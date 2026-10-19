package lt.insoft.training.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by povilas.jegelevicius on 2017-03-01.
 */
@Entity
@Table(name="POV_FOLDER")
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
