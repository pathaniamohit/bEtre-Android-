package com.example.betre;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.betre.adapters.ImageAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private RecyclerView imagesRecyclerView;
    private ImageAdapter imageAdapter;
    private List<String> imageUrls = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imagesRecyclerView = view.findViewById(R.id.images_grid);
        imagesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        imageAdapter = new ImageAdapter(getContext(), imageUrls);
        imagesRecyclerView.setAdapter(imageAdapter);

        getUserImages();

        return view;
    }

    private void getUserImages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference imagesRef = db.collection("user_images");

        imagesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot documents = task.getResult();
                if (documents != null) {
                    for (DocumentSnapshot document : documents) {
                        String url = document.getString("url");
                        if (url != null) {
                            imageUrls.add(url);
                        }
                    }
                    imageAdapter.notifyDataSetChanged();
                }
            } else {
                Log.e("ProfileFragment", "Error fetching images", task.getException());
            }
        });
    }
}
