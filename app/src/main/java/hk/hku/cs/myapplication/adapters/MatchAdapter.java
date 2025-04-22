package hk.hku.cs.myapplication.adapters;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.models.Course;
import hk.hku.cs.myapplication.models.User;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private List<User> matchedUsers;
    private User currentUser;

    public MatchAdapter(List<User> matchedUsers) {
        this.matchedUsers = matchedUsers;
    }

    public void updateUsers(List<User> newUsers, User currentUser) {
        this.matchedUsers = newUsers;
        this.currentUser = currentUser;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        User matchedUser = matchedUsers.get(position);
        int matchScore = currentUser.calculateMatchScore(matchedUser);

        holder.nameTextView.setText(matchedUser.getNameinMatch());
        holder.matchScoreTextView.setText("Match Score: " + matchScore);

        StringBuilder courseInfo = new StringBuilder();

        // 共同选修课（权重5分）
        buildCommonCoursesString(courseInfo,
                currentUser.getEnrolledCourses(),
                matchedUser.getEnrolledCourses(),
                true);

        courseInfo.append("\n");

        // 共同收藏课（权重3分）
        buildCommonCoursesString(courseInfo,
                currentUser.getFavoriteCourses(),
                matchedUser.getFavoriteCourses(),
                false);

        holder.commonCoursesTextView.setText(courseInfo.toString());

        holder.contactButton.setOnClickListener(v -> {
            if (matchedUser.getEmail() != null && !matchedUser.getEmail().isEmpty()) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + matchedUser.getEmail()));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hello " + matchedUser.getName());
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + matchedUser.getName() + ",\n\n");

                // Check if an email client is available
                if (emailIntent.resolveActivity(holder.itemView.getContext().getPackageManager()) != null) {
                    holder.itemView.getContext().startActivity(emailIntent);
                } else {
                    Toast.makeText(holder.itemView.getContext(), "No email client found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(holder.itemView.getContext(), "Email not available", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void buildCommonCoursesString(StringBuilder builder,
                                          List<Course> currentUserCourses,
                                          List<Course> matchedUserCourses,
                                          boolean isEnrolled) {
        if (currentUserCourses == null || matchedUserCourses == null) {
            builder.append("None");
            return;
        }
        List<String> commonCourseNames = new ArrayList<>();

        for (Course course : currentUserCourses) {
            if (course != null && matchedUserCourses.contains(course)) {
                String name = course.getCourseName();
                if (name != null && !name.isEmpty()) {
                    commonCourseNames.add(name);
                }
            }
        }
        builder.append(isEnrolled ? "Enrolled: " : "Favorites: ");
        if (commonCourseNames.isEmpty()) {
            builder.append("None");
        } else {
            builder.append("(").append(commonCourseNames.size()).append(") ");
            builder.append(TextUtils.join(", ", commonCourseNames));

            int points = isEnrolled ? commonCourseNames.size() * 5
                    : commonCourseNames.size() * 3;
            builder.append(" [").append(points).append(" pts]");
        }

    }

    @Override
    public int getItemCount() {
        return matchedUsers.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView matchScoreTextView;
        TextView commonCoursesTextView;
        Button contactButton;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            matchScoreTextView = itemView.findViewById(R.id.matchScoreTextView);
            commonCoursesTextView = itemView.findViewById(R.id.commonCoursesTextView);
            contactButton = itemView.findViewById(R.id.contactButton);
        }
    }
}