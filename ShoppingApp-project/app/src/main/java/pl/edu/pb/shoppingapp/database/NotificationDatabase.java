package pl.edu.pb.shoppingapp.database;

import static androidx.room.Room.*;

import android.content.Context;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.edu.pb.shoppingapp.dao.NotificationDao;
import pl.edu.pb.shoppingapp.entity.Notification;

@Database(entities = {Notification.class}, version = 1, exportSchema = false)
public abstract class NotificationDatabase extends RoomDatabase {
    private static NotificationDatabase databaseInstance;
    public static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public abstract NotificationDao notificationDao();

    public static NotificationDatabase getDatabase(final Context context) {
        if (databaseInstance == null) {
            databaseInstance = databaseBuilder(
                    context.getApplicationContext(), NotificationDatabase.class, "notification_database")
                    .build();
        }
        return databaseInstance;
    }
}