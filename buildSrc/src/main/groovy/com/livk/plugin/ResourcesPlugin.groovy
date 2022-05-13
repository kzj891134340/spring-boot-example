package com.livk.plugin

import io.spring.javaformat.gradle.tasks.Format
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.compile.JavaCompile

/**
 * <p>
 * ResourcesPlugin
 * </p>
 *
 * @author livk
 * @date 2022/4/24
 */
class ResourcesPlugin implements Plugin<Project> {

    private static final List<String> COMPILER_ARGS = new ArrayList<>()
    private static final String MAPSTRUCT_NAME = "mapstruct"
    private static final String MAPSTRUCT_COMPILER_ARGS = "-Amapstruct.unmappedTargetPolicy=IGNORE";

    static {
        COMPILER_ARGS.addAll(Arrays.asList(
                "-Xlint:-options",
                "-Xlint:rawtypes",
                "-Xlint:deprecation",
                "-Xlint:unchecked",
        ))
    }

    @Override
    void apply(Project project) {
        def javaCompile = project.tasks
                .getByName(JavaPlugin.COMPILE_JAVA_TASK_NAME)
                .dependsOn(Format.NAME, JavaPlugin.PROCESS_RESOURCES_TASK_NAME) as JavaCompile
        javaCompile.options.compilerArgs.addAll(COMPILER_ARGS)
        javaCompile.options.encoding = "UTF-8"
        javaCompile.doFirst {
            def dependencyName = new HashSet<>()
            project.configurations.forEach {
                dependencyName.addAll(it.dependencies.name)
            }
            if (dependencyName.contains(MAPSTRUCT_NAME)) {
                if (!javaCompile.options.compilerArgs.contains(MAPSTRUCT_COMPILER_ARGS)) {
                    javaCompile.options.compilerArgs.add(MAPSTRUCT_COMPILER_ARGS)
                }
            }
        }
    }
}
