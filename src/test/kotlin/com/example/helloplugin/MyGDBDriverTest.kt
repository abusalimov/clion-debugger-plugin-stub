package com.example.helloplugin

import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.cidr.CidrTestDataFixture
import com.jetbrains.cidr.CidrTestProjectDescription
import com.jetbrains.cidr.cpp.CPPTestCase
import com.jetbrains.cidr.cpp.cmake.CMakeProjectFixture
import com.jetbrains.cidr.cpp.execution.CMakeExecutionFixture
import com.jetbrains.cidr.cpp.toolchains.CPPEnvironment
import com.jetbrains.cidr.execution.debugger.CidrDebuggerTestCase
import com.jetbrains.cidr.execution.debugger.CidrDebuggingFixture
import com.jetbrains.cidr.execution.debugger.CidrDebuggingFixture.DriverEvent
import com.jetbrains.cidr.execution.debugger.CidrDebuggingFixture.DriverEvent.Kind.INTERRUPTED
import com.jetbrains.cidr.execution.debugger.DebuggerDriverKind
import org.junit.Test
import java.util.concurrent.BlockingQueue

class MyGDBDriverTest :
    CidrDebuggerTestCase<CMakeProjectFixture, CMakeExecutionFixture, MyDebuggingFixture, MyTestProjectMarkup>(
        DebuggerDriverKind.GDB, MyTestProjectMarkup.TARGET_NAME,
        CidrTestProjectDescription(MyTestProjectMarkup.PROJECT_DIR)
    ) {
    override fun createProjectFixture(): CMakeProjectFixture = CMakeProjectFixture(myTestDataFixture)
    override fun createExecutionFixture(): CMakeExecutionFixture = CMakeExecutionFixture(myProjectFixture)
    override fun createDebuggingFixture(): MyDebuggingFixture = MyDebuggingFixture(myExecutionFixture)
    override fun createTestDataFixture(): CidrTestDataFixture = MyTestDataFixture.create()
    override fun createProjectMarkup(projectDir: VirtualFile): MyTestProjectMarkup = MyTestProjectMarkup(projectDir)
    override fun getEnvironment(): CPPEnvironment = CPPTestCase.getTestCPPEnvironment()

    @Test
    fun testDriverBreakpointInMain() {
        val events: BlockingQueue<out DriverEvent?> = startDriverAndLaunch(
            targetExecutionCheckpointEvents(),
            withBreakpoint(myProjectMarkup.FILE_MAIN, myProjectMarkup.LINE_MAIN_RETURN)
        )
        val stopPlace = CidrDebuggingFixture.waitForEvent(events, BREAKPOINT).stopPlace
        assertFrame(
            stopPlace.frame, 0,
            myProjectMarkup.FILE_MAIN, myProjectMarkup.LINE_MAIN_RETURN,
            "main"
        )
    }

    @Test
    fun testDriverStepInto() {
        val events: BlockingQueue<out DriverEvent?> = startDriverAndLaunch(
            "fun", targetExecutionCheckpointEvents(),
            withBreakpoint(myProjectMarkup.FILE_MAIN, myProjectMarkup.LINE_FUN)
        )
        var stopPlace = CidrDebuggingFixture.waitForEvent(events, BREAKPOINT).stopPlace
        assertFrame(
            stopPlace.frame, 0,
            myProjectMarkup.FILE_MAIN, myProjectMarkup.LINE_FUN,
            ".*\\b(fun)\\b.*"
        )

        myDriver.stepInto(stopPlace.thread, false, false)

        stopPlace = CidrDebuggingFixture.waitForEvent(events, INTERRUPTED).stopPlace
        assertFrame(
            stopPlace.frame, 0,
            myProjectMarkup.FILE_MAIN, myProjectMarkup.LINE_CALLME,
            ".*\\b(callme)\\b.*"
        )
    }
}
