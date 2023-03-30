package pl.edu.pb.shoppingapp.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import pl.edu.pb.shoppingapp.converter.DataConverter;
import pl.edu.pb.shoppingapp.activity.EditProductActivity;
import pl.edu.pb.shoppingapp.entity.Product;
import pl.edu.pb.shoppingapp.model.ProductViewModel;
import pl.edu.pb.shoppingapp.R;
import pl.edu.pb.shoppingapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ProductViewModel productViewModel;
    private Product editedProduct;

    public static final String TAG = "HOME_FRAGMENT";

    public static final int NEW_PRODUCT_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_PRODUCT_ACTIVITY_REQUEST_CODE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.products_recyclerview);
        final ProductAdapter adapter = new ProductAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        productViewModel.findAll().observe(getViewLifecycleOwner(), adapter::setProducts);

        binding.addButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProductActivity.class);
            intent.putExtra("requestCode", NEW_PRODUCT_ACTIVITY_REQUEST_CODE);
            startActivityForResult(intent, NEW_PRODUCT_ACTIVITY_REQUEST_CODE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_PRODUCT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Product product = new Product(data.getStringExtra(EditProductActivity.EXTRA_EDIT_PRODUCT_TITLE),
                    Integer.parseInt(data.getStringExtra(String.valueOf(EditProductActivity.EXTRA_EDIT_PRODUCT_QUANTITY))),
                    data.getStringExtra(EditProductActivity.EXTRA_EDIT_PRODUCT_DESCRIPTION), false,
                    data.getByteArrayExtra((String) EditProductActivity.EXTRA_EDIT_PRODUCT_PHOTO));

            productViewModel.insert(product);
            Log.d(TAG, "Product added!");
            Snackbar.make(binding.getRoot(), getString(R.string.product_saved), Snackbar.LENGTH_SHORT).show();
        }

        if (requestCode == EDIT_PRODUCT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            editedProduct.setProductPhoto(data.getByteArrayExtra((String) EditProductActivity.EXTRA_EDIT_PRODUCT_PHOTO));
            editedProduct.setTitle(data.getStringExtra(EditProductActivity.EXTRA_EDIT_PRODUCT_TITLE));
            editedProduct.setQuantity(Integer.parseInt(data.getStringExtra(String.valueOf(EditProductActivity.EXTRA_EDIT_PRODUCT_QUANTITY))));
            editedProduct.setNote(data.getStringExtra(EditProductActivity.EXTRA_EDIT_PRODUCT_DESCRIPTION));
            productViewModel.update(editedProduct);
            editedProduct = null;
            Log.d(TAG, "Product edited!");
            Snackbar.make(binding.getRoot(), getString(R.string.product_edited), Snackbar.LENGTH_SHORT).show();
        }
    }

    private class ProductHolder extends RecyclerView.ViewHolder {
        private TextView productTitleTextView, productQuantityTextView, productDescriptionTextView;
        private ImageView productPhotoImageView;
        private Product product;
        private Bitmap bitmap;

        public ProductHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.product_item_list, parent, false));

            bitmap = null;
            productPhotoImageView = itemView.findViewById(R.id.product_item_photo);
            productTitleTextView = itemView.findViewById(R.id.product_item_title);
            productQuantityTextView = itemView.findViewById((R.id.product_item_quantity));
            productDescriptionTextView = itemView.findViewById((R.id.product_item_description));

            View productItem = itemView.findViewById(R.id.product_card_item);

            productItem.setOnLongClickListener(v -> {
                productViewModel.delete(product);
                Log.d(TAG, "Product deleted!");
                Snackbar.make(binding.productsRecyclerview.getRootView(), getString(R.string.product_removed), Snackbar.LENGTH_LONG).show();
                return true;
            });

            productItem.setOnClickListener(v -> {
                editedProduct = product;
                productPhotoImageView.setDrawingCacheEnabled(true);
                productPhotoImageView.buildDrawingCache();
                bitmap = Bitmap.createBitmap(productPhotoImageView.getDrawingCache());

                Intent intent = new Intent(getActivity(), EditProductActivity.class);
                intent.putExtra("requestCode", EDIT_PRODUCT_ACTIVITY_REQUEST_CODE);
                intent.putExtra((String) EditProductActivity.EXTRA_EDIT_PRODUCT_PHOTO, DataConverter.convertToByteArray(bitmap));
                intent.putExtra(EditProductActivity.EXTRA_EDIT_PRODUCT_TITLE, productTitleTextView.getText());
                intent.putExtra(String.valueOf(EditProductActivity.EXTRA_EDIT_PRODUCT_QUANTITY), productQuantityTextView.getText());
                intent.putExtra(EditProductActivity.EXTRA_EDIT_PRODUCT_DESCRIPTION, productDescriptionTextView.getText());
                startActivityForResult(intent, EDIT_PRODUCT_ACTIVITY_REQUEST_CODE);
                requireActivity().overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
            });

        }

        public void bind(Product product) {
            productTitleTextView.setText(product.getTitle());
            productQuantityTextView.setText(Integer.toString(product.getQuantity()));
            productDescriptionTextView.setText(product.getNote());

            if (product.getProductPhoto() != null) {
                productPhotoImageView.setImageBitmap(DataConverter.convertToImage(product.getProductPhoto()));
            }

            this.product = product;
        }
    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductHolder> {
        private List<Product> products;

        @NonNull
        @Override
        public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ProductHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
            if (products != null) {
                Product book = products.get(position);
                holder.bind(book);
            } else {
                Log.d(TAG, "No products");
            }
        }

        @Override
        public int getItemCount() {
            if (products != null) {
                return products.size();
            } else {
                return 0;
            }
        }

        void setProducts(List<Product> products) {
            this.products = products;
            notifyDataSetChanged();
        }
    }
}