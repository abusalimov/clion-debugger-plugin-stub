package com.example.helloplugin

import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.XDebuggerAssertions.assertCurrentPosition
import com.intellij.xdebugger.XDebuggerTestUtil.toggleBreakpoint
import com.jetbrains.cidr.CidrTestDataFixture
import com.jetbrains.cidr.cpp.CPPTestCase
import com.jetbrains.cidr.cpp.cmake.CMakeProjectFixture
import com.jetbrains.cidr.cpp.execution.CMakeExecutionFixture
import com.jetbrains.cidr.cpp.toolchains.CPPEnvironment
import com.jetbrains.cidr.execution.debugger.CidrCustomDebuggerProvider
import com.jetbrains.cidr.execution.debugger.CidrDebuggingFixture
import com.jetbrains.cidr.execution.debugger.CidrDebuggingFixture.DebuggerState.RESUMED
import com.jetbrains.cidr.execution.debugger.CidrDebuggingFixture.waitForEvent
import com.jetbrains.cidr.execution.debugger.CidrDebuggingTestCase
import com.jetbrains.cidr.execution.debugger.DebuggerDriverKind
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriverConfiguration
import org.junit.Test
import java.util.concurrent.BlockingQueue


class MyGDBSessionTest :
    CidrDebuggingTestCase<CMakeProjectFixture, CMakeExecutionFixture, MyDebuggingFixture, MyTestProjectMarkup>(
        DebuggerDriverKind.GDB, MyTestProjectMarkup.TARGET_NAME, MyTestProjectMarkup.PROJECT_DIR
    ) {
    override fun createProjectFixture(): CMakeProjectFixture = CMakeProjectFixture(myTestDataFixture)
    override fun createExecutionFixture(): CMakeExecutionFixture = CMakeExecutionFixture(myProjectFixture)
    override fun createDebuggingFixture(): MyDebuggingFixture = MyDebuggingFixture(myExecutionFixture)
    override fun createTestDataFixture(): CidrTestDataFixture = MyTestDataFixture.create()
    override fun createProjectMarkup(projectDir: VirtualFile): MyTestProjectMarkup = MyTestProjectMarkup(projectDir)
    override fun getEnvironment(): CPPEnvironment = CPPTestCase.getTestCPPEnvironment()

    override fun setUp() {
        super.setUp()
        CidrCustomDebuggerProvider.EP_NAME.point.registerExtension(object : CidrCustomDebuggerProvider {
            override fun isAvailable(environment: ExecutionEnvironment): Boolean = true
            override fun getDebuggerConfigurations(): List<DebuggerDriverConfiguration> {
                return listOf(myDebuggingFixture.createDriverConfiguration())
            }
        }, testRootDisposable)
    }

    @Test
    fun testSessionBreakpointInMain() {
        toggleBreakpoint(project, myProjectMarkup.FILE_MAIN, myProjectMarkup.LINE_MAIN_RETURN)
        startDebugSessionAndWaitForPause()
        assertCurrentPosition(session, myProjectMarkup.FILE_MAIN, myProjectMarkup.LINE_MAIN_RETURN)
    }

    @Test
    fun testSessionStepInto() {
        toggleBreakpoint(project, myProjectMarkup.FILE_MAIN, myProjectMarkup.LINE_FUN)
        val state: BlockingQueue<CidrDebuggingFixture.DebuggerState> = startDebugSessionAndWaitForPause("fun")
        assertCurrentPosition(session, myProjectMarkup.FILE_MAIN, myProjectMarkup.LINE_FUN)

        session.stepInto()

        waitForEvent(state, RESUMED)
        waitForEvent(state, PAUSED)
        assertCurrentPosition(session, myProjectMarkup.FILE_MAIN, myProjectMarkup.LINE_CALLME)
    }
}
