package me.singleNeuron.data

data class GithubWebHookData(
        val ref:String?,
        val commits:Array<GithubCommitData?>?
)