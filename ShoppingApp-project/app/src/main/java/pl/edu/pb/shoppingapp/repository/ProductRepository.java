package pl.edu.pb.shoppingapp.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import java.util.List;

import pl.edu.pb.shoppingapp.dao.ProductDao;
import pl.edu.pb.shoppingapp.database.ProductDatabase;
import pl.edu.pb.shoppingapp.entity.Product;

public class ProductRepository {
    private final ProductDao productDao;
    private final LiveData<List<Product>> products;

    public ProductRepository(Application application) {
        ProductDatabase database = ProductDatabase.getDatabase(application);
        productDao = database.productDao();
        products = productDao.findAll();
    }

    public LiveData<List<Product>> findAllProducts() {
        return products;
    }

    public void insert(Product product) {
        ProductDatabase.databaseWriteExecutor.execute(() -> productDao.insert(product));
    }

    public void update(Product product) {
        ProductDatabase.databaseWriteExecutor.execute(() -> productDao.update(product));
    }

    public void delete(Product product){
        ProductDatabase.databaseWriteExecutor.execute(()->productDao.delete(product));
    }
}