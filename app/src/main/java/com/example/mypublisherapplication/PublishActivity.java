package com.example.mypublisherapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PublishActivity extends AppCompatActivity {
    private static final String TAG ="PublishActivity" ;
    BottomNavigationView navigationView;
    NewTextItemFragment textFragment;
    NewImageItemFragment imageFragment;
    FrameLayout frmContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        navigationView = findViewById(R.id.btmNavigationBar);
        frmContent = findViewById(R.id.frmContent);

        textFragment = new NewTextItemFragment();
        imageFragment = new NewImageItemFragment();

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int selectdId = item.getItemId();

                if (selectdId == R.id.mnuPublishText) {
                    showNewTextPostFragment();
                } else if (selectdId == R.id.mnuPublishImage) {
                    showNewImagePostFragment();
                } else if (selectdId == R.id.mnuClose) {
                    finish();
                }
                return true;
            }
        });

        navigationView.setSelectedItemId(R.id.mnuPublishText);
    }

    private void showNewTextPostFragment() {
        // show fragment in container
        // Create the transaction
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        // Replace the content of the container
        fts.replace(R.id.frmContent, textFragment);
        // Append this transaction to the backstack
        //fts.addToBackStack("optional tag");
        // Commit the changes
        fts.commit();
    }

    private void showNewImagePostFragment() {
        // show fragment in container
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        fts.replace(R.id.frmContent, imageFragment);
        fts.commit();
    }


    //TODO - update after saving data to firbase
    public void saveAndClose(ItemData item) {
        /*MainActivity.dataList.add(item);
        if (MainActivity.adapter != null) {
            MainActivity.adapter.notifyDataSetChanged();
            finish();
        }
         */
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // db.collection("publisher-items").add(item)
        db.collection("publisher-items").document(item.getItemId()).set(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    finish();
                } else {
                    Toast.makeText(PublishActivity.this, "Error publishing item", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, task.getException().getLocalizedMessage());
                }
            }
        });

    }
}