package lt.insoft.training.model;

import jakarta.persistence.*;

/**
 * Created by povilas.jegelevicius on 2017-04-10.
 */
@Entity
@Table(name="POV_USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernateSequence")
    @SequenceGenerator(name = "hibernateSequence", sequenceName = "HIBERNATE_SEQUENCE", allocationSize = 1)
    @Column(name="ID")
    private Long id;

    @Column(name="USERNAME")
    private String username;

    @Column(name="PASSWORD")
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
