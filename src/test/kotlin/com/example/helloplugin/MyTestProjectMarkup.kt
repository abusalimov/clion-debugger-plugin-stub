package com.example.helloplugin

import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.cidr.CidrProjectMarkup

@Suppress("PropertyName")
class MyTestProjectMarkup(testProjectRoot: VirtualFile) : CidrProjectMarkup(testProjectRoot, true) {
    lateinit var FILE_MAIN: VirtualFile
    var LINE_FUN = 0
    var LINE_CALLME = 0

    companion object {
        const val PROJECT_DIR = "debug-project/"
        const val TARGET_NAME = "debug_project"
    }
}
