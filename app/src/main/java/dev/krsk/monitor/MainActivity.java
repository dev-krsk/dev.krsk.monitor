package dev.krsk.monitor;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationBarView;
import com.google.maps.android.clustering.ClusterManager;

import dev.krsk.monitor.ui.camera.CameraFragment;
import dev.krsk.monitor.vm.MapViewModel;
import dev.krsk.monitor.vo.Camera;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private MapViewModel mMapViewModel;

    private MapView mMapView;
    private GoogleMap mMap;

    private BottomNavigationView mNavView;
    private BottomSheetBehavior<LinearLayout> mBottomSheetBehavior;
    private TextView mBottomSheetTitle;
    private ImageView mBottomSheetClose;
    private FragmentTransaction mTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        setContentView(R.layout.activity_main);

        initializeBottomNavigation();
        initializeBottomSheet();
        initializeMap(savedInstanceState);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        ClusterManager<Camera> clusterManager = new ClusterManager<Camera>(this, mMap);
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Camera>() {
            @Override
            public boolean onClusterItemClick(Camera item) {
                mMapViewModel.setSelectedCamera(item);
                mBottomSheetTitle.setText(item.getTitle());

                if (mTransaction.isEmpty()) {
                    mTransaction.replace(R.id.fragmentBottomSheet, new CameraFragment()).commit();
                }

                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

                return true;
            }
        });

        mMapViewModel.getCameras().observe(this, cameras -> {
            // update UI
            clusterManager.clearItems();
            clusterManager.addItems(cameras);
        });

        mMap.setOnCameraIdleListener(clusterManager);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void initializeBottomNavigation() {
        mNavView = findViewById(R.id.nav_view);

        mNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == mNavView.getSelectedItemId()) {
                    return false;
                }

                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();

                return true;
            }
        });
    }

    private void initializeBottomSheet() {
        mTransaction = getSupportFragmentManager().beginTransaction();

        mBottomSheetTitle = findViewById(R.id.bottom_sheet_title);
        mBottomSheetClose = findViewById(R.id.bottom_sheet_close);
        mBottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheet));
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mBottomSheetClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                mMapViewModel.setSelectedCamera(null);
            }
        });

        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Toast.makeText(MainActivity.this, "STATE_COLLAPSED", Toast.LENGTH_SHORT).show();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Toast.makeText(MainActivity.this, "STATE_EXPANDED", Toast.LENGTH_SHORT).show();
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        Toast.makeText(MainActivity.this, "STATE_HALF_EXPANDED", Toast.LENGTH_SHORT).show();
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Toast.makeText(MainActivity.this, "STATE_DRAGGING", Toast.LENGTH_SHORT).show();
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Toast.makeText(MainActivity.this, "STATE_SETTLING", Toast.LENGTH_SHORT).show();
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Toast.makeText(MainActivity.this, "STATE_HIDDEN", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().popBackStack();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "OTHER_STATE", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Перемещаем кнопки управления картой, то отрытии BottomSheet
                mMap.setPadding(0,0,0, (int) (mMapView.getHeight() - bottomSheet.getY()));
            }
        });
    }

    private void initializeMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView = findViewById(R.id.mapView);

        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
    }
}