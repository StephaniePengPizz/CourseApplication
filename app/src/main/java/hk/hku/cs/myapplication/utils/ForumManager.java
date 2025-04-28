package hk.hku.cs.myapplication.utils;

import java.util.HashMap;
import java.util.Map;

import hk.hku.cs.myapplication.models.forum.Forum;
import hk.hku.cs.myapplication.models.forum.Message;

public class ForumManager {
    private static Map<String, Forum> forumMap = new HashMap<>();

    // 获取或创建论坛
    public static Forum getOrCreateForum(String courseName) {
        if (!forumMap.containsKey(courseName)) {
            forumMap.put(courseName, new Forum(courseName));
        }
        return forumMap.get(courseName);
    }

    public static void postMessage(String courseName, String sender, String content) {
        Forum forum = getOrCreateForum(courseName);
        forum.postMessage(new Message(sender, content, System.currentTimeMillis()));
    }

    public static Forum getForum(String courseName) {
        return forumMap.get(courseName);
    }
}