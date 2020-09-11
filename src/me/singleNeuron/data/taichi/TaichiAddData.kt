package me.singleNeuron.data.taichi

data class TaichiAddData(
        val aid:Int = -1,
        val name:String = "",
        val desc:String = "",
        val magisk:Boolean = false,
        val log:String = ""
) {
    companion object{
        fun fromTaichiUploadData(data:TaichiUploadData,log:String):TaichiAddData {
            return TaichiAddData(data.data.id,data.data.name,data.data.desc,false,log)
        }
    }
}