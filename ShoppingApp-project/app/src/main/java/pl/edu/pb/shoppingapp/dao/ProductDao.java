package pl.edu.pb.shoppingapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.edu.pb.shoppingapp.entity.Product;


@Dao
public interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert (Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("DELETE FROM Products")
    void deleteAll();

    @Query("SELECT * FROM Products ORDER BY title")
    LiveData<List<Product>> findAll();

    @Query("SELECT * FROM Products WHERE title LIKE :title")
    List<Product> findProductWithTitle(String title);
}