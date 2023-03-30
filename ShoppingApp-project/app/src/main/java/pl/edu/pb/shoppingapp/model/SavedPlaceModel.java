package pl.edu.pb.shoppingapp.model;

public class SavedPlaceModel {
    String name, address, placeId;
    Double rating, totalRating, lat, lng;

    public SavedPlaceModel(){}

    public SavedPlaceModel(String name, String address, String placeId, Double rating, Double totalRating, Double lat, Double lng) {
        this.name = name;
        this.address = address;
        this.placeId = placeId;
        this.rating = rating;
        this.totalRating = totalRating;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(Double totalRating) {
        this.totalRating = totalRating;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
