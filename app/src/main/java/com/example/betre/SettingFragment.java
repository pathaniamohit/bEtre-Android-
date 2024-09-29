package com.example.betre;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    ImageView back_button;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    LinearLayout edit_profile_layout,privacy_layout,about_layout,logout_layout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        back_button = view.findViewById(R.id.back_button);
        edit_profile_layout = view.findViewById(R.id.edit_profile_layout);
        privacy_layout = view.findViewById(R.id.privacy_layout);
        about_layout = view.findViewById(R.id.about_layout);
        logout_layout = view.findViewById(R.id.logout_layout);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_pictures");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        edit_profile_layout.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.home_content, new Edit_Profile_Fragment())
                    .addToBackStack(null)
                    .commit();
        });

        back_button.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.home_content, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        about_layout.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.home_content, new AboutFragment())
                    .addToBackStack(null)
                    .commit();
        });

        privacy_layout.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.home_content, new PrivacyFragment())
                    .addToBackStack(null)
                    .commit();
        });

        logout_layout.setOnClickListener(v -> {
            // Create an AlertDialog to confirm logout
            new AlertDialog.Builder(getActivity())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAuth.signOut();

                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return view;
    }

}