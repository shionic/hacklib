import org.gradle.internal.jvm.Jvm

plugins {
    `cpp-library`
}
library {
    baseName = "hacklibaccess"
    binaries.configureEach(fun CppBinary.() {
        var compileTask = compileTask.get()
        compileTask.includes.from("${Jvm.current().javaHome}/include")

        var osFamily = targetPlatform.targetMachine.operatingSystemFamily
        if (osFamily.isMacOs) {
            compileTask.includes.from("${Jvm.current().javaHome}/include/darwin")
        } else if (osFamily.isLinux) {
            compileTask.includes.from("${Jvm.current().javaHome}/include/linux")
        } else if (osFamily.isWindows) {
            compileTask.includes.from("${Jvm.current().javaHome}/include/win32")
        }
    })
    targetMachines.add(machines.linux.x86_64)
}