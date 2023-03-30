package pl.edu.pb.shoppingapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeometryModel {
    @SerializedName("location")
    @Expose
    private LocationModel location;
    @SerializedName("viewport")

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }
}