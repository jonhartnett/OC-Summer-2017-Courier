package edu.oc.courier.data;

import com.google.common.base.MoreObjects;
import edu.oc.courier.util.Column;
import edu.oc.courier.util.Id;
import edu.oc.courier.util.Savable;
import edu.oc.courier.util.Table;

import java.util.Objects;

@SuppressWarnings("unused")
@Savable
public final class Courier {
    public static Table<Courier> table = Table.from(Courier.class);

    @Id
    @Column
    private int id;
    @Column
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
