package hk.hku.cs.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.activities.forum.ForumActivity;
import hk.hku.cs.myapplication.models.Course;

public class MyCourseAdapter extends RecyclerView.Adapter<MyCourseAdapter.CourseViewHolder> {

    private List<Course> courseList;

    public MyCourseAdapter(List<Course> courseList) {
        this.courseList = courseList != null ? courseList : new ArrayList<>();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 使用自定义布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseNameTextView.setText(course.getCourseName());
        holder.courseTimeTextView.setText(course.getPrimaryScheduleTime());
        holder.courseLocationTextView.setText(course.getPrimaryLocation());

        holder.forumButton.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ForumActivity.class);
            intent.putExtra("courseName", course.getCourseName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseNameTextView;
        TextView courseTimeTextView;
        TextView courseLocationTextView;
        Button forumButton;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseNameTextView = itemView.findViewById(R.id.courseNameTextView);
            courseTimeTextView = itemView.findViewById(R.id.courseTimeTextView);
            courseLocationTextView = itemView.findViewById(R.id.courseLocationTextView);
            forumButton = itemView.findViewById(R.id.forumButton);
        }
    }
    public void updateCourses(List<Course> newCourses) {
        this.courseList = newCourses;
        notifyDataSetChanged();
    }
}