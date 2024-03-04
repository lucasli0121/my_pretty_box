package com.updatelibrary

import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.InputStream
import java.lang.Exception
import java.util.HashMap
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.Throws

object XmlParser {
    @JvmStatic
    @Throws(Exception::class)
    fun parseXml(inStream: InputStream?): HashMap<String, HashMap<String, String> > {
        val hashMap = HashMap<String, HashMap<String, String> >()
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(inStream)
        val root = document.documentElement
        val childNodes = root.childNodes
        for (j in 0 until childNodes.length) {
            val childNode = childNodes.item(j) as Node
            if (childNode.nodeType == Node.ELEMENT_NODE) {
                val element = childNode as Element
                var key = element.nodeName
                var childMap = HashMap<String, String>()
                if(element.hasChildNodes()) {
                    if(element.hasAttributes()) {
                        key = element.getAttribute("name")
                        childMap["desc"] = element.getAttribute("desc")
                    }
                    for(n in 0 until element.childNodes.length) {
                        val childNode = element.childNodes.item(n) as Node
                        if (childNode.nodeType == Node.ELEMENT_NODE) {
                            childMap[childNode.nodeName] = childNode.firstChild.nodeValue
                        } else if(childNode.nodeType == Node.TEXT_NODE) {
                            childMap[childNode.nodeName] = childNode.nodeValue
                        }
                    }
                }
                hashMap[key] = childMap
            }
        }
        return hashMap
    }

    @Throws(Exception::class)
    fun parseXml2(inStream: InputStream?): HashMap<String, String> {
        val hashMap = HashMap<String, String>()

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(inStream)
        val root = document.documentElement
        val childNodes = root.childNodes
        for (j in 0 until childNodes.length) {
            val childNode = childNodes.item(j) as Node
            if (childNode.nodeType == Node.ELEMENT_NODE) {
                val childElement = childNode as Element
                if ("version" == childElement.nodeName) {
                    hashMap["version"] = childElement.firstChild.nodeValue
                } else if ("name" == childElement.nodeName) {
                    hashMap["name"] = childElement.firstChild.nodeValue
                } else if ("url" == childElement.nodeName) {
                    hashMap["url"] = childElement.firstChild.nodeValue
                }
            }
        }
        return hashMap
    }
}