package me.singleNeuron.base

interface HtmlAble {
    fun toHtml():String
    fun processHtmlChar(msg:String):String {
        return msg.replace("<","&lt;").replace(">","&gt;").replace("&","&amp;")
    }
}