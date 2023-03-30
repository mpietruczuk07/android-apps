package pl.edu.pb.shoppingapp.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import pl.edu.pb.shoppingapp.dao.NotificationDao;
import pl.edu.pb.shoppingapp.database.NotificationDatabase;
import pl.edu.pb.shoppingapp.entity.Notification;

public class NotificationRepository {
    private final NotificationDao notificationDao;
    private final LiveData<List<Notification>> notifications;

    public NotificationRepository(Application application) {
        NotificationDatabase database = NotificationDatabase.getDatabase(application);
        notificationDao = database.notificationDao();
        notifications = notificationDao.findAll();
    }

    public LiveData<List<Notification>> findAllNotifications() {
        return notifications;
    }

    public void insert(Notification notification) {
        NotificationDatabase.databaseWriteExecutor.execute(() -> notificationDao.insert(notification));
    }

    public void update(Notification notification) {
        NotificationDatabase.databaseWriteExecutor.execute(() -> notificationDao.update(notification));
    }

    public void delete(Notification notification){
        NotificationDatabase.databaseWriteExecutor.execute(()->notificationDao.delete(notification));
    }
}