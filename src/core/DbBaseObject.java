package core;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public abstract class DbBaseObject {
    @JsonProperty(access = Access.READ_ONLY)
    protected Date createdOn;

    @JsonProperty(access = Access.READ_ONLY)
    protected Date updatedOn;

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

}
