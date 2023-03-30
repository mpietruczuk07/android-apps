package pl.edu.pb.shoppingapp.fragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Looper;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.edu.pb.shoppingapp.locationInterface.NearLocationInterface;
import pl.edu.pb.shoppingapp.constant.Constant;
import pl.edu.pb.shoppingapp.adapter.GooglePlaceAdapter;
import pl.edu.pb.shoppingapp.databinding.FragmentMapsBinding;
import pl.edu.pb.shoppingapp.model.GooglePlaceModel;
import pl.edu.pb.shoppingapp.model.GoogleResponseModel;
import pl.edu.pb.shoppingapp.model.PlaceModel;
import pl.edu.pb.shoppingapp.R;
import pl.edu.pb.shoppingapp.model.SavedPlaceModel;
import pl.edu.pb.shoppingapp.webService.MapsService;
import pl.edu.pb.shoppingapp.webService.RetrofitInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, NearLocationInterface {
    private FragmentMapsBinding binding;
    private GoogleMap mGoogleMap;
    private boolean isLocationPermission = false, isTrafficEnabled;
    private Location currentLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentMarker;
    private FirebaseAuth firebaseAuth;
    private MapsService mapsService;
    private List<GooglePlaceModel> googlePlaceModelList;
    private PlaceModel selectedPlaceModel;
    private GooglePlaceAdapter googlePlaceAdapter;
    private ArrayList<String> userSavedPlaceId;
    private DatabaseReference locationReference, userLocationReference;
    private MapStyleOptions mapStyleOptions;

    private int radius = 10000;

    private static final int LOCATION_REQUEST_CODE = 1000;
    private static final int LOCATION_SENSOR = 2000;
    private static final float ZOOM = 14;

    private static final String TAG = "MAPS_FRAGMENT";
    private static final String API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMapsBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        mapsService = RetrofitInstance.getRetrofitInstance().create(MapsService.class);
        googlePlaceModelList = new ArrayList<>();
        userSavedPlaceId = new ArrayList<>();
        locationReference = FirebaseDatabase.getInstance().getReference("Places");
        userLocationReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid()).child("Saved locations");

        mapStyleOptions = MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_dark_mode_style);

        binding.mapTypeBtn.setOnClickListener(v -> {
            Context context = new ContextThemeWrapper(getContext(), R.style.Theme_ShoppingApp_PopupTheme);
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.map_type_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.default_map_btn:
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case R.id.satellite_map_btn:
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case R.id.terrain_map_btn:
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                }
                return true;
            });
            popupMenu.show();
        });

        binding.trafficBtn.setOnClickListener(v -> {
            if (isTrafficEnabled) {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(false);
                    isTrafficEnabled = false;
                }
            } else {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(true);
                    isTrafficEnabled = true;
                }
            }
        });

        binding.currentLocationBtn.setOnClickListener(v -> getLocationUpdates());

        binding.resetBtn.setOnClickListener(v -> moveBackToLocation());

        binding.placesGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, int checkedId) {
                if (checkedId != -1) {
                    if (Locale.getDefault().toString().startsWith("en")) {
                        PlaceModel placeModel = Constant.places.get(checkedId - 1);
                        selectedPlaceModel = placeModel;
                        binding.searchPlaceName.setText(placeModel.getName());
                        getPlaces(placeModel.getPlaceType());
                    } else if (Locale.getDefault().toString().startsWith("pl")) {
                        PlaceModel placeModel = Constant.placesPL.get(checkedId - 1);
                        selectedPlaceModel = placeModel;
                        binding.searchPlaceName.setText(placeModel.getName());
                        getPlaces(placeModel.getPlaceType());
                    }
                }
            }
        });

        return binding.getRoot();
    }

    @SuppressLint({"ResourceType", "UseCompatLoadingForColorStateLists"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.placeView.setItemAnimator(null);

        boolean isNightModeOn = false;
        int currentNightMode = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                isNightModeOn = false;
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                isNightModeOn = true;
                break;
        }

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
        supportMapFragment.getMapAsync(this);

        if (Locale.getDefault().toString().startsWith("en")) {
            for (PlaceModel placeModel : Constant.places) {
                Chip chip = new Chip(requireContext());
                chip.setText(placeModel.getName());
                chip.setId(placeModel.getId());
                chip.setPadding(8, 8, 8, 8);
                chip.setChipIcon(ResourcesCompat.getDrawable(getResources(), placeModel.getDrawableId(), null));
                chip.setChipBackgroundColor(getResources().getColorStateList(R.color.orange_secondary_dark));

                if (isNightModeOn) {
                    chip.setTextColor(getResources().getColor(R.color.light_gray, null));
                    chip.setChipIconTint(getResources().getColorStateList(R.color.light_gray));
                } else {
                    chip.setTextColor(getResources().getColor(R.color.white, null));
                    chip.setChipIconTint(getResources().getColorStateList(R.color.white));
                }

                chip.setIconStartPadding(10);
                chip.setCheckable(true);
                chip.setCheckedIconVisible(false);

                binding.placesGroup.addView(chip);
            }

            setUpRecyclerView();
            getUserSavedLocations();

        } else if (Locale.getDefault().toString().startsWith("pl")) {
            for (PlaceModel placeModel : Constant.placesPL) {
                Chip chip = new Chip(requireContext());
                chip.setText(placeModel.getName());
                chip.setId(placeModel.getId());
                chip.setPadding(8, 8, 8, 8);
                chip.setChipIcon(ResourcesCompat.getDrawable(getResources(), placeModel.getDrawableId(), null));
                chip.setChipBackgroundColor(getResources().getColorStateList(R.color.orange_secondary_dark));


                if (isNightModeOn) {
                    chip.setTextColor(getResources().getColor(R.color.light_gray, null));
                    chip.setChipIconTint(getResources().getColorStateList(R.color.light_gray));
                } else {
                    chip.setTextColor(getResources().getColor(R.color.white, null));
                    chip.setChipIconTint(getResources().getColorStateList(R.color.white));
                }

                chip.setIconStartPadding(10);
                chip.setCheckable(true);
                chip.setCheckedIconVisible(false);

                binding.placesGroup.addView(chip);
            }

            setUpRecyclerView();
            getUserSavedLocations();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;

        int currentNightMode = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                mGoogleMap.setMapStyle(mapStyleOptions);
                break;
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationPermission = true;
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                isLocationPermission = false;
                return;
            }

            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.getUiSettings().setTiltGesturesEnabled(true);

            getLocationUpdates();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.location_permission_title))
                    .setMessage(getString(R.string.app_name) + " " + getString(R.string.location_permission_message))
                    .setPositiveButton(getString(R.string.positive_button_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_REQUEST_CODE);
                        }
                    }).create()
                    .show();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_REQUEST_CODE);
        }

        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                getLocationUpdates();
                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isLocationPermission = true;
                    getLocationUpdates();
                } else {
                    Snackbar.make(requireActivity().findViewById(R.id.fragment_map), getText(R.string.location_permission_denied), Snackbar.LENGTH_LONG).show();
                }
        }
    }

    public void getLocationUpdates() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        Task<LocationSettingsResponse> locationSettingsResponseTask = LocationServices
                .getSettingsClient(getContext())
                .checkLocationSettings(builder.build());

        locationSettingsResponseTask.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(com.google.android.gms.common.api.ApiException.class);
                } catch (com.google.android.gms.common.api.ApiException e) {
                    if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        try {
                            resolvableApiException.startResolutionForResult(getActivity(), LOCATION_SENSOR);
                        } catch (IntentSender.SendIntentException sendIntentException) {
                            sendIntentException.printStackTrace();
                        }
                    }
                    if (e.getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                        Toast.makeText(getActivity(), getString(R.string.location_change_unavailable), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        Log.d(TAG, "onLocationResult: " + " " + location.getLatitude() + " " + location.getLongitude());
                    }
                }
            }
        };

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermission = false;
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Starting location updates.");
                }
            }
        });

        getCurrentLocation();
    }

    private void getCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermission = false;
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location;
                moveToLocation(currentLocation);
            }
        });
    }

    private void moveToLocation(Location location) {
        if (location != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), ZOOM);
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title(getString(R.string.current_location))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .snippet(firebaseAuth.getCurrentUser().getDisplayName());

            if (currentMarker != null) {
                currentMarker.remove();
            }

            currentMarker = mGoogleMap.addMarker(markerOptions);
            currentMarker.setTag(703);
            mGoogleMap.animateCamera(cameraUpdate);
        }
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        Log.d(TAG, "Stopping location updates.");
    }

    @Override
    public void onPause() {
        super.onPause();

        if (fusedLocationProviderClient != null) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (fusedLocationProviderClient != null) {
            startLocationUpdates();
        }

        if (currentMarker != null) {
            currentMarker.remove();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case LOCATION_SENSOR:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getContext(), getString(R.string.location_sensor_enabled), Toast.LENGTH_SHORT).show();
                }
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getContext(), getString(R.string.location_sensor_disabled), Toast.LENGTH_LONG).show();
                }
        }
    }

    private void getPlaces(String placeName) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (currentLocation != null) {
                String url = API_URL
                        + currentLocation.getLatitude() + "," + currentLocation.getLongitude()
                        + "&radius=" + radius + "&type=" + placeName + "&key=" +
                        getResources().getString(R.string.API_KEY);

                mapsService.getPlaces(url).enqueue(new Callback<GoogleResponseModel>() {
                    @Override
                    public void onResponse(Call<GoogleResponseModel> call, Response<GoogleResponseModel> response) {
                        Gson gson = new Gson();
                        String res = gson.toJson(response.body());
                        Log.d(TAG, "onResponse: " + res);

                        if (response.errorBody() == null) {
                            if (response.body() != null) {
                                if (response.body().getGooglePlaceModelList() != null && response.body().getGooglePlaceModelList().size() > 0) {
                                    googlePlaceModelList.clear();
                                    mGoogleMap.clear();

                                    for (int i = 0; i < response.body().getGooglePlaceModelList().size(); i++) {
                                        if (userSavedPlaceId.contains(response.body().getGooglePlaceModelList().get(i).getPlaceId())) {
                                            response.body().getGooglePlaceModelList().get(i).setSaved(true);
                                        }
                                        googlePlaceModelList.add(response.body().getGooglePlaceModelList().get(i));
                                        addMarker(response.body().getGooglePlaceModelList().get(i), i);
                                    }
                                    googlePlaceAdapter.setGooglePlaceModelList(googlePlaceModelList);
                                } else {
                                    moveBackToLocation();
                                    Toast.makeText(getContext(), getString(R.string.no_places), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Log.d(TAG, "onResponse: " + response.errorBody());
                            Toast.makeText(requireContext(), getString(R.string.error) + response.errorBody(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GoogleResponseModel> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t);
                    }
                });
            }
        }
    }

    private void addMarker(GooglePlaceModel googlePlaceModel, int position) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(googlePlaceModel.getGeometry().getLocation().getLat(), googlePlaceModel.getGeometry().getLocation().getLng()))
                .title(googlePlaceModel.getName())
                .snippet(googlePlaceModel.getVicinity());
        markerOptions.icon(getCustomIcon());
        mGoogleMap.addMarker(markerOptions).setTag(position);
    }

    private BitmapDescriptor getCustomIcon() {
        Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_my_location_24);
        background.setTint(getResources().getColor(com.google.android.libraries.places.R.color.quantum_googred900, null));
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void setUpRecyclerView() {
        binding.placeView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.placeView.setHasFixedSize(false);
        googlePlaceAdapter = new GooglePlaceAdapter(this);
        binding.placeView.setAdapter(googlePlaceAdapter);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.placeView);

        binding.placeView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();

                if (position > -1) {
                    GooglePlaceModel googlePlaceModel = googlePlaceModelList.get(position);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(googlePlaceModel.getGeometry().getLocation().getLat(),
                            googlePlaceModel.getGeometry().getLocation().getLng()), 20));
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        int markerTag = (int) marker.getTag();
        Log.d(TAG, "onMarkerClick: " + markerTag);

        binding.placeView.scrollToPosition(markerTag);
        return false;
    }

    @Override
    public void onSaveClick(GooglePlaceModel googlePlaceModel) {
        if (userSavedPlaceId.contains(googlePlaceModel.getPlaceId())) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.remove_place_title))
                    .setMessage(getString(R.string.remove_place_question))
                    .setPositiveButton(getString(R.string.yes_answer), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removePlace(googlePlaceModel);
                        }
                    })
                    .setNegativeButton(getString(R.string.no_answer), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create().show();
        } else {
            locationReference.child(googlePlaceModel.getPlaceId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        SavedPlaceModel savedPlaceModel = new SavedPlaceModel(googlePlaceModel.getName(), googlePlaceModel.getVicinity(),
                                googlePlaceModel.getPlaceId(), googlePlaceModel.getRating(),
                                googlePlaceModel.getUserRatingsTotal().doubleValue(),
                                googlePlaceModel.getGeometry().getLocation().getLat(),
                                googlePlaceModel.getGeometry().getLocation().getLng());

                        saveLocation(savedPlaceModel);
                    }

                    saveUserLocation(googlePlaceModel.getPlaceId());
                    int index = googlePlaceModelList.indexOf(googlePlaceModel);
                    googlePlaceModelList.get(index).setSaved(true);
                    googlePlaceAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void removePlace(GooglePlaceModel googlePlaceModel) {
        userSavedPlaceId.remove(googlePlaceModel.getPlaceId());
        int index = googlePlaceModelList.indexOf(googlePlaceModel);
        googlePlaceModelList.get(index).setSaved(false);
        googlePlaceAdapter.notifyDataSetChanged();

        Snackbar.make(binding.getRoot(), getString(R.string.place_removed), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.undo_action), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userSavedPlaceId.add(googlePlaceModel.getPlaceId());
                        googlePlaceModelList.get(index).setSaved(true);
                        googlePlaceAdapter.notifyDataSetChanged();
                    }
                })
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        userLocationReference.setValue(userSavedPlaceId);
                    }
                }).show();
    }

    private void saveUserLocation(String placeId) {
        userSavedPlaceId.add(placeId);
        userLocationReference.setValue(userSavedPlaceId);
        Toast.makeText(getContext(), getString(R.string.place_saved), Toast.LENGTH_SHORT).show();
    }

    private void saveLocation(SavedPlaceModel savedPlaceModel) {
        locationReference.child(savedPlaceModel.getPlaceId()).setValue(savedPlaceModel);
    }

    private void getUserSavedLocations() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid()).child("Saved locations");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String placeId = ds.getValue(String.class);
                        userSavedPlaceId.add(placeId);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void moveBackToLocation() {
        mGoogleMap.clear();
        googlePlaceModelList.clear();
        googlePlaceAdapter.setGooglePlaceModelList(googlePlaceModelList);
        moveToLocation(currentLocation);
    }
}