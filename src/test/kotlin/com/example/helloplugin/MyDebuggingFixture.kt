package com.example.helloplugin

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.util.Function
import com.jetbrains.cidr.cpp.CPPTestCase
import com.jetbrains.cidr.cpp.execution.CMakeExecutionFixture
import com.jetbrains.cidr.cpp.execution.debugger.CLionDebuggingFixture
import com.jetbrains.cidr.cpp.execution.debugger.backend.CLionGDBDriverConfiguration
import com.jetbrains.cidr.execution.debugger.DebuggerDriverKind
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriverConfiguration

class MyDebuggingFixture(executionFixture: CMakeExecutionFixture) :
    CLionDebuggingFixture<CMakeExecutionFixture>(DebuggerDriverKind.GDB, executionFixture) {

    fun createDriverConfiguration(): DebuggerDriverConfiguration {
        return createDriverConfiguration { cl -> cl /* ignored */ }
    }

    override fun createDriverConfiguration(driverCommandLineConfigurator: Function.Mono<GeneralCommandLine>?): DebuggerDriverConfiguration {
        setUpToolchain() // a bit hackish to do that in a (supposedly pure) factory method
        return CLionGDBDriverConfiguration(myExecutionFixture.projectFixture.project, environment)
    }

    override fun configureDebugSessionSettings() {
        setUpToolchain()
    }

    private fun setUpToolchain() {
        CPPTestCase.changeTestToolchain { toolchain ->
//            toolchain.debugger = CPPDebugger.customGdb("/path/to/gdb")
            toolchain.setBundledOrToolSetGDB()
        }
    }
}