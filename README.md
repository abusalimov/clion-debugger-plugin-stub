# clion-debugger-plugin-stub
Showcase for developing debugger tests using the CLion testing infrastructure in an external plugin

## Overview
The key class is `com.jetbrains.cidr.execution.debugger.CidrDebuggerTestCase`,
if everything is set up correctly, it should be available in the test scope.
Debugger tests use a test project built using CMake. Before running the tests,
the infrastructure builds it and remembers marker lines for setting breakpoints
into a subclass of `com.jetbrains.cidr.CidrDebugProjectMarkup`. That particular
class is tied to the specific debug project.

There's two sets of tests:
  - those inheriting from
    `com.jetbrains.cidr.execution.debugger.CidrDebuggerDriverTestCase` - tests
    for the debugger driver, they run without creating a debug session or
    a CidrDebugProcess instance. It's easier to debug failures in these tests;
    
  - and those inheriting from
    `com.jetbrains.cidr.execution.debugger.CidrDebuggingWithDebugProjectTestCase` -
    these are more like "integration" tests, with an XDebugSession,
    CidrDebugProcess and stuff.

Both flavors of the tests usually set some breakpoint, run the test program with
certain arguments to trigger the breakpoint, and then probably inspect variables.
Both revolve around a blocking queue of events,
either `CidrDebuggingFixture.DriverEvent` or `CidrDebuggingFixture.DebuggerState`.
