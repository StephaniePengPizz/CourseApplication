package hk.hku.cs.myapplication.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.models.forum.ForumItem;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ForumViewHolder> {

    private List<ForumItem> forumList;
    private OnItemClickListener onItemClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnItemClickListener {
        void onItemClick(ForumItem item);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(ForumItem item);
    }

    public ForumAdapter(List<ForumItem> forumList, OnItemClickListener onItemClickListener, OnDeleteClickListener onDeleteClickListener) {
        this.forumList = forumList;
        this.onItemClickListener = onItemClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public ForumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forum, parent, false);
        return new ForumViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ForumViewHolder holder, int position) {
        ForumItem item = forumList.get(position);

        holder.titleTextView.setText(item.getTitle());
        holder.contentTextView.setText(item.getContent());
        holder.senderTextView.setText(item.getCreator() != null ? item.getCreator().getEmail() : "Unknown"); // ✅ 改用email显示
        holder.timeTextView.setText(formatTimestamp(item.getCreatedTime()));

        String currentUsername = hk.hku.cs.myapplication.MyApplication.getUsername();
        String creatorUsername = item.getCreator() != null ? item.getCreator().getEmail() : ""; // ✅ 改用getEmail()

        Log.d("AdapterDebug", "ForumAdapter currentUsername=" + currentUsername + ", creatorUsername=" + creatorUsername);

        if (currentUsername != null && creatorUsername != null && currentUsername.trim().equals(creatorUsername.trim())) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(item);
                }
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item);
            }
        });
    }


    @Override
    public int getItemCount() {
        return forumList.size();
    }

    public static class ForumViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView, senderTextView, timeTextView;
        Button deleteButton;

        public ForumViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    private String formatTimestamp(String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(timeStr);
            if (date != null) {
                return new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
