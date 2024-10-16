package com.example.betre.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.betre.R;
import com.example.betre.models.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;
    private Context context;

    public NotificationAdapter(List<Notification> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);

        if (notification == null || notification.getType() == null || notification.getUserId() == null) {
            holder.notificationTextView.setText("Invalid notification");
            return;
        }

        // Fetch the username based on the userId stored in the notification
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(notification.getUserId());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    if (username != null) {
                        displayNotification(holder, notification, username);
                    } else {
                        holder.notificationTextView.setText("Invalid notification");
                    }
                } else {
                    holder.notificationTextView.setText("Invalid notification");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                holder.notificationTextView.setText("Invalid notification");
            }
        });
    }

    private void displayNotification(NotificationViewHolder holder, Notification notification, String username) {
        String notificationText;

        if (notification.getType().equals("comment")) {
            String content = notification.getContent();
            if (content != null && !content.isEmpty()) {
                notificationText = username + " commented \"" + content + "\"";
                Spannable spannable = new SpannableString(notificationText);

                spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                int contentStart = notificationText.indexOf("\"") + 1;
                int contentEnd = notificationText.lastIndexOf("\"");
                spannable.setSpan(new ForegroundColorSpan(Color.BLUE), contentStart, contentEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.notificationTextView.setText(spannable);
            } else {
                holder.notificationTextView.setText("Invalid comment notification");
            }

        } else if (notification.getType().equals("like")) {
            notificationText = username + " liked your post.";
            Spannable spannable = new SpannableString(notificationText);

            spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.notificationTextView.setText(spannable);

        } else if (notification.getType().equals("follow")) {
            notificationText = username + " started following you.";
            Spannable spannable = new SpannableString(notificationText);

            spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.notificationTextView.setText(spannable);
        } else {
            holder.notificationTextView.setText("Unknown notification type");
        }
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView notificationTextView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationTextView = itemView.findViewById(R.id.notification_text);
        }
    }
}
