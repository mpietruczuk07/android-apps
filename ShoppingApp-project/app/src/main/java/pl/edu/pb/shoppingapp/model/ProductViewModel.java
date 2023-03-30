package pl.edu.pb.shoppingapp.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pl.edu.pb.shoppingapp.entity.Product;
import pl.edu.pb.shoppingapp.repository.ProductRepository;

public class ProductViewModel extends AndroidViewModel {
    private final ProductRepository productRepository;
    private final LiveData<List<Product>> products;

    public ProductViewModel(@NonNull Application application) {
         super(application);
         productRepository = new ProductRepository(application);
         products = productRepository.findAllProducts();
    }

    public LiveData<List<Product>> findAll() {
        return products;
    }

    public void insert(Product product) {
        productRepository.insert(product);
    }

    public void delete(Product product) {
        productRepository.delete(product);
    }

    public void update(Product editedProduct) {
        productRepository.update(editedProduct);
    }
}