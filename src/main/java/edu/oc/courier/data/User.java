package edu.oc.courier.data;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("unused")
@Entity
public final class User {

    private static final String alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
    private static final Random rand = new Random();
    private static final MessageDigest digest;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String username;
    private byte[] password;
    private String salt;
    private UserType type;

    public User(){}

    public User(String name, String username, String password, UserType type) {
        setName(name);
        setUsername(username);
        setPassword(password);
        setType(type);
    }

    public boolean isPasswordValid(String password) {
        Preconditions.checkNotNull(password, "password must not be null");

        final byte[] bytes;
        try {
            bytes = digest.digest((salt + password).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return Arrays.equals(this.password, bytes);
    }

    public void setPassword(String password) {
        Preconditions.checkNotNull(password, "password must not be null");
        generateSalt();

        try {
            this.password = digest.digest((salt + password).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateSalt() {
        if (salt != null) {
            return;
        }

        salt = rand.ints(16, 0, alpha.length()).map(alpha::charAt)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    static {
        try {
            digest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
