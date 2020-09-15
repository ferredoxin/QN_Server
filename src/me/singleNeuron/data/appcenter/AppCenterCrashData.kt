package me.singleNeuron.data.appcenter

import me.singleNeuron.base.MarkdownAble

data class AppCenterCrashData(
        val name:String = "",
        val reason:String = "",
        val url:String = "",
        val app_version:String = "",
        val stack_trace:Array<String> = arrayOf()
): MarkdownAble {
    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("坏耶...又有新Bug了\n")
        stringBuilder.append("$name\n")
        stringBuilder.append("Caused by: $reason\n")
        stringBuilder.append("QQ版本: $app_version\n")
        if (!stack_trace.isNullOrEmpty()) {
            stringBuilder.append("Stack Trace:\n")
            for (string in stack_trace) {
                stringBuilder.append("    $string\n")
            }
        }
        stringBuilder.append(url)
        return stringBuilder.toString()
    }
    override fun toMarkdown(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("坏耶...又有新Bug了\n")
        stringBuilder.append("`$name`\n")
        stringBuilder.append("Caused by: `$reason`\n")
        stringBuilder.append("QQ版本: _${app_version}_\n")
        if (!stack_trace.isNullOrEmpty()) {
            stringBuilder.append("Stack Trace:\n```\n")
            for (string in stack_trace) {
                stringBuilder.append("    $string\n")
            }
            stringBuilder.append("```\n")
        }
        stringBuilder.append("[查看]($url)")
        return stringBuilder.toString()
    }
}