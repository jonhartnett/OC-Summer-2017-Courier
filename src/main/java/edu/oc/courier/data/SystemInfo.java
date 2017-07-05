package edu.oc.courier.data;

import com.google.common.base.MoreObjects;
import edu.oc.courier.util.Column;
import edu.oc.courier.util.Id;
import edu.oc.courier.util.Savable;
import edu.oc.courier.util.Table;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Savable
public class SystemInfo {
    public static Table<SystemInfo> table = Table.from(SystemInfo.class);
    public static Optional<SystemInfo> get(){
        return table.getAll().findFirst();
    }

    @Id
    @Column
    private int id;
    @Column
    private float speed;
    @Column
    private BigDecimal base;
    @Column
    private BigDecimal price;
    @Column
    private BigDecimal bonus;
    @Column
    private Node address;

    public SystemInfo(final float speed, final BigDecimal base, final BigDecimal price, final BigDecimal bonus, final Node address) {
        this.speed = speed;
        this.base = base;
        this.price = price;
        this.bonus = bonus;
        this.address = address;
    }

    public SystemInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(final float speed) {
        this.speed = speed;
    }

    public BigDecimal getBase() {
        return base;
    }

    public void setBase(final BigDecimal base) {
        this.base = base;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(final BigDecimal bonus) {
        this.bonus = bonus;
    }

    public Node getAddress(){ return address; }

    public void setAddress(final Node address){
        this.address = address;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;

        if (o == null || this.getClass() != o.getClass())
            return false;

        final SystemInfo systemInfo = (SystemInfo) o;
        return this.id == systemInfo.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, speed, base, price, bonus, address);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("speed", speed)
                .add("base price", base)
                .add("unit price", price)
                .add("bonus", bonus)
                .add("address", address)
                .toString();
    }
}
