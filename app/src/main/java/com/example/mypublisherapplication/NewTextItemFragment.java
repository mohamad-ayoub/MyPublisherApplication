package com.example.mypublisherapplication;

import android.os.Bundle;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewTextItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewTextItemFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "NewTextItemFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewTextItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewTextItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewTextItemFragment newInstance(String param1, String param2) {
        NewTextItemFragment fragment = new NewTextItemFragment();
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
        View view = inflater.inflate(R.layout.fragment_new_text_item, container, false);
        EditText etNewText = view.findViewById(R.id.tvImgUri);
        Button btnPublish = view.findViewById(R.id.btnPublish);
        Button btnClear = view.findViewById(R.id.btnClear);

        etNewText.addTextChangedListener(new TextWatcher() {
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
                String msg = etNewText.getText().toString().trim();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                ItemData newItem = new ItemData((new Date()).getTime() + "", userId, msg, null);
                //((PublishActivity) getActivity()).saveAndClose(newItem);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("publisher-items").document(newItem.getItemId()).set(newItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
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
                etNewText.setText(null);
            }
        });

        etNewText.setText("");
        return view;
    }
}