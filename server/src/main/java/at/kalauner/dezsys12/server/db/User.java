package at.kalauner.dezsys12.server.db;


import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Represents a user saved in the DB
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160212.1
 */
@Entity
@Table(name="\"user\"")
public class User {
    @Id
    @Size(max = 50)
    private String email;

    @Size(max = 50)
    @NotEmpty
    private String name;

    @NotEmpty
    private String password;

    public User() {
    }

    public User(String email) {
        this.email = email;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
