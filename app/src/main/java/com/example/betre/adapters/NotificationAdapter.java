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

        String username = notification.getUsername();
        String notificationText;

        if (notification.getType().equals("comment")) {
            notificationText = username + " commented \"" + notification.getContent() + "\"";
            Spannable spannable = new SpannableString(notificationText);

            // Set the username in red
            spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Set the comment content in blue
            int contentStart = notificationText.indexOf("\"") + 1;
            int contentEnd = notificationText.lastIndexOf("\"");
            spannable.setSpan(new ForegroundColorSpan(Color.BLUE), contentStart, contentEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.notificationTextView.setText(spannable);

        } else if (notification.getType().equals("like")) {
            notificationText = username + " liked your post.";
            Spannable spannable = new SpannableString(notificationText);

            // Set the username in red
            spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.notificationTextView.setText(spannable);

        } else if (notification.getType().equals("follow")) {
            notificationText = username + " started following you.";
            Spannable spannable = new SpannableString(notificationText);

            // Set the username in red
            spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.notificationTextView.setText(spannable);
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView notificationTextView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationTextView = itemView.findViewById(R.id.notification_text);
        }
    }
}
