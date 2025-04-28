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
import hk.hku.cs.myapplication.models.forum.ReplyItem;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {

    private List<ReplyItem> replyList;
    private OnDeleteReplyListener onDeleteReplyListener;

    public interface OnDeleteReplyListener {
        void onDeleteReply(ReplyItem item);
    }

    public ReplyAdapter(List<ReplyItem> replyList, OnDeleteReplyListener onDeleteReplyListener) {
        this.replyList = replyList;
        this.onDeleteReplyListener = onDeleteReplyListener;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reply, parent, false);

        return new ReplyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        ReplyItem item = replyList.get(position);

        holder.senderTextView.setText(item.getUser() != null ? item.getUser().getEmail() : "Unknown"); // ✅ 改用email显示
        holder.contentTextView.setText(item.getContent());
        holder.timeTextView.setText(formatTimestamp(item.getCreated_time()));

        String currentUsername = hk.hku.cs.myapplication.MyApplication.getUsername();
        String creatorUsername = item.getUser() != null ? item.getUser().getEmail() : ""; // ✅ 改用getEmail()

        Log.d("AdapterDebug", "ReplyAdapter currentUsername=" + currentUsername + ", creatorUsername=" + creatorUsername);

        if (currentUsername != null && creatorUsername != null && currentUsername.trim().equals(creatorUsername.trim())) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                if (onDeleteReplyListener != null) {
                    onDeleteReplyListener.onDeleteReply(item);
                }
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return replyList.size();
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView senderTextView, contentTextView, timeTextView;
        Button deleteButton;

        public ReplyViewHolder(View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
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