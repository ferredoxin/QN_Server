package me.singleNeuron.data.github

data class GithubWebHookData(
        val ref:String = "",
        val commits:Array<GithubCommitData> = arrayOf()
)