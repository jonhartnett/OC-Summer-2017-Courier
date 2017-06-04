package edu.oc.courier.data;

import com.google.common.base.MoreObjects;
import java.util.Objects;

@SuppressWarnings("unused")
public final class DirectionStep {

    private String title;
    private String decsription;
    private Intersection intersection;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDecsription() {
        return decsription;
    }

    public void setDecsription(String decsription) {
        this.decsription = decsription;
    }

    public Intersection getIntersection() {
        return intersection;
    }

    public void setIntersection(Intersection intersection) {
        this.intersection = intersection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DirectionStep that = (DirectionStep) o;
        return Objects.equals(title, that.title) &&
            Objects.equals(decsription, that.decsription) &&
            Objects.equals(intersection, that.intersection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, decsription, intersection);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("title", title)
            .add("decsription", decsription)
            .add("intersection", intersection)
            .toString();
    }
}
