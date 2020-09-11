package me.singleNeuron.data.github

data class GithubWebHookData(
        val ref:String = "",
        val repository:GithubRepositoryData = GithubRepositoryData(),
        val commits:Array<GithubCommitData> = arrayOf()
)

data class GithubRepositoryData (
        val name:String = "",
        val full_name:String = ""
)

data class GithubCommitData(
        val modified: Array<String> = arrayOf(),
        val message: String = ""
)