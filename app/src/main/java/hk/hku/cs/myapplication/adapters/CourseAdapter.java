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
import hk.hku.cs.myapplication.models.course.Course;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private OnAddCourseClickListener addCourseListener;
    private OnFavoriteClickListener favoriteClickListener;
    private OnCourseLongClickListener courseLongClickListener;

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
        boolean isFavorite = course.isFavorite();
        holder.favoriteButton.setText(isFavorite ? "⭐️" : "☆");

        holder.favoriteButton.setOnClickListener(v -> {
            if (course.isFavorite()) {
                course.setFavorite(false);
                if (favoriteClickListener != null) {
                    favoriteClickListener.onRemoveFavorite(course.getId());
                }
            } else {
                course.setFavorite(true);
                if (favoriteClickListener != null) {
                    favoriteClickListener.onAddFavorite(course.getId());
                }
            }
            notifyItemChanged(position);
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (courseLongClickListener != null) {
                courseLongClickListener.onCourseLongClick(course.getId());
                return true; // true means the long click was handled
            }
            return false;
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
    public interface OnCourseLongClickListener {
        void onCourseLongClick(int courseId);
    }
    public void setOnCourseLongClickListener(OnCourseLongClickListener listener) {
        this.courseLongClickListener = listener;
    }
    public interface OnFavoriteClickListener {
        void onAddFavorite(int courseId);
        void onRemoveFavorite(int courseId);
    }
    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteClickListener = listener;
    }
    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseNameTextView;
        TextView courseTimeTextView;
        TextView courseLocationTextView;
        Button addToMyCourseButton;
        Button favoriteButton;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            addToMyCourseButton = itemView.findViewById(R.id.addToMyCourseButton);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            courseNameTextView = itemView.findViewById(R.id.courseNameTextView);
            courseTimeTextView = itemView.findViewById(R.id.courseTimeTextView);
            courseLocationTextView = itemView.findViewById(R.id.courseLocationTextView);

        }
    }
    public void updateCourses(List<Course> newCourses) {
        this.courseList = newCourses;
        notifyDataSetChanged();
    }
    public List<Course> getCourseList() {
        return courseList;
    }
}