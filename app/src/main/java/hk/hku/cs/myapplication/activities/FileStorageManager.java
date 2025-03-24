package hk.hku.cs.myapplication.activities;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hk.hku.cs.myapplication.models.Course;

public class FileStorageManager {
    private static final String TAG = "FileStorage";
    private static final String FILE_NAME = "courses.json";
    private static final Type COURSE_LIST_TYPE = new TypeToken<List<Course>>(){}.getType();

    // 默认初始数据
    private static List<Course> getDefaultCourses() {
        return Arrays.asList(
                new Course("Math", "09:00 AM", "Room 101", "Monday"),
                new Course("Science", "10:00 AM", "Room 102", "Tuesday"),
                new Course("English", "01:00 PM", "Room 104", "Thursday")
                );
    }

    // 获取设备存储文件
    private static File getDataFile(Context context) {
        return new File(context.getFilesDir(), FILE_NAME);
    }

    /**
     * 初始化数据（仅当首次运行或数据丢失时调用）
     */
    public static void initDataIfNeeded(Context context) {
        File dataFile = getDataFile(context);
        if (!dataFile.exists()) {
            save_data(context, getDefaultCourses());
            Log.d(TAG, "初始化默认数据完成");
        }
    }

    /**
     * 从设备存储加载数据
     */
    public static List<Course> load_data(Context context) {
        File dataFile = getDataFile(context);
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            List<Course> courses = new Gson().fromJson(reader, COURSE_LIST_TYPE);
            return courses != null ? courses : new ArrayList<>();
        } catch (IOException e) {
            Log.e(TAG, "加载失败，返回空列表", e);
            return new ArrayList<>();
        }
    }

    /**
     * 保存数据到设备存储
     */
    public static void save_data(Context context, List<Course> courses) {
        File dataFile = getDataFile(context);
        try (FileWriter writer = new FileWriter(dataFile)) {
            new Gson().toJson(courses, writer);
            Log.d(TAG, "数据已保存至: " + dataFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "保存失败", e);
        }
    }
}