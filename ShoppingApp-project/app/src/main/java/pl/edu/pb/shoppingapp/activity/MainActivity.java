package pl.edu.pb.shoppingapp.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import pl.edu.pb.shoppingapp.fragment.FavouriteShopsFragment;
import pl.edu.pb.shoppingapp.fragment.HomeFragment;
import pl.edu.pb.shoppingapp.fragment.MapsFragment;
import pl.edu.pb.shoppingapp.fragment.MoreFragment;
import pl.edu.pb.shoppingapp.model.NotificationViewModel;
import pl.edu.pb.shoppingapp.R;
import pl.edu.pb.shoppingapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    public static NotificationViewModel model;
    private ActivityMainBinding binding;

    private static final int NOTIFICATION_REQUEST_CODE = 3000;
    private static final String TAG = "MAIN_ACTIVITY";
    private static final String TOPIC = "GENERAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        model = ViewModelProviders.of(this).get(NotificationViewModel.class);

        askNotificationPermission();

        binding.bottomNavMenu.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_btn:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.maps_btn:
                    replaceFragment(new MapsFragment());
                    break;
                case R.id.favourite_shops_btn:
                    replaceFragment(new FavouriteShopsFragment());
                    break;
                case R.id.more_btn:
                    replaceFragment(new MoreFragment());
                    break;
            }
            return true;
        });

        //to be deleted in the future!
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                Log.d(TAG, task.getResult());
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Message received");
                } else {
                    Log.d(TAG, "Message receiving error!");
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction()
                .setCustomAnimations(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)
                .replace(R.id.main_view, fragment, null)
                .setReorderingAllowed(true)
                .addToBackStack(null);
        fragmentTransaction.commit();
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            Log.d(TAG, "Location permission granted");
        } else {
        }
    });

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case NOTIFICATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Location permission granted");
                } else {
                    Snackbar.make(this, findViewById(R.id.main_view), getText(R.string.notification_permission_denied), Snackbar.LENGTH_LONG).show();
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume method");

        int selectedDarkMode = getSharedPreferences("DARK_MODE", Context.MODE_PRIVATE)
                .getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        switch (selectedDarkMode) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause method");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart method");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart method");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop method");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy method");
    }
}