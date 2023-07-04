package com.example.helloplugin

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.XDebuggerAssertions.assertCurrentPosition
import com.intellij.xdebugger.XDebuggerTestUtil.toggleBreakpoint
import com.jetbrains.cidr.CidrTestDataFixture
import com.jetbrains.cidr.cpp.CPPTestCase
import com.jetbrains.cidr.cpp.cmake.CMakeProjectFixture
import com.jetbrains.cidr.cpp.execution.CMakeExecutionFixture
import com.jetbrains.cidr.cpp.execution.debugger.gdb.CMakeGDBDebuggingFixture
import com.jetbrains.cidr.cpp.toolchains.CPPEnvironment
import com.jetbrains.cidr.execution.debugger.CidrDebuggingFixture
import com.jetbrains.cidr.execution.debugger.CidrDebuggingFixture.DebuggerState.*
import com.jetbrains.cidr.execution.debugger.CidrDebuggingFixture.waitForEvent
import com.jetbrains.cidr.execution.debugger.CidrDebuggingTestCase
import com.jetbrains.cidr.execution.debugger.DebuggerDriverKind
import org.junit.Test
import java.util.concurrent.BlockingQueue


class MyGDBSessionTest :
    CidrDebuggingTestCase<CMakeProjectFixture?, CMakeExecutionFixture?, CMakeGDBDebuggingFixture?, MyTestProjectMarkup>(
        DebuggerDriverKind.GDB, MyTestProjectMarkup.TARGET_NAME, MyTestProjectMarkup.PROJECT_DIR
    ) {
    override fun createProjectFixture(): CMakeProjectFixture = CMakeProjectFixture(myTestDataFixture)
    override fun createExecutionFixture(): CMakeExecutionFixture = CMakeExecutionFixture(myProjectFixture!!)
    override fun createDebuggingFixture(): CMakeGDBDebuggingFixture = CMakeGDBDebuggingFixture(myExecutionFixture!!)
    override fun createTestDataFixture(): CidrTestDataFixture = MyTestDataFixture.create()
    override fun createProjectMarkup(projectDir: VirtualFile): MyTestProjectMarkup = MyTestProjectMarkup(projectDir)
    override fun getEnvironment(): CPPEnvironment = CPPTestCase.getTestCPPEnvironment()

    @Test
    @Throws(Exception::class)
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
