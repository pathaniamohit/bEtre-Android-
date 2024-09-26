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
        if (getArguments() != null) {
            String param1 = getArguments().getString("param1");
            String param2 = getArguments().getString("param2");
        }
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
}
