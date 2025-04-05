package hk.hku.cs.myapplication.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HolidayManager {
    private static final Map<MonthDay, String> SOLAR_HOLIDAYS = new HashMap<>();
    private static final Map<LocalDate, String> LUNAR_HOLIDAYS_2025 = new HashMap<>();

    static {
        // 初始化公历节日
        SOLAR_HOLIDAYS.put(MonthDay.of(1, 1), "元旦");
        SOLAR_HOLIDAYS.put(MonthDay.of(2, 14), "情人节");
        SOLAR_HOLIDAYS.put(MonthDay.of(3, 8), "妇女节");
        SOLAR_HOLIDAYS.put(MonthDay.of(3, 12), "植树节");
        SOLAR_HOLIDAYS.put(MonthDay.of(4, 1), "愚人节");
        SOLAR_HOLIDAYS.put(MonthDay.of(5, 1), "劳动节");
        SOLAR_HOLIDAYS.put(MonthDay.of(5, 4), "青年节");
        SOLAR_HOLIDAYS.put(MonthDay.of(6, 1), "儿童节");
        SOLAR_HOLIDAYS.put(MonthDay.of(7, 1), "建党节");
        SOLAR_HOLIDAYS.put(MonthDay.of(8, 1), "建军节");
        SOLAR_HOLIDAYS.put(MonthDay.of(9, 10), "教师节");
        SOLAR_HOLIDAYS.put(MonthDay.of(10, 1), "国庆节");
        SOLAR_HOLIDAYS.put(MonthDay.of(12, 25), "圣诞节");

        // 初始化2025年农历节日（使用公历日期）
        LUNAR_HOLIDAYS_2025.put(LocalDate.of(2025, 1, 29), "除夕");
        LUNAR_HOLIDAYS_2025.put(LocalDate.of(2025, 1, 30), "春节");
        LUNAR_HOLIDAYS_2025.put(LocalDate.of(2025, 2, 28), "元宵节");
        LUNAR_HOLIDAYS_2025.put(LocalDate.of(2025, 4, 4), "清明节");
        LUNAR_HOLIDAYS_2025.put(LocalDate.of(2025, 5, 28), "端午节");
        LUNAR_HOLIDAYS_2025.put(LocalDate.of(2025, 8, 24), "中秋节");
        LUNAR_HOLIDAYS_2025.put(LocalDate.of(2025, 9, 23), "重阳节");
    }

    public static String getHoliday(LocalDate date) {
        // 先检查农历节日（仅2025年）
        String lunarHoliday = LUNAR_HOLIDAYS_2025.get(date);
        if (lunarHoliday != null) {
            return lunarHoliday;
        }

        // 检查公历节日
        MonthDay monthDay = MonthDay.from(date);
        return SOLAR_HOLIDAYS.get(monthDay);
    }
}