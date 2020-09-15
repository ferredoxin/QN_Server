package me.singleNeuron.data.appcenter

data class AppCenterDistributeData(
        val app_display_name:String = "",
        var release_notes:String = "",
        val version:String = "",
        val short_version:String = "",
        val send_at:String = ""
)