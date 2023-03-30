package pl.edu.pb.shoppingapp.constant;

import java.util.ArrayList;
import java.util.Arrays;

import pl.edu.pb.shoppingapp.model.PlaceModel;
import pl.edu.pb.shoppingapp.R;

public interface Constant {
    ArrayList<PlaceModel> places = new ArrayList<>(
            Arrays.asList(new PlaceModel(1, R.drawable.ic_baseline_shopping_cart_24, "Supermarket", "supermarket"),
                    new PlaceModel(2, R.drawable.ic_baseline_local_convenience_store_24, "Convenience store", "convenience_store"),
                    new PlaceModel(3, R.drawable.ic_baseline_local_gas_station_24, "Gas station", "gas_station"),
                    new PlaceModel(4, R.drawable.ic_baseline_shopping_bag_24, "Clothing store", "clothing_store"),
                    new PlaceModel(5, R.drawable.ic_baseline_book_24, "Book store", "book_store"),
                    new PlaceModel(6, R.drawable.ic_baseline_computer_24, "Electronics store", "electronics_store"),
                    new PlaceModel(7, R.drawable.ic_baseline_local_hospital_24, "Drug store", "drugstore")
            ));

    ArrayList<PlaceModel> placesPL = new ArrayList<>(
            Arrays.asList(new PlaceModel(1, R.drawable.ic_baseline_shopping_cart_24, "Supermarket", "supermarket"),
                    new PlaceModel(2, R.drawable.ic_baseline_local_convenience_store_24, "Sklep osiedlowy", "convenience_store"),
                    new PlaceModel(3, R.drawable.ic_baseline_local_gas_station_24, "Stacja paliw", "gas_station"),
                    new PlaceModel(4, R.drawable.ic_baseline_shopping_bag_24, "Sklep odzieżowy", "clothing_store"),
                    new PlaceModel(5, R.drawable.ic_baseline_book_24, "Księgarnia", "book_store"),
                    new PlaceModel(6, R.drawable.ic_baseline_computer_24, "Sklep z elektroniką", "electronics_store"),
                    new PlaceModel(7, R.drawable.ic_baseline_local_hospital_24, "Apteka", "drugstore")
            ));
}
