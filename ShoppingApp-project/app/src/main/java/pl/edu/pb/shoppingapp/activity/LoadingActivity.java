package pl.edu.pb.shoppingapp.activity;

import static androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE;
import static androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED;
import static androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

import pl.edu.pb.shoppingapp.R;

public class LoadingActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private static final String TAG = "LOADING_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoadingActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

                switch (errorCode) {
                    case BIOMETRIC_ERROR_NONE_ENROLLED:
                        Toast.makeText(LoadingActivity.this, getText(R.string.no_biometrics), Toast.LENGTH_LONG).show();
                        break;
                    case BIOMETRIC_ERROR_NO_HARDWARE:
                        Toast.makeText(LoadingActivity.this, getText(R.string.no_biometric_hardware), Toast.LENGTH_LONG).show();
                        break;
                    case BIOMETRIC_ERROR_HW_UNAVAILABLE:
                        Toast.makeText(LoadingActivity.this, getText(R.string.biometric_hardware_unavailable), Toast.LENGTH_LONG).show();
                }

                startActivity(new Intent(LoadingActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(LoadingActivity.this, getText(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getText(R.string.biometrics_title))
                .setDescription(getText(R.string.biometrics_description))
                .setDeviceCredentialAllowed(true).build();

        firebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firebaseAuth.getCurrentUser() != null) {
                    boolean isAppLocked = getSharedPreferences("user_biometrics", Context.MODE_PRIVATE)
                            .getBoolean("biometrics", false);

                    if (isAppLocked) {
                        biometricPrompt.authenticate(promptInfo);
                    } else {
                        startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(LoadingActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 3000);

        int selectedDarkMode = getSharedPreferences("DARK_MODE", Context.MODE_PRIVATE)
                .getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        switch (selectedDarkMode) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                ;
                break;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
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
                ;
                break;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}