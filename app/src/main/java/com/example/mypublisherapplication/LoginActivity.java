package com.example.mypublisherapplication;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    Button btnStart;
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnStart=findViewById(R.id.btnStart);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    startLogin();
                } else {
                    openMainActivity();
                }
            }
        });


        FirebaseFirestore.getInstance().collection("publisher-scores").document(FirebaseAuth.getInstance().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                } else {
                    MainActivity.userScores = value.getData();
                    if (MainActivity.adapter != null)
                        MainActivity.adapter.notifyDataSetChanged();

                    FirebaseFirestore.getInstance().collection("publisher-items")
                            //.whereEqualTo("uid",FirebaseAuth.getInstance().getUid())
                            // .whereNotEqualTo("message",null)
                            // .whereGreaterThan("usersCount",4)
                            //.whereLessThan("usersCount", 10)
                            //.orderBy("usersCount", Query.Direction.DESCENDING).limit(1)
                            //.whereNotIn("itemId", Arrays.asList(MainActivity.userScores.keySet().toArray()))
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {

                                    } else {
                                        for (DocumentChange document : value.getDocumentChanges()) {
                                            ItemData item = document.getDocument().toObject(ItemData.class);
                                            switch (document.getType()) {
                                                case ADDED:
                                                    MainActivity.dataList.add(item);
                                                    break;
                                                case MODIFIED:
                                                    int pos = MainActivity.dataList.indexOf(item);
                                                    //dataList.get(pos).setScore(item.getScore());
                                                    //dataList.get(pos).setUsersCount(item.getUsersCount());
                                                    MainActivity.dataList.set(pos, item);
                                                    break;
                                                case REMOVED:
                                                    MainActivity.dataList.remove(item);
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                        if (MainActivity.adapter != null)
                                            MainActivity.adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                }
            }
        });





    }

    private void startLogin() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                //new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build());


        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers) // do not remove this line
                .setLogo(R.drawable.logo_my_publisher)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void openMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Toast.makeText(this, "Wellcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, result.getIdpResponse().getError().getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

}