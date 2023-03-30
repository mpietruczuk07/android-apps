package pl.edu.pb.shoppingapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import pl.edu.pb.shoppingapp.R;
import pl.edu.pb.shoppingapp.model.UserModel;
import pl.edu.pb.shoppingapp.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String email, username, password;

    private static final int MIN_PASSWORD_LENGTH = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> {
            onBackPressed();
            overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
        });

        binding.signupBtn.setOnClickListener(v -> {
            registerUser();
            overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
        });

        binding.loginText.setOnClickListener(v -> {
            onBackPressed();
            overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
        });
    }

    private void registerUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        username = binding.signupUsername.getText().toString().trim();
        email = binding.signupEmail.getText().toString().trim();
        password = binding.signupPassword.getText().toString().trim();

        if (username.isEmpty()) {
            binding.signupUsername.setError(getText(R.string.username_required));
            binding.signupUsername.requestFocus();
        } else if (email.isEmpty()) {
            binding.signupEmail.setError(getText(R.string.email_required));
            binding.signupEmail.requestFocus();
        }
        else if(!(Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            binding.signupEmail.setError(getText(R.string.email_incorrect));
            binding.signupEmail.requestFocus();
        }
        else if (password.isEmpty()) {
            binding.signupPassword.setError(getText(R.string.password_required));
            binding.signupPassword.requestFocus();
        } else if (password.length() < MIN_PASSWORD_LENGTH) {
            binding.signupPassword.setError(getText(R.string.password_too_short));
            binding.signupPassword.requestFocus();
        }
        else{
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest
                                .Builder().setDisplayName(username).build();
                        firebaseAuth.getCurrentUser().updateProfile(userProfileChangeRequest)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            UserModel userModel = new UserModel(username, email, true);
                                            databaseReference.child(firebaseAuth.getUid())
                                                    .setValue(userModel)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    firebaseAuth.getCurrentUser().sendEmailVerification()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(
                                                                    RegisterActivity.this,
                                                                    getString(R.string.email_confirmation),
                                                                    Toast.LENGTH_SHORT).show();
                                                            onBackPressed();
                                                            overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                        Toast.makeText(RegisterActivity.this,  getText(R.string.register_success) +
                                " " +getText(R.string.email_confirmation), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, getText(R.string.register_error) +
                                " " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}