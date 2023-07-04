package com.example.helloplugin

import com.intellij.openapi.application.PathManager
import com.jetbrains.cidr.CidrTestDataFixture
import java.io.File

class MyTestDataFixture(testData: String) : CidrTestDataFixture(File(testData), false) {
    companion object {
        fun create(): MyTestDataFixture {
            val resourceRoot = PathManager.getResourceRoot(MyTestDataFixture::class.java, "/projects")
            val testData = checkNotNull(resourceRoot) { "testData" }
            return MyTestDataFixture(testData)
        }
    }
}