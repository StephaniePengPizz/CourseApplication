package hk.hku.cs.myapplication.adapters;

import android.content.Intent;
import android.net.Uri;
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
import hk.hku.cs.myapplication.models.course.Course;
import hk.hku.cs.myapplication.models.user.User;

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

        List<String> commonEnrolled = getCommonCourses(
                currentUser.getEnrolledCourses(),
                matchedUser.getEnrolledCourses()
        );

        if (!commonEnrolled.isEmpty()) {
            holder.commonEnrolledLabel.setVisibility(View.VISIBLE);
            holder.commonEnrolledCourses.setText(formatCourseList(commonEnrolled));
        } else {
            holder.commonEnrolledLabel.setVisibility(View.GONE);
            holder.commonEnrolledCourses.setText("");
        }

        // Handle favorite courses
        List<String> commonFavorites = getCommonCourses(
                currentUser.getFavoriteCourses(),
                matchedUser.getFavoriteCourses()
        );

        if (!commonFavorites.isEmpty()) {
            holder.commonFavoritesLabel.setVisibility(View.VISIBLE);
            holder.commonFavoritesCourses.setText(formatCourseList(commonFavorites));
        } else {
            holder.commonFavoritesLabel.setVisibility(View.GONE);
            holder.commonFavoritesCourses.setText("");
        }


        holder.contactButton.setOnClickListener(v -> {
            if (matchedUser.getEmail() != null) {
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
    private List<String> getCommonCourses(List<Course> user1Courses, List<Course> user2Courses) {
        List<String> common = new ArrayList<>();
        if (user1Courses == null || user2Courses == null) return common;

        for (Course course : user1Courses) {
            if (course != null && user2Courses.contains(course)) {
                String name = course.getCourseName();
                if (name != null && !name.isEmpty()) {
                    common.add(name);
                }
            }
        }
        return common;
    }

    private String formatCourseList(List<String> courses) {
        if (courses.isEmpty()) return "";

        // Create bullet points for each course
        StringBuilder sb = new StringBuilder();
        for (String course : courses) {
            sb.append("• ").append(course).append("\n");
        }
        // Remove last newline
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    public int getItemCount() {
        return matchedUsers.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView matchScoreTextView;
        TextView commonEnrolledLabel;
        TextView commonEnrolledCourses;
        TextView commonFavoritesLabel;
        TextView commonFavoritesCourses;
        Button contactButton;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            matchScoreTextView = itemView.findViewById(R.id.matchScoreTextView);
            commonEnrolledLabel = itemView.findViewById(R.id.commonEnrolledLabel);
            commonEnrolledCourses = itemView.findViewById(R.id.commonEnrolledCourses);
            commonFavoritesLabel = itemView.findViewById(R.id.commonFavoritesLabel);
            commonFavoritesCourses = itemView.findViewById(R.id.commonFavoritesCourses);
            contactButton = itemView.findViewById(R.id.contactButton);
        }
    }
}