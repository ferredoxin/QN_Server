package me.singleNeuron.data

import me.singleNeuron.base.MarkdownAble

data class DevLog(
    val kind: String,
    val detail: String
): MarkdownAble {

    override fun toMarkdown(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("*Result: $kind*\n\n")
        stringBuilder.append("`$detail`")
        return stringBuilder.toString()
    }

}
