package me.singleNeuron.me.singleNeuron.data.appcenter

import me.singleNeuron.base.MarkdownAble

data class AppCenterCrashData(
        val name:String = "",
        val reason:String = "",
        val url:String = "",
        val app_version:String = ""
): MarkdownAble {
    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("坏耶...又有新Bug了\n")
        stringBuilder.append("$name\n")
        stringBuilder.append("Caused by: `$reason`\n")
        stringBuilder.append("QQ版本: _${app_version}_\n")
        stringBuilder.append("[查看]($url)")
        return stringBuilder.toString()
    }
    override fun toMarkdown(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("坏耶...又有新Bug了\n")
        stringBuilder.append("$name\n")
        stringBuilder.append("Caused by: $reason\n")
        stringBuilder.append("QQ版本: $app_version\n")
        stringBuilder.append(url)
        return stringBuilder.toString()
    }
}