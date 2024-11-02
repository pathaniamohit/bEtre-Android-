package com.example.betre.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.betre.R;
import com.example.betre.models.Post;
import java.util.List;

public class FlaggedContentAdapter extends RecyclerView.Adapter<FlaggedContentAdapter.FlaggedContentViewHolder> {

    private Context context;
    private List<Post> flaggedPostsList;

    public FlaggedContentAdapter(Context context, List<Post> flaggedPostsList) {
        this.context = context;
        this.flaggedPostsList = flaggedPostsList;
    }

    @NonNull
    @Override
    public FlaggedContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_flagged_content, parent, false);
        return new FlaggedContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlaggedContentViewHolder holder, int position) {
        Post post = flaggedPostsList.get(position);

        holder.flaggedPostContent.setText(post.getContent());
        holder.flaggedPostUser.setText("Posted by: " + post.getUserName());
        holder.flaggedPostReports.setText("Reports: " + post.getCount_comment());
    }

    @Override
    public int getItemCount() {
        return flaggedPostsList.size();
    }

    public static class FlaggedContentViewHolder extends RecyclerView.ViewHolder {
        TextView flaggedPostContent, flaggedPostUser, flaggedPostReports;

        public FlaggedContentViewHolder(@NonNull View itemView) {
            super(itemView);
            flaggedPostContent = itemView.findViewById(R.id.flagged_post_content);
            flaggedPostUser = itemView.findViewById(R.id.flagged_post_user);
            flaggedPostReports = itemView.findViewById(R.id.flagged_post_reports);
        }
    }
}
