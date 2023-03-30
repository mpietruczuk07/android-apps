package pl.edu.pb.shoppingapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import pl.edu.pb.shoppingapp.R;
import pl.edu.pb.shoppingapp.databinding.ActivityForgetPasswordBinding;

public class ForgetPasswordActivity extends AppCompatActivity {
    private ActivityForgetPasswordBinding binding;
    private FirebaseAuth firebaseAuth;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v->{
            onBackPressed();
            overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
        });

        binding.forgetBtn.setOnClickListener(v -> {
            resetPassword();
            overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
        });
    }

    private void resetPassword() {
        firebaseAuth = FirebaseAuth.getInstance();
        email = binding.emailFieldReset.getText().toString().trim();

        if (email.isEmpty()) {
            binding.emailFieldReset.setError(getText(R.string.email_required));
            binding.emailFieldReset.requestFocus();
        }
        else if(!(Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            binding.emailFieldReset.setError(getText(R.string.email_incorrect));
            binding.emailFieldReset.requestFocus();
        }
        else {
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(ForgetPasswordActivity.this,
                                getText(R.string.email_reset_confirmation), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
                    }
                    else {
                        Toast.makeText(ForgetPasswordActivity.this, getText(R.string.email_reset_error) + " " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}