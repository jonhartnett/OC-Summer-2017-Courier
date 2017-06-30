package edu.oc.courier.data;

import com.google.common.base.MoreObjects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class SystemInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private float speed;
    private BigDecimal base;
    private BigDecimal price;
    private BigDecimal bonus;
    private String courierAddress;

    public SystemInfo(final float speed, final BigDecimal base, final BigDecimal price, final BigDecimal bonus, final String courierAddress) {
        this.speed = speed;
        this.base = base;
        this.price = price;
        this.bonus = bonus;
        this.courierAddress = courierAddress;
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

    public String getCourierAddress(){ return courierAddress; }

    public void setCourierAddress(final String address){
        this.courierAddress = address;
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
        return Objects.hash(id, speed, base, price, bonus, courierAddress);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("speed", speed)
                .add("base price", base)
                .add("unit price", price)
                .add("bonus", bonus)
                .add("courierAddress", courierAddress)
                .toString();
    }
}
