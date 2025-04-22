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

public class MyCourseAdapter extends RecyclerView.Adapter<MyCourseAdapter.ViewHolder> {

    private List<Course> myCourses;
    private OnRemoveCourseClickListener removeCourseClickListener;

    public MyCourseAdapter(List<Course> myCourses) {
        this.myCourses = myCourses;
    }

    public interface OnRemoveCourseClickListener {
        void onRemoveCourse(Course course);
    }

    public void setOnRemoveCourseClickListener(OnRemoveCourseClickListener listener) {
        this.removeCourseClickListener = listener;
    }

    public void updateCourses(List<Course> newCourses) {
        this.myCourses = newCourses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = myCourses.get(position);
        holder.courseNameTextView.setText(course.getCourseName());
        holder.courseTimeTextView.setText(course.getPrimaryScheduleTime());
        holder.courseLocationTextView.setText(course.getPrimaryLocation());

        holder.forumButton.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ForumActivity.class);
            intent.putExtra("courseId", course.getId());
            intent.putExtra("courseName", course.getCourseName());
            context.startActivity(intent);
        });

        holder.removeButton.setOnClickListener(v -> {
            if (removeCourseClickListener != null) {
                removeCourseClickListener.onRemoveCourse(course);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myCourses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseNameTextView, courseTimeTextView, courseLocationTextView;
        Button forumButton, removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseNameTextView = itemView.findViewById(R.id.courseNameTextView);
            courseTimeTextView = itemView.findViewById(R.id.courseTimeTextView);
            courseLocationTextView = itemView.findViewById(R.id.courseLocationTextView);
            forumButton = itemView.findViewById(R.id.forumButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}