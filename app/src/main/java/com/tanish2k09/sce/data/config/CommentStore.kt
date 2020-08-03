package com.tanish2k09.sce.data.config

class CommentStore {
    private val comment = StringBuilder()

    fun getCommentString(): String {
        return comment.toString()
    }

    fun appendLine(line: String) {
        comment.append(line).append('\n')
    }
}
