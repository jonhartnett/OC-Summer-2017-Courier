package edu.oc.courier.data;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import edu.oc.courier.util.*;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@SuppressWarnings("unused")
@Savable
public final class User {
    public static Table<User> table = Table.from(User.class);
    private static Table<User>.CustomQuery getByUser = table.getCustom().where("username=?");
    public static Optional<User> getByUsername(String username){
        return getByUser.execute(username).findFirst();
    }

    private static final String alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
    private static final Random rand = new Random();
    private static final MessageDigest digest;

    @Id
    @Column
    private int id;
    @Column
    private String name;
    @Unique
    @Column
    private String username;
    @Column
    private byte[] password;
    @Column
    private String salt;
    @Column
    private UserType type;

    public User(){}

    public User(int id, String name, byte[] password, String salt, UserType type, String username){
        this.id = id;
        this.name = name;
        this.password = password;
        this.salt = salt;
        this.type = type;
        this.username = username;
    }

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
