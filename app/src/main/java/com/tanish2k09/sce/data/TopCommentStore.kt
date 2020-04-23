package com.tanish2k09.sce.data

object TopCommentStore {
    private var topComment = StringBuilder()

    val comment: String
        get() = topComment.toString()

    fun appendLine(line: String) {
        topComment.append(line).append('\n')
    }

    fun clear() {
        topComment = StringBuilder()
    }
}
