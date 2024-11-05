////package com.example.betre;
////
////import android.os.Bundle;
////import android.util.Log;
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.widget.ImageView;
////import android.widget.TextView;
////import android.widget.Toast;
////
////import androidx.annotation.NonNull;
////import androidx.annotation.Nullable;
////import androidx.fragment.app.Fragment;
////import androidx.recyclerview.widget.GridLayoutManager;
////import androidx.recyclerview.widget.RecyclerView;
////
////import com.bumptech.glide.Glide;
////import com.example.betre.adapters.ImageAdapter_profile;
////import com.example.betre.models.Post;
////import com.example.betre.models.User;
////import com.google.firebase.auth.FirebaseAuth;
////import com.google.firebase.database.DataSnapshot;
////import com.google.firebase.database.DatabaseError;
////import com.google.firebase.database.DatabaseReference;
////import com.google.firebase.database.FirebaseDatabase;
////import com.google.firebase.database.ValueEventListener;
////
////import java.util.ArrayList;
////import java.util.List;
////
////public class UserProfileFragment extends Fragment {
////    private static final String TAG = "UserProfileFragment";
////
////    private ImageView profileImage;
////    private TextView profileName, profileEmail, photosCount, followersCount, followsCount;
////    private FirebaseAuth mAuth;
////    private DatabaseReference mDatabase;
////    private RecyclerView postsRecyclerView;
////    private ImageAdapter_profile postAdapter;
////    private List<Post> postList;
////    private String userId;
////    ImageView back_button;
////
////
////    public UserProfileFragment() {}
////
////    public static UserProfileFragment newInstance(String userId) {
////        UserProfileFragment fragment = new UserProfileFragment();
////        Bundle args = new Bundle();
////        args.putString("userId", userId);  // Pass userId to fragment
////        fragment.setArguments(args);
////        return fragment;
////    }
////
////    @Override
////    public void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        if (getArguments() != null) {
////            userId = getArguments().getString("userId");  // Get userId passed to the fragment
////        }
////    }
////
////    @Override
////    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
////        return inflater.inflate(R.layout.fragment_user_profile, container, false);  // Inflate user profile layout
////    }
////
////    @Override
////    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
////        super.onViewCreated(view, savedInstanceState);
////
////        profileImage = view.findViewById(R.id.profile_picture);
////        profileName = view.findViewById(R.id.profile_name);
////        profileEmail = view.findViewById(R.id.profile_email);
////        photosCount = view.findViewById(R.id.photos_count);
////        followersCount = view.findViewById(R.id.followers_count);
////        followsCount = view.findViewById(R.id.follows_count);
////        postsRecyclerView = view.findViewById(R.id.images_grid);
////        back_button = view.findViewById(R.id.back_button);
////
////
////        postsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
////        postList = new ArrayList<>();
////        postAdapter = new ImageAdapter_profile(getContext(), postList, false);
////        postsRecyclerView.setAdapter(postAdapter);
////
////        mAuth = FirebaseAuth.getInstance();
////        mDatabase = FirebaseDatabase.getInstance().getReference();
////
////        // Load the user's profile and data based on the passed userId
////        if (userId != null) {
////            loadUserProfile(userId);
////            loadUserPosts(userId);
////            loadPhotosCount(userId);
////            loadFollowersCount(userId);
////            loadFollowsCount(userId);
////        } else {
////            Log.w(TAG, "No userId passed to UserProfileFragment.");
////        }
////
//////        back_button.setOnClickListener(v -> {
//////            getParentFragmentManager().beginTransaction()
//////                    .replace(R.id.home_content, new Fragment())
//////                    .addToBackStack(null)
//////                    .commit();
//////        });
////
////        back_button.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
////
////    }
////
////    private void loadUserProfile(String userId) {
////        Log.d(TAG, "loadUserProfile: Fetching user profile data for userId: " + userId);
////        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                if (dataSnapshot.exists()) {
////                    User user = dataSnapshot.getValue(User.class);
////                    if (user != null) {
////                        profileName.setText(user.getUsername() != null ? user.getUsername() : "Username");
////                        profileEmail.setText(user.getEmail() != null ? user.getEmail() : "Email");
////
////                        // Load profile image (if exists)
////                        Glide.with(UserProfileFragment.this)
////                                .load(user.getProfileImageUrl())
////                                .circleCrop()
////                                .placeholder(R.drawable.ic_profile_placeholder)
////                                .into(profileImage);
////                    }
////                } else {
////                    showToast("User data not found");
////                }
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError) {
////                Log.e(TAG, "Failed to fetch user data: " + databaseError.getMessage());
////                showToast("Failed to fetch user data");
////            }
////        });
////    }
////
////    private void loadPhotosCount(String userId) {
////        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
////
////        postsRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                int count = 0;
////                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
////                    count++;
////                }
////                photosCount.setText(String.valueOf(count));
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////                Log.e(TAG, "Failed to load photos count: " + error.getMessage());
////            }
////        });
////    }
////
////    private void loadFollowersCount(String userId) {
////        mDatabase.child("followers").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                int count = (int) snapshot.getChildrenCount();
////                followersCount.setText(String.valueOf(count));
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////                Log.e(TAG, "Failed to fetch followers count: " + error.getMessage());
////            }
////        });
////    }
////
////    private void loadFollowsCount(String userId) {
////        mDatabase.child("following").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                int count = (int) snapshot.getChildrenCount();
////                followsCount.setText(String.valueOf(count));
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////                Log.e(TAG, "Failed to fetch follows count: " + error.getMessage());
////            }
////        });
////    }
////
////    private void loadUserPosts(String userId) {
////        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
////
////        postsRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                postList.clear();
////                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
////                    Post post = postSnapshot.getValue(Post.class);
////                    if (post != null) {
////                        postList.add(post);
////                    }
////                }
////                postAdapter.notifyDataSetChanged();
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////                Toast.makeText(getContext(), "Error loading posts: " + error.getMessage(), Toast.LENGTH_SHORT).show();
////            }
////        });
////    }
////
////    private void showToast(String message) {
////        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
////    }
////}
////
////
//// File: UserProfileFragment.java
//// File: UserProfileFragment.java
//package com.example.betre;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.betre.models.Post;
//import com.example.betre.models.User;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class UserProfileFragment extends Fragment {
//    private static final String TAG = "UserProfileFragment";
//
//    private static final String ARG_USER_ID = "user_id";
//
//    private String userId;
//
//    private ImageView profileImage;
//    private TextView profileName, profileEmail, photosCount, followersCount, followsCount;
//    private ProgressBar progressBar;
//    private RecyclerView postsRecyclerView;
//    private PostAdapter postAdapter;
//    private List<Post> postList;
//
//    public UserProfileFragment() {
//        // Required empty public constructor
//    }
//
//    // Factory method to create a new instance with userId
//    public static UserProfileFragment newInstance(String userId) {
//        UserProfileFragment fragment = new UserProfileFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_USER_ID, userId);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null){
//            userId = getArguments().getString(ARG_USER_ID);
//        }
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState){
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_user_profile, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
//        super.onViewCreated(view, savedInstanceState);
//
//        // Initialize UI components
//        profileImage = view.findViewById(R.id.user_profile_picture);
//        profileName = view.findViewById(R.id.user_profile_name);
//        profileEmail = view.findViewById(R.id.user_profile_email);
//        photosCount = view.findViewById(R.id.photos_count);
//        followersCount = view.findViewById(R.id.followers_count);
//        followsCount = view.findViewById(R.id.follows_count);
//        progressBar = view.findViewById(R.id.user_profile_progress_bar);
//        postsRecyclerView = view.findViewById(R.id.user_posts_recycler_view);
//
//        // Set up RecyclerView
//        postsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
//        postList = new ArrayList<>();
//        postAdapter = new PostAdapter(postList);
//        postsRecyclerView.setAdapter(postAdapter);
//
//        // Fetch and display user profile data
//        if (userId != null && !userId.isEmpty()){
//            fetchUserProfile(userId);
//            fetchUserPosts(userId);
//            fetchPhotosCount(userId);
//            fetchFollowersCount(userId);
//            fetchFollowsCount(userId);
//        }
//        else{
//            Log.e(TAG, "onViewCreated: userId is null or empty.");
//            Toast.makeText(getContext(), "Invalid user.", Toast.LENGTH_SHORT).show();
//            // Optionally, navigate back or show an error message
//        }
//    }
//
//    private void fetchUserProfile(String userId){
//        progressBar.setVisibility(View.VISIBLE);
//        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
//        userRef.addListenerForSingleValueEvent(new ValueEventListener(){
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
//                progressBar.setVisibility(View.GONE);
//                if (dataSnapshot.exists()){
//                    User user = dataSnapshot.getValue(User.class);
//                    if (user != null){
//                        profileName.setText(user.getUsername() != null ? user.getUsername() : "Username");
//                        profileEmail.setText(user.getEmail() != null ? user.getEmail() : "Email");
//
//                        Glide.with(UserProfileFragment.this)
//                                .load(user.getProfileImageUrl())
//                                .circleCrop()
//                                .placeholder(R.drawable.ic_profile_placeholder)
//                                .into(profileImage);
//                    }
//                }
//                else{
//                    Log.w(TAG, "fetchUserProfile: User data not found for userId: " + userId);
//                    Toast.makeText(getContext(), "User not found.", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError){
//                progressBar.setVisibility(View.GONE);
//                Log.e(TAG, "fetchUserProfile: Database error: " + databaseError.getMessage());
//                Toast.makeText(getContext(), "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void fetchUserPosts(String userId){
//        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
//        postsRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener(){
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot){
//                postList.clear();
//                for (DataSnapshot postSnapshot : snapshot.getChildren()){
//                    Post post = postSnapshot.getValue(Post.class);
//                    if (post != null){
//                        post.setPostId(postSnapshot.getKey());
//                        postList.add(post);
//                    }
//                }
//                postAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error){
//                Log.e(TAG, "fetchUserPosts: Database error: " + error.getMessage());
//                Toast.makeText(getContext(), "Failed to load posts.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void fetchPhotosCount(String userId){
//        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
//        postsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener(){
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot){
//                int count = (int) snapshot.getChildrenCount();
//                photosCount.setText(String.valueOf(count));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error){
//                Log.e(TAG, "fetchPhotosCount: Database error: " + error.getMessage());
//                // Optionally, show a message to the user
//            }
//        });
//    }
//
//    private void fetchFollowersCount(String userId){
//        DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference("followers").child(userId);
//        followersRef.addListenerForSingleValueEvent(new ValueEventListener(){
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot){
//                int count = (int) snapshot.getChildrenCount();
//                followersCount.setText(String.valueOf(count));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error){
//                Log.e(TAG, "fetchFollowersCount: Database error: " + error.getMessage());
//                // Optionally, show a message to the user
//            }
//        });
//    }
//
//    private void fetchFollowsCount(String userId){
//        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("following").child(userId);
//        followingRef.addListenerForSingleValueEvent(new ValueEventListener(){
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot){
//                int count = (int) snapshot.getChildrenCount();
//                followsCount.setText(String.valueOf(count));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error){
//                Log.e(TAG, "fetchFollowsCount: Database error: " + error.getMessage());
//                // Optionally, show a message to the user
//            }
//        });
//    }
//
//    // RecyclerView Adapter for displaying posts
//    public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
//        private List<Post> posts;
//        private static final long DOUBLE_CLICK_TIME_DELTA = 300; // Milliseconds
//        private long lastClickTime = 0;
//
//        public PostAdapter(List<Post> posts){
//            this.posts = posts;
//        }
//
//        @NonNull
//        @Override
//        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
//            return new PostViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(PostViewHolder holder, int position){
//            Post post = posts.get(position);
//            Glide.with(getContext())
//                    .load(post.getImageUrl())
//                    .into(holder.postImage);
//
//            // Handle double click to view the post in detail
//            holder.itemView.setOnClickListener(v -> {
//                long clickTime = System.currentTimeMillis();
//                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
//                    // Double-click detected, open DisplayPost fragment and pass post's postId
//                    openDisplayPostFragment(post.getPostId());
//                }
//                lastClickTime = clickTime;
//            });
//        }
//
//        @Override
//        public int getItemCount(){
//            return posts.size();
//        }
//
//        public class PostViewHolder extends RecyclerView.ViewHolder{
//            public ImageView postImage;
//
//            public PostViewHolder(View itemView){
//                super(itemView);
//                postImage = itemView.findViewById(R.id.image_item);
//            }
//        }
//    }
//
//    private void openDisplayPostFragment(String postId){
//        if (postId != null && !postId.isEmpty()){
//            DisplayPost displayPostFragment = DisplayPost.newInstance(postId);
//            ((AppCompatActivity) getContext()).getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.home_content, displayPostFragment)
//                    .addToBackStack(null)
//                    .commit();
//        }
//        else{
//            Log.e(TAG, "openDisplayPostFragment: postId is null or empty.");
//            Toast.makeText(getContext(), "Post not found.", Toast.LENGTH_SHORT).show();
//        }
//    }
//}


// File: UserProfileFragment.java
//package com.example.betre;
//
//import android.os.Bundle;
//import android.text.InputType;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button; // Added import
//import android.widget.EditText; // Added import
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog; // Added import
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.betre.models.Post;
//import com.example.betre.models.Report; // Added import
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
//public class UserProfileFragment extends Fragment {
//    private static final String TAG = "UserProfileFragment";
//
//    private static final String ARG_USER_ID = "user_id";
//
//    private String userId;
//
//    private ImageView profileImage;
//    private TextView profileName, profileEmail, photosCount, followersCount, followsCount;
//    private ProgressBar progressBar;
//    private RecyclerView postsRecyclerView;
//    private PostAdapter postAdapter;
//    private List<Post> postList;
//
//    // Declare the Report button
//    private Button reportUserButton;
//
//    public UserProfileFragment() {
//        // Required empty public constructor
//    }
//
//    // Factory method to create a new instance with userId
//    public static UserProfileFragment newInstance(String userId) {
//        UserProfileFragment fragment = new UserProfileFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_USER_ID, userId);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null){
//            userId = getArguments().getString(ARG_USER_ID);
//        }
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState){
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_user_profile, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
//        super.onViewCreated(view, savedInstanceState);
//
//        // Initialize UI components
//        profileImage = view.findViewById(R.id.user_profile_picture);
//        profileName = view.findViewById(R.id.user_profile_name);
//        profileEmail = view.findViewById(R.id.user_profile_email);
//        photosCount = view.findViewById(R.id.photos_count);
//        followersCount = view.findViewById(R.id.followers_count);
//        followsCount = view.findViewById(R.id.follows_count);
//        progressBar = view.findViewById(R.id.user_profile_progress_bar);
//        postsRecyclerView = view.findViewById(R.id.user_posts_recycler_view);
//        reportUserButton = view.findViewById(R.id.report_user_button); // Initialize Report button
//
//        // Set up RecyclerView
//        postsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
//        postList = new ArrayList<>();
//        postAdapter = new PostAdapter(postList);
//        postsRecyclerView.setAdapter(postAdapter);
//
//        // Fetch and display user profile data
//        if (userId != null && !userId.isEmpty()){
//            fetchUserProfile(userId);
//            fetchUserPosts(userId);
//            fetchPhotosCount(userId);
//            fetchFollowersCount(userId);
//            fetchFollowsCount(userId);
//        }
//        else{
//            Log.e(TAG, "onViewCreated: userId is null or empty.");
//            Toast.makeText(getContext(), "Invalid user.", Toast.LENGTH_SHORT).show();
//            // Optionally, navigate back or show an error message
//        }
//
//        // Set up Report button click listener
//        reportUserButton.setOnClickListener(v -> showReportDialog());
//    }
//
//    private void fetchUserProfile(String userId){
//        progressBar.setVisibility(View.VISIBLE);
//        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
//        userRef.addListenerForSingleValueEvent(new ValueEventListener(){
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
//                progressBar.setVisibility(View.GONE);
//                if (dataSnapshot.exists()){
//                    User user = dataSnapshot.getValue(User.class);
//                    if (user != null){
//                        profileName.setText(user.getUsername() != null ? user.getUsername() : "Username");
//                        profileEmail.setText(user.getEmail() != null ? user.getEmail() : "Email");
//
//                        Glide.with(UserProfileFragment.this)
//                                .load(user.getProfileImageUrl())
//                                .circleCrop()
//                                .placeholder(R.drawable.ic_profile_placeholder)
//                                .into(profileImage);
//                    }
//                }
//                else{
//                    Log.w(TAG, "fetchUserProfile: User data not found for userId: " + userId);
//                    Toast.makeText(getContext(), "User not found.", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError){
//                progressBar.setVisibility(View.GONE);
//                Log.e(TAG, "fetchUserProfile: Database error: " + databaseError.getMessage());
//                Toast.makeText(getContext(), "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void fetchUserPosts(String userId){
//        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
//        postsRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener(){
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot){
//                postList.clear();
//                for (DataSnapshot postSnapshot : snapshot.getChildren()){
//                    Post post = postSnapshot.getValue(Post.class);
//                    if (post != null){
//                        post.setPostId(postSnapshot.getKey());
//                        postList.add(post);
//                    }
//                }
//                postAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error){
//                Log.e(TAG, "fetchUserPosts: Database error: " + error.getMessage());
//                Toast.makeText(getContext(), "Failed to load posts.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void fetchPhotosCount(String userId){
//        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
//        postsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener(){
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot){
//                int count = (int) snapshot.getChildrenCount();
//                photosCount.setText(String.valueOf(count));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error){
//                Log.e(TAG, "fetchPhotosCount: Database error: " + error.getMessage());
//                // Optionally, show a message to the user
//            }
//        });
//    }
//
//    private void fetchFollowersCount(String userId){
//        DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference("followers").child(userId);
//        followersRef.addListenerForSingleValueEvent(new ValueEventListener(){
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot){
//                int count = (int) snapshot.getChildrenCount();
//                followersCount.setText(String.valueOf(count));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error){
//                Log.e(TAG, "fetchFollowersCount: Database error: " + error.getMessage());
//                // Optionally, show a message to the user
//            }
//        });
//    }
//
//    private void fetchFollowsCount(String userId){
//        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("following").child(userId);
//        followingRef.addListenerForSingleValueEvent(new ValueEventListener(){
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot){
//                int count = (int) snapshot.getChildrenCount();
//                followsCount.setText(String.valueOf(count));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error){
//                Log.e(TAG, "fetchFollowsCount: Database error: " + error.getMessage());
//                // Optionally, show a message to the user
//            }
//        });
//    }
//
//    // Method to show the report dialog
//    private void showReportDialog() {
//        // Create an AlertDialog builder
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("Report User");
//
//        // Set up the input (a simple EditText for the reason)
//        final EditText input = new EditText(getContext());
//        input.setHint("Enter reason for reporting");
//        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
//        builder.setView(input);
//
//        // Set up the buttons
//        builder.setPositiveButton("Report", (dialog, which) -> {
//            String reason = input.getText().toString().trim();
//            if (!reason.isEmpty()) {
//                submitReport(reason);
//            } else {
//                Toast.makeText(getContext(), "Please enter a reason.", Toast.LENGTH_SHORT).show();
//            }
//        });
//        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
//
//        // Show the dialog
//        builder.show();
//    }
//
//    // Method to submit the report to Firebase
//    private void submitReport(String reason) {
//        // Get the current user's ID
//        String reportingUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        // Prevent users from reporting themselves
//        if (reportingUserId.equals(userId)) {
//            Toast.makeText(getContext(), "You cannot report your own profile.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Create a new Report object
//        Report report = new Report();
//        report.setReportedUserId(userId); // The user being reported
//        report.setReportingUserId(reportingUserId); // The user who is reporting
//        report.setReason(reason);
//        report.setTimestamp(System.currentTimeMillis());
//
//        // Get a unique key for the report
//        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("reported_profiles");
//        String reportId = reportsRef.push().getKey();
//
//        if (reportId != null) {
//            // Write the report to Firebase
//            reportsRef.child(reportId).setValue(report)
//                    .addOnSuccessListener(aVoid -> {
//                        Toast.makeText(getContext(), "Report submitted successfully.", Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, "Report submitted: " + reportId);
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(getContext(), "Failed to submit report. Please try again.", Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "Failed to submit report", e);
//                    });
//        } else {
//            Toast.makeText(getContext(), "Failed to generate report ID.", Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "submitReport: Report ID is null");
//        }
//    }
//
//    // RecyclerView Adapter for displaying posts
//    public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
//        private List<Post> posts;
//        private static final long DOUBLE_CLICK_TIME_DELTA = 300; // Milliseconds
//        private long lastClickTime = 0;
//
//        public PostAdapter(List<Post> posts){
//            this.posts = posts;
//        }
//
//        @NonNull
//        @Override
//        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
//            return new PostViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(PostViewHolder holder, int position){
//            Post post = posts.get(position);
//            Glide.with(getContext())
//                    .load(post.getImageUrl())
//                    .into(holder.postImage);
//
//            // Handle double click to view the post in detail
//            holder.itemView.setOnClickListener(v -> {
//                long clickTime = System.currentTimeMillis();
//                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
//                    // Double-click detected, open DisplayPost fragment and pass post's postId
//                    openDisplayPostFragment(post.getPostId());
//                }
//                lastClickTime = clickTime;
//            });
//        }
//
//        @Override
//        public int getItemCount(){
//            return posts.size();
//        }
//
//        public class PostViewHolder extends RecyclerView.ViewHolder{
//            public ImageView postImage;
//
//            public PostViewHolder(View itemView){
//                super(itemView);
//                postImage = itemView.findViewById(R.id.image_item);
//            }
//        }
//    }
//
//    private void openDisplayPostFragment(String postId){
//        if (postId != null && !postId.isEmpty()){
//            DisplayPost displayPostFragment = DisplayPost.newInstance(postId);
//            ((AppCompatActivity) getContext()).getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.home_content, displayPostFragment)
//                    .addToBackStack(null)
//                    .commit();
//        }
//        else{
//            Log.e(TAG, "openDisplayPostFragment: postId is null or empty.");
//            Toast.makeText(getContext(), "Post not found.", Toast.LENGTH_SHORT).show();
//        }
//    }
//}


// File: UserProfileFragment.java
package com.example.betre;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Added import
import android.widget.EditText; // Added import
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog; // Added import
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.betre.models.Post;
import com.example.betre.models.Report; // Added import
import com.example.betre.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserProfileFragment extends Fragment {
    private static final String TAG = "UserProfileFragment";

    private static final String ARG_USER_ID = "user_id";

    private String userId;

    private ImageView profileImage;
    private TextView profileName, profileEmail, photosCount, followersCount, followsCount;
    private ProgressBar progressBar;
    private RecyclerView postsRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    // Declare the Report button
    private Button reportUserButton;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    // Factory method to create a new instance with userId
    public static UserProfileFragment newInstance(String userId) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            userId = getArguments().getString(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        profileImage = view.findViewById(R.id.user_profile_picture);
        profileName = view.findViewById(R.id.user_profile_name);
        profileEmail = view.findViewById(R.id.user_profile_email);
        photosCount = view.findViewById(R.id.photos_count);
        followersCount = view.findViewById(R.id.followers_count);
        followsCount = view.findViewById(R.id.follows_count);
        progressBar = view.findViewById(R.id.user_profile_progress_bar);
        postsRecyclerView = view.findViewById(R.id.user_posts_recycler_view);
        reportUserButton = view.findViewById(R.id.report_user_button); // Initialize Report button

        // Set up RecyclerView
        postsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        postsRecyclerView.setAdapter(postAdapter);

        // Fetch and display user profile data
        if (userId != null && !userId.isEmpty()){
            fetchUserProfile(userId);
            fetchUserPosts(userId);
            fetchPhotosCount(userId);
            fetchFollowersCount(userId);
            fetchFollowsCount(userId);
        }
        else{
            Log.e(TAG, "onViewCreated: userId is null or empty.");
            Toast.makeText(getContext(), "Invalid user.", Toast.LENGTH_SHORT).show();
            // Optionally, navigate back or show an error message
        }

        // Set up Report button click listener
        reportUserButton.setOnClickListener(v -> showReportDialog());
    }

    private void fetchUserProfile(String userId){
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null){
                        profileName.setText(user.getUsername() != null ? user.getUsername() : "Username");
                        profileEmail.setText(user.getEmail() != null ? user.getEmail() : "Email");

                        Glide.with(UserProfileFragment.this)
                                .load(user.getProfileImageUrl())
                                .circleCrop()
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .into(profileImage);
                    }
                }
                else{
                    Log.w(TAG, "fetchUserProfile: User data not found for userId: " + userId);
                    Toast.makeText(getContext(), "User not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "fetchUserProfile: Database error: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserPosts(String userId){
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
        postsRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                postList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null){
                        post.setPostId(postSnapshot.getKey());
                        postList.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.e(TAG, "fetchUserPosts: Database error: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to load posts.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPhotosCount(String userId){
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
        postsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                int count = (int) snapshot.getChildrenCount();
                photosCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.e(TAG, "fetchPhotosCount: Database error: " + error.getMessage());
                // Optionally, show a message to the user
            }
        });
    }

    private void fetchFollowersCount(String userId){
        DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference("followers").child(userId);
        followersRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                int count = (int) snapshot.getChildrenCount();
                followersCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.e(TAG, "fetchFollowersCount: Database error: " + error.getMessage());
                // Optionally, show a message to the user
            }
        });
    }

    private void fetchFollowsCount(String userId){
        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("following").child(userId);
        followingRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                int count = (int) snapshot.getChildrenCount();
                followsCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.e(TAG, "fetchFollowsCount: Database error: " + error.getMessage());
                // Optionally, show a message to the user
            }
        });
    }

    // Method to show the report dialog
    private void showReportDialog() {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Report User");

        // Set up the input (a simple EditText for the reason)
        final EditText input = new EditText(getContext());
        input.setHint("Enter reason for reporting");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Report", (dialog, which) -> {
            String reason = input.getText().toString().trim();
            if (!reason.isEmpty()) {
                submitReport(reason);
            } else {
                Toast.makeText(getContext(), "Please enter a reason.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show the dialog
        builder.show();
    }

    // Method to submit the report to Firebase
    private void submitReport(String reason) {
        // Get the current user's ID
        String reportingUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Prevent users from reporting themselves
        if (reportingUserId.equals(userId)) {
            Toast.makeText(getContext(), "You cannot report your own profile.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch reporting user's username
        fetchUsername(reportingUserId, reportingUsername -> {
            // Fetch reported user's username
            fetchUsername(userId, reportedUsername -> {
                // Now, create the Report object with usernames
                Report report = new Report();
                report.setReportedUserId(userId);
                report.setReportingUserId(reportingUserId);
                report.setReason(reason);
                report.setTimestamp(System.currentTimeMillis());
                report.setReportedUsername(reportedUsername);
                report.setReportingUsername(reportingUsername);

                // Get a unique key for the report
                DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("reported_profiles");
                String reportId = reportsRef.push().getKey();

                if (reportId != null) {
                    // Write the report to Firebase
                    reportsRef.child(reportId).setValue(report)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Report submitted successfully.", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Report submitted: " + reportId);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to submit report. Please try again.", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Failed to submit report", e);
                            });
                } else {
                    Toast.makeText(getContext(), "Failed to generate report ID.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "submitReport: Report ID is null");
                }
            });
        });
    }

    // Interface for callback after fetching username
    private interface OnUsernameFetchedListener {
        void onUsernameFetched(String username);
    }

    // Method to fetch username based on userId
    private void fetchUsername(String userId, OnUsernameFetchedListener listener) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = "Unknown";
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getUsername() != null) {
                        username = user.getUsername();
                    }
                }
                listener.onUsernameFetched(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "fetchUsername: Failed to fetch username for userId: " + userId);
                listener.onUsernameFetched("Unknown");
            }
        });
    }

    // RecyclerView Adapter for displaying posts
    public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
        private List<Post> posts;
        private static final long DOUBLE_CLICK_TIME_DELTA = 300; // Milliseconds
        private long lastClickTime = 0;

        public PostAdapter(List<Post> posts){
            this.posts = posts;
        }

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
            return new PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PostViewHolder holder, int position){
            Post post = posts.get(position);
            Glide.with(getContext())
                    .load(post.getImageUrl())
                    .into(holder.postImage);

            // Handle double click to view the post in detail
            holder.itemView.setOnClickListener(v -> {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
                    // Double-click detected, open DisplayPost fragment and pass post's postId
                    openDisplayPostFragment(post.getPostId());
                }
                lastClickTime = clickTime;
            });
        }

        @Override
        public int getItemCount(){
            return posts.size();
        }

        public class PostViewHolder extends RecyclerView.ViewHolder{
            public ImageView postImage;

            public PostViewHolder(View itemView){
                super(itemView);
                postImage = itemView.findViewById(R.id.image_item);
            }
        }
    }

    private void openDisplayPostFragment(String postId){
        if (postId != null && !postId.isEmpty()){
            DisplayPost displayPostFragment = DisplayPost.newInstance(postId);
            ((AppCompatActivity) getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_content, displayPostFragment)
                    .addToBackStack(null)
                    .commit();
        }
        else{
            Log.e(TAG, "openDisplayPostFragment: postId is null or empty.");
            Toast.makeText(getContext(), "Post not found.", Toast.LENGTH_SHORT).show();
        }
    }
}
