package hk.hku.cs.myapplication.adapters;

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

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private OnAddCourseClickListener addCourseListener;

    public CourseAdapter(List<Course> courseList) {
        this.courseList = courseList != null ? courseList : new ArrayList<>();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 使用自定义布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseNameTextView.setText(course.getCourseName());
        holder.courseTimeTextView.setText(course.getPrimaryScheduleTime());
        holder.courseLocationTextView.setText(course.getPrimaryLocation());
        holder.addToMyCourseButton.setOnClickListener(v -> {
            if (addCourseListener != null) {
                addCourseListener.onAddCourseClick(course.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public interface OnAddCourseClickListener {
        void onAddCourseClick(int courseId);
    }

    public void setOnAddCourseClickListener(OnAddCourseClickListener listener) {
        this.addCourseListener = listener;
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseNameTextView;
        TextView courseTimeTextView;
        TextView courseLocationTextView;
        Button addToMyCourseButton;


        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            addToMyCourseButton = itemView.findViewById(R.id.addToMyCourseButton);
            courseNameTextView = itemView.findViewById(R.id.courseNameTextView);
            courseTimeTextView = itemView.findViewById(R.id.courseTimeTextView);
            courseLocationTextView = itemView.findViewById(R.id.courseLocationTextView);

        }
    }
    public void updateCourses(List<Course> newCourses) {
        this.courseList = newCourses;
        notifyDataSetChanged();
    }
}