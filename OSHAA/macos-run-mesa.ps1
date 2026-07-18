$ErrorActionPreference = "Stop"

$mesa = Join-Path $HOME "mesa-native"
$glfw = Join-Path $HOME "glfw/build/src/libglfw.3.dylib"

$requiredFiles = @(
    "$mesa/lib/libgl_interpose.dylib"
    "$mesa/lib/libEGL.dylib"
    "$mesa/lib/libGL.dylib"
    "$mesa/lib/libvulkan.1.dylib"
    "$mesa/share/vulkan/icd.d/kosmickrisp_mesa_icd.aarch64.json"
    $glfw
)

foreach ($file in $requiredFiles) {
    if (-not (Test-Path -LiteralPath $file)) {
        throw "Required file not found: $file"
    }
}

# Escape paths for Groovy single-quoted strings.
$mesaGroovy = $mesa.Replace("\", "\\").Replace("'", "\'")
$glfwGroovy = $glfw.Replace("\", "\\").Replace("'", "\'")

$initScript = Join-Path `
    ([IO.Path]::GetTempPath()) `
    "macos-mesa-$PID.init.gradle"

@"
gradle.beforeProject { project ->
    project.pluginManager.withPlugin("net.neoforged.gradle.common") {
        def runs = project.extensions.getByName("runs")

        runs.configureEach { run ->
            if (run.name == "client") {
                run.jvmArguments "-XstartOnFirstThread"

                run.systemProperties(
                    "org.lwjgl.egl.libname": "$mesaGroovy/lib/libEGL.dylib",
                    "org.lwjgl.opengl.libname": "$mesaGroovy/lib/libGL.dylib",
                    "org.lwjgl.glfw.libname": "$glfwGroovy"
                )

                run.environmentVariables(
                    "DYLD_INSERT_LIBRARIES": "$mesaGroovy/lib/libgl_interpose.dylib",
                    "DYLD_LIBRARY_PATH": "$mesaGroovy/lib",
                    "LIBGL_DRIVERS_PATH": "$mesaGroovy/lib/dri",
                    "VK_DRIVER_FILES": "$mesaGroovy/share/vulkan/icd.d/kosmickrisp_mesa_icd.aarch64.json",

                    "EGL_PLATFORM": "surfaceless",
                    "MESA_LOADER_DRIVER_OVERRIDE": "zink",
                    "MESA_GL_VERSION_OVERRIDE": "4.6",
                    "MESA_GLSL_VERSION_OVERRIDE": "460",

                    "MESA_EGL_LIBRARY": "$mesaGroovy/lib/libEGL.dylib",
                    "MESA_VULKAN_LIBRARY": "$mesaGroovy/lib/libvulkan.1.dylib",

                    "ZINK_DESCRIPTORS": "auto"
                )
            }
        }
    }
}
"@ | Set-Content -LiteralPath $initScript -Encoding utf8NoBOM

try {
    & /bin/bash ./gradlew `
        --init-script $initScript `
        runClient `
        --no-daemon `
        --no-configuration-cache

    $exitCode = $LASTEXITCODE
}
finally {
    Remove-Item -LiteralPath $initScript -Force -ErrorAction SilentlyContinue
}

exit $exitCode
