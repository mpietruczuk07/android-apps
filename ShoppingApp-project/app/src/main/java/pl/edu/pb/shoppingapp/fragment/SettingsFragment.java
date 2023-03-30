package pl.edu.pb.shoppingapp.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import pl.edu.pb.shoppingapp.activity.SetThemeActivity;
import pl.edu.pb.shoppingapp.R;
import pl.edu.pb.shoppingapp.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private FirebaseAuth firebaseAuth;

    private static final String TAG = "SETTINGS_FRAGMENT";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        if (!requireActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            binding.cardBiometrics.setVisibility(View.GONE);
        } else {
            boolean isAppLocked = requireActivity()
                    .getSharedPreferences("user_biometrics", Context.MODE_PRIVATE)
                    .getBoolean("biometrics", false);
            if (isAppLocked) {
                binding.biometricSwitch.setChecked(true);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)) {
                binding.cardNotifications.setVisibility(View.GONE);
            } else {
                boolean areNotificationsEnabled = requireActivity()
                        .getSharedPreferences("user_notifications", Context.MODE_PRIVATE)
                        .getBoolean("notifications", true);
                if (areNotificationsEnabled) {
                    binding.notificationsSwitch.setChecked(true);
                }
            }
        } else {
            boolean areNotificationsEnabled = requireActivity()
                    .getSharedPreferences("user_notifications", Context.MODE_PRIVATE)
                    .getBoolean("notifications", true);
            if (areNotificationsEnabled) {
                binding.notificationsSwitch.setChecked(true);
            }
        }

        binding.backBtn.setOnClickListener(v -> {
            if (isStateSaved()) {
                requireActivity().onBackPressed();
                requireActivity().overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
            } else {
                replaceFragment(new MoreFragment(), false);
            }
        });

        binding.usernameText.setOnClickListener(v -> {
            usernameDialog();
        });

        binding.cardEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new AuthenticationFragment(), false);
            }
        });

        binding.cardPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new AuthenticationFragment(), true);
            }
        });

        binding.biometricSwitch.setOnCheckedChangeListener((switchView, isChecked) -> {
            SharedPreferences.Editor editor = requireActivity()
                    .getSharedPreferences("user_biometrics", Context.MODE_PRIVATE).edit();
            if (isChecked) {
                editor.putBoolean("biometrics", true);
            } else {
                editor.putBoolean("biometrics", false);
            }
            editor.apply();
        });

        binding.notificationsSwitch.setOnCheckedChangeListener((switchView, isChecked) -> {
            SharedPreferences.Editor editor = requireActivity()
                    .getSharedPreferences("user_notifications", Context.MODE_PRIVATE).edit();
            if (isChecked) {
                editor.putBoolean("notifications", true);
            } else {
                editor.putBoolean("notifications", false);
            }
            editor.apply();
        });

        binding.cardTheme.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SetThemeActivity.class));
            requireActivity().overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.txtEmail.setText(firebaseAuth.getCurrentUser().getEmail());
        binding.usernameText.setText(firebaseAuth.getCurrentUser().getDisplayName());
    }

    private void usernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.change_username_dialog_layout, null, false);
        builder.setView(view);
        TextInputEditText editUsername = view.findViewById(R.id.input_edit_username);
        builder.setTitle(getText(R.string.edit_username));
        builder.setPositiveButton(getText(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String username = editUsername.getText().toString().trim();
                if (!username.isEmpty()) {
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                    firebaseAuth.getCurrentUser().updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                                Map<String, Object> map = new HashMap<>();
                                map.put("username", username);
                                databaseReference.child(firebaseAuth.getUid()).updateChildren(map);
                                binding.usernameText.setText(username);
                                Snackbar.make(requireActivity().findViewById(R.id.settings_linear_layout), getText(R.string.username_updated), Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(requireActivity().findViewById(R.id.settings_linear_layout), getText(R.string.username_error), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Snackbar.make(requireActivity().findViewById(R.id.settings_linear_layout), getText(R.string.username_required), Snackbar.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
    }

    private void replaceFragment(Fragment fragment, boolean isPassword){
        Bundle data = new Bundle();
        data.putBoolean("isPassword", isPassword);
        fragment.setArguments(data);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction()
                .setCustomAnimations(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)
                .replace(R.id.main_view, fragment, null)
                .setReorderingAllowed(true)
                .addToBackStack(null);
        fragmentTransaction.commit();
    }
}