package pl.edu.pb.shoppingapp.database;

import static androidx.room.Room.*;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.edu.pb.shoppingapp.dao.ProductDao;
import pl.edu.pb.shoppingapp.entity.Product;

@androidx.room.Database(
        version = 2,
        exportSchema = true,
        entities = {Product.class},
        autoMigrations = {
                @AutoMigration(
                        from = 1,
                        to = 2
                ),
        }
)

public abstract class ProductDatabase extends RoomDatabase {
    private static ProductDatabase databaseInstance;
    public static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public abstract ProductDao productDao();

    public static ProductDatabase getDatabase(final Context context) {
        if (databaseInstance == null) {
            databaseInstance = databaseBuilder(
                    context.getApplicationContext(), ProductDatabase.class, "product_database")
                    .build();
        }
        return databaseInstance;
    }
}