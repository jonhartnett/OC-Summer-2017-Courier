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

    public SystemInfo(float speed, BigDecimal base, BigDecimal price, BigDecimal bonus) {
        this.speed = speed;
        this.base = base;
        this.price = price;
        this.bonus = bonus;
    }

    public SystemInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public BigDecimal getBase() {
        return base;
    }

    public void setBase(BigDecimal base) {
        this.base = base;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || this.getClass() != o.getClass())
            return false;

        SystemInfo systemInfo = (SystemInfo) o;
        return this.id == systemInfo.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, speed, base, price, bonus);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("speed", speed)
                .add("base price", base)
                .add("unit price", price)
                .add("bonus", bonus)
                .toString();
    }
}
