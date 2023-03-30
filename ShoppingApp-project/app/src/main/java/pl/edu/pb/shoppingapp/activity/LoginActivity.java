package pl.edu.pb.shoppingapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import pl.edu.pb.shoppingapp.R;
import pl.edu.pb.shoppingapp.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth;
    private String email, password;

    private static final int MIN_PASSWORD_LENGTH = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.forgetPasswordText.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
        });

        binding.loginBtn.setOnClickListener(v -> {
            loginUser();
        });

        binding.signupBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
        });
    }

    private void loginUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        email = binding.loginEmail.getText().toString().trim();
        password = binding.loginPassword.getText().toString().trim();

        if (email.isEmpty()) {
            binding.loginEmail.setError(getText(R.string.email_required));
            binding.loginEmail.requestFocus();
        } else if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            binding.loginEmail.setError(getText(R.string.email_incorrect));
            binding.loginEmail.requestFocus();
        } else if (password.isEmpty()) {
            binding.loginPassword.setError(getText(R.string.password_required));
            binding.loginPassword.requestFocus();
        } else if (password.length() < MIN_PASSWORD_LENGTH) {
            binding.loginPassword.setError(getText(R.string.password_too_short));
            binding.loginPassword.requestFocus();
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
                            finish();
                        } else {
                            firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, getText(R.string.email_confirmation), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, getText(R.string.email_confirmation_error) + " " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, getText(R.string.login_error) + " " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}