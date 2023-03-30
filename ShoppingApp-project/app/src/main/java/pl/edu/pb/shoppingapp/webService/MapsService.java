package pl.edu.pb.shoppingapp.webService;

import pl.edu.pb.shoppingapp.model.GoogleResponseModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MapsService {
    @GET
    Call<GoogleResponseModel> getPlaces(@Url String url);
}
