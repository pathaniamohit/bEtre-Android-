package com.example.betre;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ModeratorActivity extends AppCompatActivity {

    private static final String TAG = "ModeratorActivity";  // Define TAG for logging
    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_moderator);

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Initialize fragments and put them in fragmentMap
        fragmentMap.put(R.id.navigation_dashboard_mod, new DashboardModFragment());
        fragmentMap.put(R.id.navigation_activity_mod, new ActivityModFragment());
        fragmentMap.put(R.id.navigation_inbox_mod, new InboxModFragment());
        fragmentMap.put(R.id.navigation_profile_mod, new ProfileModFragment());
        fragmentMap.put(R.id.navigation_flagged_mod, new FlaggedModFragment());

        // Load the default fragment
        Fragment defaultFragment = fragmentMap.get(R.id.navigation_dashboard_mod);
        if (defaultFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_content, defaultFragment)
                    .commit();
        } else {
            Log.e(TAG, "Default fragment is null");
        }

        if (savedInstanceState == null) {
            Fragment dashboardFragment = fragmentMap.get(R.id.navigation_dashboard_mod);
            if (dashboardFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.home_content, dashboardFragment)
                        .commit();
            } else {
                Log.e(TAG, "Dashboard fragment is null");
            }
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = fragmentMap.get(item.getItemId());
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_content, selectedFragment)
                            .commit();
                    return true;
                }
                Log.e(TAG, "Selected fragment is null for item ID: " + item.getItemId());
                return false;
            }
        });
    }
}
