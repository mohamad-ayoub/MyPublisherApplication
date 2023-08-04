package com.example.mypublisherapplication;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewImageItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewImageItemFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "NewImageItemFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    updateUiAfterImageSelection(uri);
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    TextView tvImageUri;
    Button btnPublish;
    Button btnClear;
    FloatingActionButton fabOpenGallery;
    ImageView newImageView;

    private void updateUiAfterImageSelection(Uri uri) {
        tvImageUri.setText(uri.toString());
        newImageView.setImageURI(uri);
    }

    public NewImageItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewImageItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewImageItemFragment newInstance(String param1, String param2) {
        NewImageItemFragment fragment = new NewImageItemFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_image_item, container, false);

        tvImageUri = view.findViewById(R.id.tvImgUri);
        btnPublish = view.findViewById(R.id.btnPublish);
        btnClear = view.findViewById(R.id.btnClear);
        fabOpenGallery = view.findViewById(R.id.fabOpenFallery);
        newImageView = view.findViewById(R.id.newImageView);

        tvImageUri.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnPublish.setEnabled(s.toString().trim().length() > 0);
            }
        });

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Uri localUri = Uri.parse(tvImageUri.getText().toString().trim());
                final StorageReference ref = FirebaseStorage.getInstance().getReference("publisher-images").child((new Date().getTime()) + "");

                ref.putFile(localUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return task.getResult().getStorage().getDownloadUrl();
                    }
                }).continueWithTask(new Continuation<Uri, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Uri> task) throws Exception {
                        Uri link = task.getResult();
                        ItemData newItem = new ItemData((new Date()).getTime() + "", FirebaseAuth.getInstance().getUid(), null, link.toString());

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        return db.collection("publisher-items").document(newItem.getItemId()).set(newItem);
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            String userId = FirebaseAuth.getInstance().getUid();
                            DocumentReference docRef = db.collection("publisher-active").document(userId);
                            if (docRef == null) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("points", 2);
                                db.collection("publisher-active").document(userId).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            getActivity().finish();
                                        } else {
                                            Toast.makeText(getContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                docRef.update("points", FieldValue.increment(2)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            getActivity().finish();
                                        } else {
                                            Toast.makeText(getContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(getContext(), "Error publishing item", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, task.getException().getLocalizedMessage());
                        }
                    }
                });

            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelection();
            }
        });

        fabOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PickVisualMediaRequest request = new PickVisualMediaRequest.Builder().build();
                pickMedia.launch(request);
            }
        });

        clearSelection();
        return view;
    }

    private void clearSelection() {
        tvImageUri.setText(null);
        newImageView.setImageURI(null);
    }
}