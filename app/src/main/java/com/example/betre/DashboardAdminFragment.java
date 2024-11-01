//package com.example.betre;
//
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link DashboardAdminFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.example.betre.adapters.UserAdapter;
//import com.example.betre.models.User;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import java.util.ArrayList;
//import java.util.List;
//
//public class DashboardAdminFragment extends Fragment {
//
//    private RecyclerView recyclerViewUsers;
//    private UserAdapter userAdapter;
//    private List<User> userList;
//    private List<User> filteredList;
//    private DatabaseReference usersDB;
//    private EditText searchEditText;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_dashboard_admin, container, false);
//
//        recyclerViewUsers = view.findViewById(R.id.recyclerViewUsers);
//        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        // Initialize lists
//        userList = new ArrayList<>();
//        filteredList = new ArrayList<>();
//
//        // Initialize adapter with filteredList
//        userAdapter = new UserAdapter(filteredList, getContext());
//        recyclerViewUsers.setAdapter(userAdapter);
//
//        searchEditText = view.findViewById(R.id.searchEditText);
//
//        usersDB = FirebaseDatabase.getInstance().getReference("users");
//        loadUsers();
//
//        searchEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                filterUsers(s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        return view;
//    }
//
//
//
//    private void loadUsers() {
//        usersDB.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                userList.clear();
//                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                    User user = userSnapshot.getValue(User.class);
//                    if (user != null) {
//                        user.setUserId(userSnapshot.getKey()); // Set the user ID
//                        userList.add(user);
//                    }
//                }
//
//                // Update filteredList with all users initially
//                filteredList.clear();
//                filteredList.addAll(userList);
//                userAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Failed to load users.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//
//    private void filterUsers(String query) {
//        filteredList.clear();
//        if (query.isEmpty()) {
//            // If search query is empty, show all users
//            filteredList.addAll(userList);
//        } else {
//            // Filter users by username or email
//            for (User user : userList) {
//                if (user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
//                        user.getEmail().toLowerCase().contains(query.toLowerCase())) {
//                    filteredList.add(user);
//                }
//            }
//        }
//        userAdapter.notifyDataSetChanged(); // Notify adapter of data changes
//    }
//}

//package com.example.betre;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.example.betre.adapters.UserAdapter;
//import com.example.betre.models.User;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class DashboardAdminFragment extends Fragment {
//
//    private RecyclerView recyclerViewUsers;
//    private UserAdapter userAdapter;
//    private List<User> userList;
//    private List<User> filteredList;
//    private DatabaseReference usersDB;
//    private EditText searchEditText;
//    private String currentUserRole; // To store the current user's role
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.fragment_dashboard_admin, container, false);
//
//        recyclerViewUsers = view.findViewById(R.id.recyclerViewUsers);
//        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        // Initialize lists
//        userList = new ArrayList<>();
//        filteredList = new ArrayList<>();
//
//        // Retrieve current user's role
//        getCurrentUserRole();
//
//        // Initialize adapter with filteredList and currentUserRole
//        userAdapter = new UserAdapter(filteredList, getContext(), currentUserRole);
//        recyclerViewUsers.setAdapter(userAdapter);
//
//        searchEditText = view.findViewById(R.id.searchEditText);
//
//        usersDB = FirebaseDatabase.getInstance().getReference("users");
//        loadUsers();
//
//        searchEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // No action needed
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                filterUsers(s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // No action needed
//            }
//        });
//
//        return view;
//    }
//
//    /**
//     * Retrieve the current user's role from Firebase Realtime Database.
//     */
//    private void getCurrentUserRole() {
//        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
//
//        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    String role = snapshot.child("role").getValue(String.class);
//                    currentUserRole = role != null ? role : "user";
//
//                    // Notify adapter to update delete button visibility
//                    userAdapter.notifyDataSetChanged();
//                } else {
//                    currentUserRole = "user";
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Failed to retrieve user role.", Toast.LENGTH_SHORT).show();
//                currentUserRole = "user";
//            }
//        });
//    }
//
//    private void loadUsers() {
//        usersDB.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                userList.clear();
//                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                    User user = userSnapshot.getValue(User.class);
//                    if (user != null) {
//                        user.setUserId(userSnapshot.getKey()); // Set the user ID
//                        userList.add(user);
//                    }
//                }
//
//                // Update filteredList with all users initially
//                filteredList.clear();
//                filteredList.addAll(userList);
//                userAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Failed to load users.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void filterUsers(String query) {
//        filteredList.clear();
//        if (query.isEmpty()) {
//            // If search query is empty, show all users
//            filteredList.addAll(userList);
//        } else {
//            // Filter users by username or email
//            for (User user : userList) {
//                if (user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
//                        user.getEmail().toLowerCase().contains(query.toLowerCase())) {
//                    filteredList.add(user);
//                }
//            }
//        }
//        userAdapter.notifyDataSetChanged(); // Notify adapter of data changes
//    }
//}


// File: app/src/main/java/com/example/betre/DashboardAdminFragment.java
package com.example.betre;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.betre.adapters.UserAdapter;
import com.example.betre.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardAdminFragment extends Fragment {

    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList;
    private List<User> filteredList;
    private DatabaseReference usersDB;
    private EditText searchEditText;
    private String currentUserRole; // To store the current user's role

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard_admin, container, false);

        recyclerViewUsers = view.findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize lists
        userList = new ArrayList<>();
        filteredList = new ArrayList<>();

        searchEditText = view.findViewById(R.id.searchEditText);

        usersDB = FirebaseDatabase.getInstance().getReference("users");

        // Retrieve current user's role first
        getCurrentUserRole(view);

        // Set up search functionality
        setupSearchFunctionality();

        return view;
    }

    /**
     * Retrieve the current user's role from Firebase Realtime Database.
     * After fetching, initialize the adapter and load users.
     */
    private void getCurrentUserRole(View view) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String role = snapshot.child("role").getValue(String.class);
                    currentUserRole = role != null ? role : "user";
                    Log.d("DashboardAdminFragment", "Current user role: " + currentUserRole);

                    // Initialize adapter with filteredList and currentUserRole
                    userAdapter = new UserAdapter(filteredList, getContext(), currentUserRole);
                    recyclerViewUsers.setAdapter(userAdapter);

                    // Now load users
                    loadUsers();
                } else {
                    currentUserRole = "user";
                    Log.d("DashboardAdminFragment", "User role not found. Defaulting to 'user'.");

                    // Initialize adapter with filteredList and currentUserRole
                    userAdapter = new UserAdapter(filteredList, getContext(), currentUserRole);
                    recyclerViewUsers.setAdapter(userAdapter);

                    // Now load users
                    loadUsers();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to retrieve user role.", Toast.LENGTH_SHORT).show();
                currentUserRole = "user";

                // Initialize adapter with filteredList and currentUserRole
                userAdapter = new UserAdapter(filteredList, getContext(), currentUserRole);
                recyclerViewUsers.setAdapter(userAdapter);

                // Now load users
                loadUsers();
            }
        });
    }

    /**
     * Set up search functionality for filtering users.
     */
    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });
    }

    /**
     * Load users from Firebase Realtime Database.
     */
    private void loadUsers() {
        usersDB.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                userList.clear();
//                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                    User user = userSnapshot.getValue(User.class);
//                    if (user != null) {
//                        user.setUserId(userSnapshot.getKey()); // Set the user ID
//                        userList.add(user);
//                    }
//                }
//
//                // Update filteredList with all users initially
//                filteredList.clear();
//                filteredList.addAll(userList);
//                userAdapter.notifyDataSetChanged();
//            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    try {
                        User user = userSnapshot.getValue(User.class);
                        if (user != null) {
                            user.setUserId(userSnapshot.getKey()); // Set the user ID
                            userList.add(user);
                        }
                    } catch (DatabaseException e) {
                        Log.e("DashboardAdminFragment", "Failed to parse user: " + userSnapshot.getKey(), e);
                    }
                }

                // Update filteredList with all users initially
                filteredList.clear();
                filteredList.addAll(userList);
                userAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load users.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Filter users based on the search query.
     * @param query The search query string.
     */
    private void filterUsers(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            // If search query is empty, show all users
            filteredList.addAll(userList);
        } else {
            // Filter users by username or email
            for (User user : userList) {
                if (user.getUsername() != null && user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                        user.getEmail() != null && user.getEmail().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(user);
                }
            }
        }
        userAdapter.notifyDataSetChanged(); // Notify adapter of data changes
    }
}
