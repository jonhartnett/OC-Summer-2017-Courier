package edu.oc.courier.data;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.util.Objects;

@SuppressWarnings("unused")
@Entity
public final class Courier {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    public Courier(){}

    public Courier(String name) {
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Courier courier = (Courier) o;
        return id == courier.id &&
            Objects.equals(name, courier.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("name", name)
            .add("id", id)
            .toString();
    }
}
