package pl.edu.pb.shoppingapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import pl.edu.pb.shoppingapp.R;
import pl.edu.pb.shoppingapp.databinding.FragmentAuthenticationBinding;

public class AuthenticationFragment extends Fragment {
    private FragmentAuthenticationBinding binding;
    private boolean isPassword;
    private String email, password;
    private FirebaseAuth firebaseAuth;
    private final static String TAG = "AUTHENTICATION_FRAGMENT";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAuthenticationBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isPassword = getArguments().getBoolean("isPassword");
        binding.inputEditEmail.setText(firebaseAuth.getCurrentUser().getEmail());

        binding.backBtn.setOnClickListener(v -> {
            if (isStateSaved()) {
                requireActivity().onBackPressed();
                requireActivity().overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
            } else {
                Fragment fragment = new SettingsFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)
                        .replace(R.id.main_view, fragment)
                        .addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        binding.confirmEditBtn.setOnClickListener(v -> {
            email = binding.inputEditEmail.getText().toString().trim();
            password = binding.inputEditPassword.getText().toString().trim();

            if (email.isEmpty()) {
                binding.inputEditEmail.setError(getText(R.string.email_required));
                binding.inputEditEmail.requestFocus();
            } else if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                binding.inputEditEmail.setError(getText(R.string.email_incorrect));
                binding.inputEditEmail.requestFocus();
            } else if (password.isEmpty()) {
                binding.inputEditPassword.setError(getText(R.string.password_required));
                binding.inputEditPassword.requestFocus();
            } else if (password.length() < 8) {
                binding.inputEditPassword.setError(getText(R.string.password_too_short));
                binding.inputEditPassword.requestFocus();
            } else {
                AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);
                firebaseAuth.getCurrentUser().reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Fragment fragment;
                            if (isPassword) {
                                fragment = new PasswordChangeFragment();
                            } else {
                                fragment = new EmailChangeFragment();
                            }
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager
                                    .beginTransaction()
                                    .setCustomAnimations(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)
                                    .replace(R.id.main_view, fragment)
                                    .addToBackStack(null);
                            fragmentTransaction.commit();
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException());
                            Toast.makeText(requireContext(), getString(R.string.error) + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}