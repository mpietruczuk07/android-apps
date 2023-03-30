package pl.edu.pb.shoppingapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.RadioGroup;

import pl.edu.pb.shoppingapp.R;
import pl.edu.pb.shoppingapp.databinding.ActivitySetThemeBinding;

public class SetThemeActivity extends AppCompatActivity {
    private ActivitySetThemeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
        binding = ActivitySetThemeBinding.inflate(getLayoutInflater());
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(binding.getRoot());

        int selectedDarkMode = getSharedPreferences("DARK_MODE", Context.MODE_PRIVATE)
                .getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        switch (selectedDarkMode) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                binding.radioLight.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                binding.radioDark.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                binding.radioSystem.setChecked(true);
                break;
        }

        binding.backBtn.setOnClickListener(v-> {
            onBackPressed();
            overridePendingTransition(
                    androidx.navigation.ui.R.anim.nav_default_pop_enter_anim,
                    androidx.navigation.ui.R.anim.nav_default_pop_exit_anim
            );
        });

        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            SharedPreferences.Editor editor =
                    getSharedPreferences("DARK_MODE", Context.MODE_PRIVATE).edit();

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio_light:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        editor.putInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);
                        recreate();
                        break;
                    case R.id.radio_dark:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        editor.putInt("theme_mode", AppCompatDelegate.MODE_NIGHT_YES);
                        recreate();
                        break;
                    case R.id.radio_system:
                        AppCompatDelegate
                                .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        editor.putInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        recreate();
                        break;
                }
                editor.apply();
            }
        });
    }
}