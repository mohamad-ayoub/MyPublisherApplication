package com.example.mypublisherapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    RecyclerView rcvItemsList;
    FloatingActionButton fabPublishNewItem;


    RecyclerView.LayoutManager manager;
    static ArrayList<ItemData> dataList = new ArrayList<ItemData>();
    static Map<String, Object> userScores = new HashMap<>();
    static ItemsAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rcvItemsList = findViewById(R.id.rcvItemsList);
        fabPublishNewItem = findViewById(R.id.fabPublishNewItem);

       /*dataList.add(new ItemData("111", "123", null, resourceToUri(R.drawable.nature1).toString()));
        dataList.add(new ItemData("112", "123", null, resourceToUri(R.drawable.nature2).toString()));
        dataList.get(1).setScore(30);
        dataList.get(1).setUsersCount(8);
        dataList.add(new ItemData("113", "123", "simple Message", null));
        dataList.add(new ItemData("114", "123", null, resourceToUri(R.drawable.nature3).toString()));
        */
        adapter = new ItemsAdapter(this, dataList, userScores);
        //manager = new LinearLayoutManager(this);
        manager = new GridLayoutManager(this, 2);

        rcvItemsList.setAdapter(adapter);
        rcvItemsList.setLayoutManager(manager);

        fabPublishNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PublishActivity.class);
                startActivity(intent);
            }
        });
/*
        FirebaseFirestore.getInstance().collection("publisher-scores").document(FirebaseAuth.getInstance().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, error.getLocalizedMessage());
                } else {
                    userScores = value.getData();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        FirebaseFirestore.getInstance().collection("publisher-items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, error.getLocalizedMessage());
                } else {
                    for (DocumentChange document : value.getDocumentChanges()) {
                        ItemData item = document.getDocument().toObject(ItemData.class);
                        switch (document.getType()) {
                            case ADDED:
                                dataList.add(item);
                                break;
                            case MODIFIED:
                                int pos = dataList.indexOf(item);
                                //dataList.get(pos).setScore(item.getScore());
                                //dataList.get(pos).setUsersCount(item.getUsersCount());
                                dataList.set(pos, item);
                                break;
                            case REMOVED:
                                dataList.remove(item);
                                break;
                            default:
                                break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

 */

        CollectionReference colRef = FirebaseFirestore.getInstance().collection("publisher-active");
        colRef.orderBy("points", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentChange documentChange:task.getResult().getDocumentChanges())
                    {
                        long points = (long)documentChange.getDocument().getData().get("points");

                        Toast.makeText(MainActivity.this, "top points is " + points, Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, "user id is " + documentChange.getDocument().getId(), Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

    public Uri resourceToUri(int resdId) {
        Uri path = Uri.parse("android.resource://com.example.mypublisherapplication/" + resdId);
        return path;
    }

}