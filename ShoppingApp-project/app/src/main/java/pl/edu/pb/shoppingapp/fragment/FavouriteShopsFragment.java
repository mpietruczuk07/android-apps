package pl.edu.pb.shoppingapp.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import pl.edu.pb.shoppingapp.R;
import pl.edu.pb.shoppingapp.locationInterface.SavedLocationInterface;
import pl.edu.pb.shoppingapp.databinding.FragmentFavouriteShopsBinding;
import pl.edu.pb.shoppingapp.databinding.SavedPlaceItemLayoutBinding;
import pl.edu.pb.shoppingapp.model.SavedPlaceModel;

public class FavouriteShopsFragment extends Fragment implements SavedLocationInterface {
    private FragmentFavouriteShopsBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseRecyclerAdapter<String, ViewHolder> firebaseRecyclerAdapter;
    private SavedLocationInterface savedLocationInterface;

    private static final String TAG = "FAVORITE_SHOPS_FRAGMENT";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouriteShopsBinding.inflate(getLayoutInflater(), container, false);
        savedLocationInterface = this;
        firebaseAuth = FirebaseAuth.getInstance();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.savedPlacesView.setLayoutManager(new LinearLayoutManager(requireContext()));
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.savedPlacesView);
        binding.savedPlacesView.setItemAnimator(null);
        getSavedPlaces();
    }

    private void getSavedPlaces() {
        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid()).child("Saved locations");

        FirebaseRecyclerOptions<String> options = new FirebaseRecyclerOptions.Builder<String>().setQuery(query, String.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<String, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull String savedPlaceId) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Places").child(savedPlaceId);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            SavedPlaceModel savedPlaceModel = snapshot.getValue(SavedPlaceModel.class);
                            holder.binding.setSavedPlaceModel(savedPlaceModel);
                            holder.binding.setListener(savedLocationInterface);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                SavedPlaceItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
                        R.layout.saved_place_item_layout, parent, false);
                return new ViewHolder(binding);
            }
        };

        binding.savedPlacesView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        firebaseRecyclerAdapter.stopListening();
    }

    @Override
    public void onLocationClick(SavedPlaceModel savedPlaceModel) {
        String uri = "https://maps.google.com/maps?q=loc:" + savedPlaceModel.getLat() + "," + savedPlaceModel.getLng() + " (" + savedPlaceModel.getName() + " )";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SavedPlaceItemLayoutBinding binding;

        public ViewHolder(@NonNull SavedPlaceItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
