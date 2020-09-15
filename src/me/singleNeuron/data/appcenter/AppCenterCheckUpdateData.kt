package me.singleNeuron.data.appcenter

import me.singleNeuron.base.HtmlAble
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
        var release_notes:String = "",
        val status:String = ""
):MarkdownAble, HtmlAble {
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

    override fun toHtml(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("<b>$app_name</b> ($bundle_identifier)\n\n")
        stringBuilder.append("New Version <code>$short_version($version)</code> $status\n")
        stringBuilder.append("Build ID: <code>$id</code>\n")
        stringBuilder.append("Time: <code>$uploaded_at</code>\n")
        stringBuilder.append("\n${processHtmlChar(release_notes)}")
        return stringBuilder.toString()
    }
}