package com.example.helloplugin

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.cidr.CidrTestDataFixture
import com.jetbrains.cidr.CidrTestProjectDescription
import com.jetbrains.cidr.cpp.CPPTestCase
import com.jetbrains.cidr.cpp.cmake.CMakeProjectFixture
import com.jetbrains.cidr.cpp.execution.CMakeExecutionFixture
import com.jetbrains.cidr.cpp.execution.debugger.gdb.CMakeGDBDebuggingFixture
import com.jetbrains.cidr.cpp.toolchains.CPPEnvironment
import com.jetbrains.cidr.execution.debugger.CidrDebuggerTestCase
import com.jetbrains.cidr.execution.debugger.CidrDebuggingFixture
import com.jetbrains.cidr.execution.debugger.CidrDebuggingFixture.DriverEvent
import com.jetbrains.cidr.execution.debugger.DebuggerDriverKind
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriver.StopPlace
import org.junit.Test
import java.io.File
import java.util.concurrent.BlockingQueue

class MyGDBDriverTest :
    CidrDebuggerTestCase<CMakeProjectFixture?, CMakeExecutionFixture?, CMakeGDBDebuggingFixture?, MyTestProjectMarkup>(
        DebuggerDriverKind.GDB, MyTestProjectMarkup.TARGET_NAME,
        CidrTestProjectDescription(MyTestProjectMarkup.PROJECT_DIR)
    ) {

    override fun createProjectFixture(): CMakeProjectFixture {
        return CMakeProjectFixture(myTestDataFixture)
    }

    override fun createExecutionFixture(): CMakeExecutionFixture {
        return CMakeExecutionFixture(myProjectFixture!!)
    }

    override fun createDebuggingFixture(): CMakeGDBDebuggingFixture {
        return CMakeGDBDebuggingFixture(myExecutionFixture!!)
    }

    override fun createTestDataFixture(): CidrTestDataFixture {
        val testData =
            checkNotNull(PathManager.getResourceRoot(MyGDBDriverTest::class.java, "/projects")) { "testData" }
        return CidrTestDataFixture(File(testData), false)
    }

    override fun createProjectMarkup(projectDir: VirtualFile): MyTestProjectMarkup {
        return MyTestProjectMarkup(projectDir)
    }

    override fun getEnvironment(): CPPEnvironment {
        return CPPTestCase.getTestCPPEnvironment()
    }

    @Test
    @Throws(Exception::class)
    fun testBreakpointInMain() {
        val events: BlockingQueue<out DriverEvent?> = startDriverAndLaunch(
            "arg", targetExecutionCheckpointEvents(),
            withBreakpoint(myProjectMarkup.FILE_MAIN, myProjectMarkup.LINE_BREAKPOINT)
        )
        val stopPlace: StopPlace = CidrDebuggingFixture.waitForEvent(events, BREAKPOINT).stopPlace
        assertFrame(
            stopPlace.frame, 0,
            myProjectMarkup.FILE_MAIN, myProjectMarkup.LINE_BREAKPOINT,
            "main"
        )
    }
}
