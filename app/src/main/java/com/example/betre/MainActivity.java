package com.example.betre;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();
    private static final String TAG = "MainActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

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

        // Initialize fragment map with the fragment instances
        fragmentMap.put(R.id.navigation_explore, new ExploreFragment());
        fragmentMap.put(R.id.navigation_search, new SearchFragment());
        fragmentMap.put(R.id.navigation_create, new CreateFragment());
        fragmentMap.put(R.id.navigation_profile, new ProfileFragment());
        fragmentMap.put(R.id.navigation_inbox, new InboxFragment());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.home_content, Objects.requireNonNull(fragmentMap.get(R.id.navigation_explore))).commit();
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = fragmentMap.get(item.getItemId());

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_content, selectedFragment).commit();
                    return true;
                }
                return false;
            }
        });
    }
}

