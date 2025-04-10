package hk.hku.cs.myapplication.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.activities.match.MatchResultActivity;
import hk.hku.cs.myapplication.models.User;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {

    private final List<User> users;

    public MatchAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.nameText.setText(user.getName());
        holder.commonText.setText("共同课程: " + user.getCommonCoursesCount());

        holder.contactButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MatchResultActivity.class);
            intent.putExtra("user_name", user.getName());
            intent.putExtra("common_count", user.getCommonCoursesCount());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView commonText;
        Button contactButton;

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            commonText = itemView.findViewById(R.id.commonText);
            contactButton = itemView.findViewById(R.id.contactButton);
        }
    }
}