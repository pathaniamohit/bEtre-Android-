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
    private static final String TAG = "NotificationAdapter";

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

        if (notification == null) {
            Log.e(TAG, "onBindViewHolder: Notification is null at position " + position);
            holder.notificationTextView.setText("Invalid notification");
            return;
        }

        // Load profile image if available
        Glide.with(context)
                .load(notification.getProfileImageUrl())
                .circleCrop()
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(holder.userProfileImage);

        // Display formatted notification text with colors for specific content
        holder.notificationTextView.setText(createSpannableText(notification));

        // Format timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a, MMM d", Locale.getDefault());
        String formattedDate = sdf.format(notification.getTimestamp());
        holder.timestampTextView.setText("");
    }

    private Spannable createSpannableText(Notification notification) {
        String text = notification.getDescription();
        Spannable spannable = new SpannableString(text);

        // Color styling based on notification type
        if ("comment".equals(notification.getType())) {
            int start = text.indexOf(notification.getContent());
            int end = start + notification.getContent().length();
            spannable.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if ("warning".equals(notification.getType())) {
            int start = text.indexOf(notification.getContent());
            int end = start + notification.getContent().length();
            spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.user_profile_image);
            notificationTextView = itemView.findViewById(R.id.notification_text);
            timestampTextView = itemView.findViewById(R.id.timestamp);
        }
    }
}
