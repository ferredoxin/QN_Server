package me.singleNeuron.data.appcenter

import me.singleNeuron.base.MarkdownAble

data class AppCenterBuildData (
        val app_name:String = "",
        val branch:String = "",
        val build_status:String = "",
        val build_id:String = "",
        val build_reason:String = "",
        val finish_time:String = "",
        val source_version:String = ""
): MarkdownAble {
    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("$app_name\n")
        stringBuilder.append("Build $build_id: $build_status\n")
        stringBuilder.append("Trigger by $build_reason\n")
        stringBuilder.append("Version: $source_version\n")
        stringBuilder.append(finish_time)
        return stringBuilder.toString()
    }
    override fun toMarkdown(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("*$app_name*\n")
        stringBuilder.append("Build $build_id: _${build_status}_\n")
        stringBuilder.append("Trigger by _${build_reason}_\n")
        stringBuilder.append("Version: `$source_version`\n")
        stringBuilder.append("`$finish_time`")
        return stringBuilder.toString()
    }
}