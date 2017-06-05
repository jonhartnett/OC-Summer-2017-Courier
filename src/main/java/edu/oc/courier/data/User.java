package edu.oc.courier.data;

import com.google.common.base.MoreObjects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings("unused")
@Entity
public final class User implements Serializable{

    @Id
    @GeneratedValue
    private int id;
    private String name;
    private byte[] password;
    private String salt;
    private UserType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final User user = (User) o;
        return id == user.id &&
            Objects.equals(name, user.name) &&
            Arrays.equals(password, user.password) &&
            Objects.equals(salt, user.salt) &&
            type == user.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, password, salt, type);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("name", name)
            .add("id", id)
            .add("password", password)
            .add("salt", salt)
            .add("type", type)
            .toString();
    }
}
