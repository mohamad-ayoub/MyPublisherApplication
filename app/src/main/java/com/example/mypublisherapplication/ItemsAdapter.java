package com.example.mypublisherapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {
    private static final String TAG = "ItemsAdapter";
    private Context context;
    private ArrayList<ItemData> itemsData;
    private Map<String, Object> userScoresData;

    public ItemsAdapter(Context context, ArrayList<ItemData> itemsData, Map<String, Object> userScoresData) {
        this.context = context;
        this.itemsData = itemsData;
        this.userScoresData = userScoresData;
    }

    @NonNull
    @Override


    public ItemsAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ItemViewHolder holder, int position) {
        final int finalPosition = position;
        ItemData item = itemsData.get(position);
        holder.tvItemMessage.setText(item.getMessage());
        float rating = (float) item.getScore() / item.getUsersCount();
        holder.rbItemRating.setRating(rating);
        holder.tvUsersCount.setText("( " + item.getUsersCount() + " )");

        // TODO - handle chemked for giving score
        if (itemHasUserScore(item.getItemId())) {
            holder.imgHasScore.setImageResource(android.R.drawable.checkbox_on_background);
            holder.imgHasScore.setOnClickListener(null);
        } else {
            holder.imgHasScore.setImageResource(android.R.drawable.checkbox_off_background);
            holder.imgHasScore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowDialog(finalPosition);
                }
            });
        }

        if (item.getImageUrl() == null || item.getImageUrl().length() == 0) {
            holder.imgItemImage.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "onBindViewHolder: " + item.getImageUrl());
            // TODO - show image from database
            holder.imgItemImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(item.getImageUrl()).into(holder.imgItemImage);

        }


    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }


    public void ShowDialog(int position) {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(context);
        final RatingBar rating = new RatingBar(context);
        rating.setMax(5);
        rating.setNumStars(5);
        popDialog.setIcon(android.R.drawable.btn_star_big_on);
        popDialog.setTitle("Add Rating: ");
        popDialog.setView(rating);

        // Button OK
        popDialog.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final ItemData item = itemsData.get(position);
                                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                                final DocumentReference itemRef = db.collection("publisher-items").document(item.getItemId());
                                UserItemScore scoreItem = new UserItemScore(item.getItemId(), rating.getProgress());
                                userScoresData.put(scoreItem.getItemId(), scoreItem);
                                CollectionReference scoresRef = db.collection("publisher-scores");
                                DocumentReference activeRef = db.collection("publisher-active").document(FirebaseAuth.getInstance().getUid());
                                db.runTransaction(new Transaction.Function<Object>() {
                                    @Nullable
                                    @Override
                                    public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                        scoresRef.document(FirebaseAuth.getInstance().getUid()).set(userScoresData);
                                        itemRef.update("usersCount", FieldValue.increment(1), "score", FieldValue.increment(rating.getProgress()));
                                        activeRef.update("points", FieldValue.increment(1));
                                        return null;
                                    }
                                });


                            }

                        })

                // Button Cancel
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        popDialog.create();
        popDialog.show();

    }

    final class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItemImage, imgHasScore;
        RatingBar rbItemRating;
        TextView tvItemMessage;
        TextView tvUsersCount;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItemImage = itemView.findViewById(R.id.imgItemImage);
            imgHasScore = itemView.findViewById(R.id.imgHasScore);
            rbItemRating = itemView.findViewById(R.id.rbItemRating);
            tvItemMessage = itemView.findViewById(R.id.tvItemMessage);
            tvUsersCount = itemView.findViewById(R.id.tvUsersCount);
        }
    }

    private boolean itemHasUserScore(String itemId) {
        return userScoresData.containsKey(itemId);
    }
}
