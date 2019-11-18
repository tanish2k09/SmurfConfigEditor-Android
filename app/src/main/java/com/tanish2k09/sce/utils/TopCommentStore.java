package com.tanish2k09.sce.utils;

public class TopCommentStore {
    private static StringBuilder topComment = new StringBuilder();

    public static void appendLine(String line) {
        topComment.append(line).append('\n');
    }

    public static String getComment() {
        return topComment.toString();
    }

    public static void clear() {
        topComment = new StringBuilder();
    }
}
