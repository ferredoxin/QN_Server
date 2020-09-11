package me.singleNeuron.me.singleNeuron.data.appcenter

import me.singleNeuron.base.MarkdownAble
import java.lang.StringBuilder

data class AppCenterCheckUpdateData(
        val app_name:String="",
        val id:Int = -1,
        val version:String = "",
        val short_version:String = "",
        val bundle_identifier:String = "",
        val uploaded_at:String = "",
        val download_url:String = "",
        val release_notes:String = "",
        val status:String = ""
):MarkdownAble {
    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("$app_name ($bundle_identifier)\n\n")
        stringBuilder.append("New Version $short_version($version) $status\n")
        stringBuilder.append("Build ID: $id\n")
        stringBuilder.append("Time: $uploaded_at\n")
        stringBuilder.append("\n$release_notes")
        return stringBuilder.toString()
    }
    override fun toMarkdown(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("*$app_name* ($bundle_identifier)\n\n")
        stringBuilder.append("New Version `$short_version($version)` $status\n")
        stringBuilder.append("Build ID: `$id`\n")
        stringBuilder.append("Time: `$uploaded_at`\n")
        stringBuilder.append("\n$release_notes")
        return stringBuilder.toString()
    }
}