package com.example.betre.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.betre.R;
import com.example.betre.models.Notification;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;



public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;
    private Context context;
    private DatabaseReference notificationRef;
    private static final String TAG = "NotificationAdapter";

    public NotificationAdapter(List<Notification> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
        notificationRef = FirebaseDatabase.getInstance().getReference("notifications");
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

        if (notification == null) {
            Log.e(TAG, "onBindViewHolder: Notification is null at position " + position);
            holder.notificationTextView.setText("Invalid notification");
            return;
        }

        // Display profile image if available
        Glide.with(context)
                .load(notification.getProfileImageUrl())
                .circleCrop()
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(holder.userProfileImage);

        // Display formatted notification text
        displayNotification(holder, notification);

        // Format timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a, MMM d", Locale.getDefault());
        String formattedDate = sdf.format(notification.getTimestamp());
        holder.timestampTextView.setText(formattedDate);

        // Dismiss report notification
        holder.dismissButton.setVisibility(notification.getType().equals("report") ? View.VISIBLE : View.GONE);
        holder.dismissButton.setOnClickListener(v -> {
            notificationRef.child(notification.getNotificationId()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        notificationList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Report dismissed", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to dismiss report", Toast.LENGTH_SHORT).show());
        });
    }

    private void displayNotification(NotificationViewHolder holder, Notification notification) {
        String username = notification.getUsername();
        String content = notification.getContent();

        // Handle null values
        if (username == null) {
            username = "Unknown User"; // Or any default string
            Log.w(TAG, "displayNotification: Notification with null username detected. Notification ID: " + notification.getNotificationId());
        }

        if (content == null) {
            content = ""; // Or any default handling
            Log.w(TAG, "displayNotification: Notification with null content detected. Notification ID: " + notification.getNotificationId());
        }

        Spannable spannable;

        switch (notification.getType()) {
            case "comment":
                String commentText = username + " commented: " + content;
                spannable = createSpannable(commentText, username, Color.RED);
                holder.notificationTextView.setText(spannable);
                break;
            case "like":
                String likeText = username + " liked your post.";
                spannable = createSpannable(likeText, username, Color.RED);
                holder.notificationTextView.setText(spannable);
                break;
            case "follow":
                String followText = username + " started following you.";
                spannable = createSpannable(followText, username, Color.RED);
                holder.notificationTextView.setText(spannable);
                break;
            case "unfollow":
                String unfollowText = username + " unfollowed you.";
                spannable = createSpannable(unfollowText, username, Color.RED);
                holder.notificationTextView.setText(spannable);
                break;
            case "report":
                String reportText = "Reported for: " + content;
                spannable = createSpannable(reportText, username, Color.RED);
                holder.notificationTextView.setText(spannable);
                break;
            default:
                holder.notificationTextView.setText("Unknown notification type");
                Log.w(TAG, "displayNotification: Unknown notification type: " + notification.getType());
        }
    }

    private Spannable createSpannable(String fullText, String username, int color) {
        if (username == null) {
            username = "Unknown User"; // Default value or handle accordingly
        }

        Spannable spannable = new SpannableString(fullText);
        int usernameEnd = username.length();
        if (usernameEnd > 0 && fullText.length() >= usernameEnd) {
            spannable.setSpan(new ForegroundColorSpan(color), 0, usernameEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            Log.w(TAG, "createSpannable: Invalid username length or fullText length.");
        }
        return spannable;
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImage;
        TextView notificationTextView, timestampTextView;
        TextView dismissButton;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.user_profile_image);
            notificationTextView = itemView.findViewById(R.id.notification_text);
            timestampTextView = itemView.findViewById(R.id.timestamp);
            dismissButton = itemView.findViewById(R.id.dismiss_button);
        }
    }
}