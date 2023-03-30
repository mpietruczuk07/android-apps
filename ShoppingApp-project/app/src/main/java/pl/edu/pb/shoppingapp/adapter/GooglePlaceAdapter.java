package pl.edu.pb.shoppingapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.edu.pb.shoppingapp.locationInterface.NearLocationInterface;
import pl.edu.pb.shoppingapp.R;
import pl.edu.pb.shoppingapp.databinding.PlaceItemLayoutBinding;
import pl.edu.pb.shoppingapp.model.GooglePlaceModel;

public class GooglePlaceAdapter extends RecyclerView.Adapter<GooglePlaceAdapter.ViewHolder>{
    private List<GooglePlaceModel> googlePlaceModelList;
    private NearLocationInterface nearLocationInterface;

    public GooglePlaceAdapter(NearLocationInterface nearLocationInterface){
        this.nearLocationInterface = nearLocationInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PlaceItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.place_item_layout, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GooglePlaceAdapter.ViewHolder holder, int position) {
        if(googlePlaceModelList != null){
            GooglePlaceModel placeModel = googlePlaceModelList.get(position);
            holder.binding.setGooglePlaceModel(placeModel);
            holder.binding.setListener(nearLocationInterface);
        }
    }

    @Override
    public int getItemCount() {
        if(googlePlaceModelList != null)
            return googlePlaceModelList.size();
        else
            return 0;
    }

    public void setGooglePlaceModelList(List<GooglePlaceModel> googlePlaceModelList) {
        this.googlePlaceModelList = googlePlaceModelList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private PlaceItemLayoutBinding binding;

        public ViewHolder(@NonNull PlaceItemLayoutBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}