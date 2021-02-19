package com.workchat.core.chat.locations;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.ForwardScope;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Tìm user chat từ danh ba
 */
public class SearchLocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private AppCompatActivity context = this;

    @BindView(R2.id.rootLayout)
    LinearLayout rootLayout;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.rv)
    RecyclerView rv;
    @BindView(R2.id.txtSend)
    TextView txtSend;

    @BindView(R2.id.txtLoading)
    TextView txtLoading;

    private void initRecyclerView() {
        //init rv
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter = new Search_Location_Adapter(context);
        rv.setAdapter(adapter);
        adapter.setItemClickListener(new Search_Location_Adapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                whenItemClick(position);

            }
        });
    }

    private void whenItemClick(int position) {
        if (adapter != null) {
            MyLocation item = adapter.getItem(position);
            if (item.getLat() > 0) {//co roi thi show
                LatLng latLng = new LatLng(item.getLat(), item.getLon());
                showCameraToPosition(latLng, LEVEL_ZOOM_DEFAULT, item.getName());

            } else {//chua co thong tin lat,lon thi lay
                getPlaceAndShowMarker(item);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initPlace();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAddress();
            }
        });

        registerKeyboardShowHide();
        initRecyclerView();
        initSearch();
        initPermission();

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.editText1)
    EditText txtSearch;

    private void initSearch() {
        final int padding = getResources().getDimensionPixelOffset(R.dimen.dimen_10dp);
        final Drawable drawableLeft = ContextCompat.getDrawable(context, R.drawable.ic_search_grey_500_24dp);
        final Drawable drawableRight = ContextCompat.getDrawable(context, R.drawable.ic_close_grey_500_24dp);
        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    txtSearch.clearFocus();
                    MyUtils.hideKeyboard(context, txtSearch);
                    setShow(true);
                }

                return true;
            }
        });
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String text = s.toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    txtSearch.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableRight, null);
//                    txtSearch.setText(text);
//                    address = text;
                } else {
                    txtSearch.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
//                    setTextLocation(location);
                }

                if (!mTyping) {
                    mTyping = true;
                }
                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }
        });

        txtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Drawable d = txtSearch.getCompoundDrawables()[DRAWABLE_RIGHT];
                    if (d != null) {
                        float left = event.getRawX() - padding;
                        float right = event.getRawX() + padding;
                        float pos = (txtSearch.getRight() - d.getBounds().width());
                        if (event.getRawX() >= pos ||
                                left >= pos ||
                                right >= pos
                        ) {
                            // your action here
                            txtSearch.setText("");
                            MyUtils.hideKeyboard(context, txtSearch);
                            setShow(true);
                            //restore lai list (vitri hien tai + vi tri xung quanh)
                            if (listNears != null && listNears.size() > 0) {
                                adapter.setData2(listNears, mCurrentLocation);
                                whenItemClick(0);
                            }


                            return true;
                        }
                    }

                }
                return false;
            }

        });

        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    setShow(true);
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });


    }

    ////////////////////////////////////////////////////////////////////////////////////
    private static final int TYPING_TIMER_LENGTH = 500;
    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler();
    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            //dang stop thi search
            String keyword = txtSearch.getText().toString().trim();
            setShow(TextUtils.isEmpty(keyword));
            suggestPlace(keyword);

        }
    };


    ////////////////////////////////////////////////////////////////////////////////////
    public static final int LEVEL_ZOOM_DEFAULT = 18;
    private GoogleMap mGoogleMap;
    private SupportMapFragment supportMapFragment;
    @BindView(R2.id.layoutMap)
    FrameLayout layoutMap;

    private void setShow(boolean isShow) {
        if (isShow) {
            if (layoutMap.getVisibility() != View.VISIBLE) {
                layoutMap.setVisibility(View.VISIBLE);
            }
        } else {
            if (layoutMap.getVisibility() != View.GONE) {
                layoutMap.setVisibility(View.GONE);
            }
        }
    }

    private void initViews() {
        initMap();

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        supportMapFragment.getMapAsync(this);

        /*supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                    }
                });

                *//*LatLng framgiaVietnam = new LatLng(21.0166458, 105.7841248);
                mGoogleMap
                        .addMarker(
                                new MarkerOptions().position(framgiaVietnam).title("Framgia Vietnam"));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(framgiaVietnam, LEVEL_ZOOM_DEFAULT));*//*
                if(mCurrentLocation!=null){
                    showMarkerMyLocation(mCurrentLocation);
                }
            }
        });*/
    }

    private GoogleApiClient mGoogleApiClient;

    private MyLocation mCurrentLocation;

    private LatLng mLatLngSearchPosition;

    private LinearLayout radius15, radius3, radius5, radius10, radius30, radiusall;

    private void initMap() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) mGoogleApiClient.disconnect();
    }

    private LocationManager locationManager;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            final Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation != null) {
                setMyCurrent(lastLocation);
            } else {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2 * 1000, 1, locationListener);

            }


        }
    }

    private void setMyCurrent(Location lastLocation) {

        mCurrentLocation = new MyLocation();
        mCurrentLocation.setCurrentLocation(true);
        mCurrentLocation.setLat(lastLocation.getLatitude());
        mCurrentLocation.setLon(lastLocation.getLongitude());
        mCurrentLocation.setName(getString(R.string.current_location_checkin));

        if (mLatLngSearchPosition == null) {
            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            showCameraToPosition(latLng, LEVEL_ZOOM_DEFAULT, "");
            showMarkerMyLocation(latLng);

            getCurrentPlaces();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
//        mLatLngSearchPosition = latLng;
//        showCameraToPosition(mLatLngSearchPosition, LEVEL_ZOOM_DEFAULT);
//        showMarker(mLatLngSearchPosition,"");

        /*if (mRadiusSearch.get() <= Constant.RADIUS_DEFAULT
                || mRadiusSearch.get() >= Constant.RADIUS_ALL) {
            showCameraToPosition(mLatLngSearchPosition, LEVEL_ZOOM_DEFAULT);
        } else {
            final LatLngBounds circleBounds = new LatLngBounds(
                    locationMinMax(false, mLatLngSearchPosition, mRadiusSearch.get()),
                    locationMinMax(true, mLatLngSearchPosition, mRadiusSearch.get()));
            showCameraToPosition(circleBounds, 200);
        }

        showCircleToGoogleMap(mLatLngSearchPosition, mRadiusSearch.get());*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
//        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
//        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.setTrafficEnabled(true);
        mGoogleMap.setBuildingsEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);

            //Get Last Known Location and convert into Current Longitute and Latitude
            /*final LocationManager mlocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            final Location currentGeoLocation = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            double currentLat = currentGeoLocation.getLatitude();
            double currentLon = currentGeoLocation.getLongitude();

            LatLng currentLatLng = new LatLng(currentLat,currentLon);
            showMarkerMyLocation(currentLatLng);*/

        } else {
            //            Common.checkAndRequestPermissionsGPS(getActivity());
        }

        /*mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //get latlng at the center by calling
                LatLng midLatLng = mGoogleMap.getCameraPosition().target;
                showMarkerMyLocation(midLatLng);
            }
        });*/
    }

    public void showCameraToPosition(LatLng position, float zoomLevel, String title) {

        //move
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(position)
                .zoom(zoomLevel)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();

        if (mGoogleMap != null) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), null);
        }

        //marker
        showMarker(position, title);
    }


    public void showMarkerMyLocation(LatLng position) {
        /*mGoogleMap.clear();
        MarkerOptions markerOptions = new MarkerOptions().position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_active));
        mGoogleMap.addMarker(markerOptions);*/


        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return MyUtils.getAddress(context, String.valueOf(position.latitude), String.valueOf(position.longitude));
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                mGoogleMap.clear();
                mGoogleMap
                        .addMarker(
                                new MarkerOptions().position(position).title(s));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, LEVEL_ZOOM_DEFAULT));

                //bo sung thong tin cho dia diem dang dung
                if (!TextUtils.isEmpty(s)) {
                    mCurrentLocation.setAddress(s);
                }

                //add tam vi tri dang co truoc
                if (adapter != null) {
                    adapter.setData(new ArrayList<MyLocation>(), mCurrentLocation);
                }

            }
        }.execute();


    }

    public void showMarker(LatLng position, String title) {

        if (mGoogleMap != null) {
            mGoogleMap.clear();
            mGoogleMap
                    .addMarker(
                            new MarkerOptions().position(position).title(title));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, LEVEL_ZOOM_DEFAULT));
        }


    }

    @Override
    public void onClick(View v) {

    }

    /*public void showCircleToGoogleMap(LatLng position, float radius) {
        if (position == null) {
            return;
        }
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(position);
        //Radius in meters
        circleOptions.radius(radius * 1000);
        circleOptions.fillColor(getResources().getColor(R.color.circle_on_map));
        circleOptions.strokeColor(getResources().getColor(R.color.circle_on_map));
        circleOptions.strokeWidth(0);
        if (mGoogleMap != null) {
            mGoogleMap.addCircle(circleOptions);
        }
    }

    private LatLng locationMinMax(boolean positive, LatLng position, float radius) {
        double sign = positive ? 1 : -1;
        double dx = (sign * radius * 1000) / 6378000 * (180 / Math.PI);
        double lat = position.latitude + dx;
        double lon = position.longitude + dx / Math.cos(position.latitude * Math.PI / 180);
        return new LatLng(lat, lon);
    }
    */


    ////////////////////////////////////////////////////////////////////////////////
    private Search_Location_Adapter adapter;
    private PlacesClient placesClient;
    private RectangularBounds bounds;

    private void initPlace() {
        //INIT PLACE
        if (!com.google.android.libraries.places.api.Places.isInitialized()) {
            Places.initialize(getApplicationContext(), ChatApplication.Companion.getGOOGLE_MAPS_ANDROID_API_KEY());
        }
        placesClient = Places.createClient(this);
        bounds = RectangularBounds.newInstance(
                new LatLng(-34.041458, 150.790100),
                new LatLng(-33.682247, 151.383362)
        );
    }

    private void suggestPlace(String query) {
        if (!TextUtils.isEmpty(query)) {
            // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
            // and once again when the user makes a selection (for example when calling fetchPlace()).
            AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

            // Use the builder to create a FindAutocompletePredictionsRequest.
            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    // Call either setLocationBias() OR setLocationRestriction().
                    .setLocationBias(bounds)
                    //.setLocationRestriction(bounds)
                    .setCountry("vn")
//                    .setTypeFilter(TypeFilter.ADDRESS)
                    .setSessionToken(token)
                    .setQuery(query)
                    .build();

            placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                List<AutocompletePrediction> list = response.getAutocompletePredictions();
                adapter.setData3(list, mCurrentLocation);
                whenItemClick(0);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
//                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                }
            });
        }
    }

    private void getPlace(MyLocation location) {
        if (location != null) {
            String placeId = location.getPlaceId();
            if (!TextUtils.isEmpty(placeId)) {
                // Specify the fields to return.
                List<com.google.android.libraries.places.api.model.Place.Field> placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME);

                // Construct a request object, passing the place ID and fields array.
                FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    placeSelected = response.getPlace();
                    if (placeSelected != null) {
                        LatLng latLng = placeSelected.getLatLng();
                        if (latLng != null) {
//                            lat = String.valueOf(latLng.latitude);
//                            lon = String.valueOf(latLng.longitude);
//                            address = placeSelected.getAddress();

                            location.setLat(latLng.latitude);
                            location.setLon(latLng.longitude);
                            location.setAddress(placeSelected.getAddress());
                            returnLocation(location);
                        }

                    }
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        int statusCode = apiException.getStatusCode();
                        // Handle error with given status code.
//                    Log.e(TAG, "Place not found: " + exception.getMessage());
                    }
                });
            }
        }

    }

    private void getPlaceAndShowMarker(MyLocation location) {
        if (location != null) {
            String placeId = location.getPlaceId();
            if (!TextUtils.isEmpty(placeId)) {
                // Specify the fields to return.
                List<Place.Field> placeFields =
                        Arrays.asList(
                                Place.Field.ID,
                                Place.Field.NAME,
                                Place.Field.LAT_LNG,
                                Place.Field.ADDRESS
                        );

                // Construct a request object, passing the place ID and fields array.
                FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    placeSelected = response.getPlace();
                    if (placeSelected != null) {
                        LatLng latLng = placeSelected.getLatLng();
                        if (latLng != null) {
//                            lat = String.valueOf(latLng.latitude);
//                            lon = String.valueOf(latLng.longitude);
//                            address = placeSelected.getAddress();

                            location.setLat(latLng.latitude);
                            location.setLon(latLng.longitude);
                            location.setAddress(placeSelected.getAddress());
                            //update adapter thong tin lay dc
                            if (adapter != null) {
                                adapter.updateItem(location);
                            }

                            showCameraToPosition(latLng, LEVEL_ZOOM_DEFAULT, placeSelected.getName());
                        }

                    }
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        int statusCode = apiException.getStatusCode();
                        // Handle error with given status code.
//                    Log.e(TAG, "Place not found: " + exception.getMessage());
                    }
                });
            }
        }

    }

    private Place placeSelected;
    String address = "";
    String lat = "", lon = "";//neu dia chi khong ton tai

    private void confirmAddress() {
        /*if (!TextUtils.isEmpty(address)) {
            com.workchat.core.plan.Place place = new com.workchat.core.plan.Place(lat, lon, address);
            Intent data = new Intent();
            data.putExtra(com.workchat.core.plan.Place.PLACE_MODEL, place);
            setResult(RESULT_OK, data);

            //luu lai vi tri
//            db.putObject(com.workchat.core.plan.Place.PLACE_MODEL, place);

            hideKeyboard();
//            finish();
        }*/

        hideKeyboard();
        MyLocation location = adapter.getLocationSelected();
        if (location != null) {

            //nếu có đủ tọa độ lat,lon thì OK, ngược lại thì lấy theo placeId
            if (location.getLat() != 0 && location.getLon() != 0) {
                returnLocation(location);
            } else {
                getPlace(location);
            }
        }
    }

    private void returnLocation(MyLocation location) {
        if (location != null) {
            Intent data = new Intent();
            data.putExtra(MyLocation.MY_LOCATION, location);
            setResult(RESULT_OK, data);
            finish();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String text = txtSearch.getText().toString();
            if (!TextUtils.isEmpty(text)) {
                hideKeyboard();
            } else {
                finish();
            }
        }
        return true;
    }


    private void hideKeyboard() {
        try {
            setShow(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<PlaceLikelihood> listNears = new ArrayList<>();

    private void getCurrentPlaces() {

        // Use fields to define the data types to return.
//        List<Place.Field> placeFields = Collections.singletonList(Place.Field.NAME);
        List<Place.Field> placeFields = new ArrayList<>();
        placeFields.add(Place.Field.NAME);
        placeFields.add(Place.Field.ADDRESS);
        placeFields.add(Place.Field.LAT_LNG);


// Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.newInstance(placeFields);

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {

                txtLoading.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();
                    /*for (PlaceLikelihood place : response.getPlaceLikelihoods()) {
                        String add = place.getPlace().getName() + " " + place.getLikelihood() + " " + place.getPlace().getAddress();
                        Log.i("TAG", add);
                    }*/
                    if (response != null && response.getPlaceLikelihoods().size() > 0) {
                        adapter.setData2(listNears = response.getPlaceLikelihoods(), mCurrentLocation);
                        whenItemClick(0);
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e("TAG", "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
//            checkPermission();
        }

    }


    //////////////////////////////////////////////////////////////////////////////////////////
    /*private String key = "";
    private GoogleMapAPI googleMapAPI;
    private void initGoogleMap(){
        key = getString(R.string.GOOGLE_MAPS_ANDROID_API_KEY);
        googleMapAPI = APIClient.getClient().create(GoogleMapAPI.class);
    }*/

    private boolean isFirst = true;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (isFirst) {
                setMyCurrent(location);
                isFirst = false;
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    private void registerKeyboardShowHide() {
        rootLayout.getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {

                                Rect r = new Rect();
                                rootLayout.getWindowVisibleDisplayFrame(r);
                                int screenHeight = rootLayout.getRootView().getHeight();

                                int keypadHeight = screenHeight - r.bottom;
                                if (keypadHeight > screenHeight * 0.15) {
                                    // keyboard is opened
                                    setShow(false);
                                } else {
                                    // keyboard is closed
                                    setShow(true);
                                }
                            }
                        });
    }


    private static final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};

    private void initPermission() {
        PermissionX.init(this)
                .permissions(perms)
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(ExplainScope scope, List<String> deniedList) {
                        scope.showRequestReasonDialog(deniedList, getString(R.string.deny_permission_notify), "OK", "Cancel");
                    }
                })
                .onForwardToSettings(new ForwardToSettingsCallback() {
                    @Override
                    public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                        scope.showForwardToSettingsDialog(deniedList, getString(R.string.deny_permission_notify), "OK", "Cancel");
                    }
                })
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                        if (allGranted) {
                            initViews();
                            onStart();
                        } else {
                            MyUtils.showAlertDialog(context, getString(R.string.deny_permission_notify), true);
                        }
                    }
                });
    }
}
