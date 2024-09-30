package com.example.betre;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateFragment extends Fragment {

    private ImageView userProfileImage;
    private TextView userName, selectImage, addLocation;
    private EditText postContent;
    private Button buttonDiscard, buttonPost;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    public CreateFragment() {

    }

    public static CreateFragment newInstance(String param1, String param2) {
        CreateFragment fragment = new CreateFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        userProfileImage = view.findViewById(R.id.user_profile_image);
        userName = view.findViewById(R.id.user_name);
        selectImage = view.findViewById(R.id.select_image);
        addLocation = view.findViewById(R.id.add_location);
        postContent = view.findViewById(R.id.post_content);
        buttonDiscard = view.findViewById(R.id.button_discard);
        buttonPost = view.findViewById(R.id.button_post);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            loadUserProfile(user.getUid());
        }

        buttonDiscard.setOnClickListener(v -> {
            postContent.setText("");
            Toast.makeText(getActivity(), "Post Discarded", Toast.LENGTH_SHORT).show();
        });

        buttonPost.setOnClickListener(v -> {
            String content = postContent.getText().toString();
            if (!content.isEmpty()) {
                Toast.makeText(getActivity(), "Post Created", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Content cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        selectImage.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Select Image clicked", Toast.LENGTH_SHORT).show();
        });

        addLocation.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Add Location clicked", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void loadUserProfile(String userId) {
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);

                    if (username != null) {
                        userName.setText(username);
                    }

                    if (profileImageUrl != null) {
                        Glide.with(CreateFragment.this)
                                .load(profileImageUrl)
                                .circleCrop()
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .into(userProfileImage);
                    }
                } else {
                    Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
