package lt.insoft.training.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
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

    @Column(name="INSERT_DATE")
    private LocalDateTime insertDate;

    public Folder() {
    }

    public Folder (String folderName, LocalDateTime insertDate){
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

    public LocalDateTime getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(LocalDateTime insertDate) {
        this.insertDate = insertDate;
    }

}
