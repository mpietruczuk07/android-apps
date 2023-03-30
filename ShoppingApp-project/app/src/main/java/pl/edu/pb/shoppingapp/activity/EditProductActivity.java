package pl.edu.pb.shoppingapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import pl.edu.pb.shoppingapp.converter.DataConverter;
import pl.edu.pb.shoppingapp.R;
import pl.edu.pb.shoppingapp.databinding.ActivityEditProductBinding;

public class EditProductActivity extends AppCompatActivity {
    private ActivityEditProductBinding binding;
    private Bitmap bmpImage;
    private String name, quantity, description;

    private static final int EDIT_REQUEST = 2;
    private static final int CAMERA_INTENT = 5000;
    private static final int CAMERA_REQUEST_CODE = 6000;

    public static final String EXTRA_EDIT_PRODUCT_TITLE = "EXAMPLE_TITLE";
    public static final float EXTRA_EDIT_PRODUCT_QUANTITY = 0;
    public static final String EXTRA_EDIT_PRODUCT_DESCRIPTION = "EXAMPLE_DESCRIPTION";
    public static final CharSequence EXTRA_EDIT_PRODUCT_PHOTO = "EXAMPLE_PHOTO";

    private static final String TAG = "EDIT_PRODUCT_ACTIVITY";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
        binding = ActivityEditProductBinding.inflate(getLayoutInflater());
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v-> {
            onBackPressed();
            overridePendingTransition(
                    androidx.navigation.ui.R.anim.nav_default_pop_enter_anim,
                    androidx.navigation.ui.R.anim.nav_default_pop_exit_anim
            );
        });

        if (getIntent().getExtras().getInt("requestCode") == EDIT_REQUEST) {
            binding.addEditProductToolbarTitle.setText(getString(R.string.edit_product_toolbar_name));
        }
        if (getIntent().hasExtra((String) EXTRA_EDIT_PRODUCT_PHOTO)) {
            binding.addEditProductPhoto.setImageBitmap(DataConverter.convertToImage(getIntent().getByteArrayExtra((String) EXTRA_EDIT_PRODUCT_PHOTO)));
            bmpImage = DataConverter.convertToImage(getIntent().getByteArrayExtra((String) EXTRA_EDIT_PRODUCT_PHOTO));
        }
        if (getIntent().hasExtra(EXTRA_EDIT_PRODUCT_TITLE)) {
            binding.addEditProductName.setText(getIntent().getStringExtra(EXTRA_EDIT_PRODUCT_TITLE));
        }
        if (getIntent().hasExtra(String.valueOf(EXTRA_EDIT_PRODUCT_QUANTITY))) {
            binding.addEditProductQuantity.setText((getIntent().getStringExtra(String.valueOf(EXTRA_EDIT_PRODUCT_QUANTITY))));
        }
        if (getIntent().hasExtra(EXTRA_EDIT_PRODUCT_DESCRIPTION)) {
            binding.addEditProductDescription.setText((getIntent().getStringExtra(EXTRA_EDIT_PRODUCT_DESCRIPTION)));
        }

        binding.addEditProductSaveBtn.setOnClickListener(v -> {
            name = binding.addEditProductName.getText().toString();
            quantity = binding.addEditProductQuantity.getText().toString();
            description = binding.addEditProductDescription.getText().toString();

            if (bmpImage == null) {
                binding.addEditProductPhoto.setDrawingCacheEnabled(true);
                binding.addEditProductPhoto.buildDrawingCache();
                bmpImage = Bitmap.createBitmap(binding.addEditProductPhoto.getDrawingCache());
            }

            if (name.isEmpty()) {
                binding.addEditProductName.setError(getText(R.string.username_required));
                binding.addEditProductName.requestFocus();
            } else if (quantity.isEmpty()) {
                binding.addEditProductQuantity.setError(getText(R.string.quantity_required));
                binding.addEditProductQuantity.requestFocus();
            } else if (description.isEmpty()) {
                binding.addEditProductDescription.setError(getText(R.string.description_required));
                binding.addEditProductDescription.requestFocus();
            } else {
                Intent replyIntent = new Intent();
                byte[] productPhoto = DataConverter.convertToByteArray(bmpImage);
                replyIntent.putExtra(EXTRA_EDIT_PRODUCT_TITLE, name);
                replyIntent.putExtra(String.valueOf(EXTRA_EDIT_PRODUCT_QUANTITY), quantity);
                replyIntent.putExtra(EXTRA_EDIT_PRODUCT_DESCRIPTION, description);
                replyIntent.putExtra((String) EXTRA_EDIT_PRODUCT_PHOTO, productPhoto);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });

        binding.addEditProductPhotoBtn.setOnClickListener(v -> takePhoto());
    }

    public void takePhoto() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            startActivityForResult(intent, CAMERA_INTENT);

        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_INTENT:
                bmpImage = (Bitmap) data.getExtras().get("data");
                binding.addEditProductPhoto.setImageBitmap(bmpImage);
                break;
        }
    }
}
