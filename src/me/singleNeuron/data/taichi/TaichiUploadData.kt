package me.singleNeuron.data.taichi

data class TaichiUploadData(
        val code: Int = 0,
        val data: TaichiData = TaichiData()
)

data class TaichiData(
        val `package`:String = "",
        val name:String = "",
        val desc:String = "",
        val version_code:Int = -1,
        val version_name:String = "",
        val id:Int = -1
)