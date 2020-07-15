# KTX: Tools

Utilities for working with Ktx or generating code for use with LibGDX.

### Why?

Like the LibGDX `gdx-tools` library, this library contains tools for development rather than use in your game itself.

### Guide

This module contains tools that should be not be included in the core module. It can be imported as a `buildscript` 
`classpath` dependency so its Gradle plugin can be used. The plugin adds Gradle tasks to the project in the `ktx tools` 
group. It can be used by adding this to the `build.gradle` file:

    apply plugin: "io.github.libktx.tools"
    
The tools can also be used from an application. In this case, import this library as a dependency of a JVM module (not 
the `core` game module.)

#### BundleLineCreator

`BundleLineCreator` is used to generate `BundleLine` enum classes from properties files for use with `ktx-i18n`. It 
searches a directory for `.properties` files, extracts the property names, and adds a `BundleLine` enum class with 
corresponding enum values. The default behavior requires only an output package name. It automatically finds the 
`assets` directory as found in a project created with LibGDX Setup, and favors limiting its search to a sub-directory 
named `i18n` or `nls` if either exists. It combines property names into a single enum class named `Nls`, placed in the 
output package in the `core` module, overwriting if necessary.

To run this default behavior from a JVM app, call `BundleLineCreator.execute("com.mycompany.mygame")`.

To run it as a Gradle task, you can first set the output package name in the `gradle.build` using

```groovy
ktxTools {
    createBundleLines.targetPackage = "com.mycompany.mygame"
}
```

and then running the `createBundleLines` task in the `ktx tools` group. IntelliJ's 
[FileWatchers](https://www.jetbrains.com/help/idea/using-file-watchers.html) plugin may be of interest as it can be set 
up to run the task when a file changes.

See usage examples below for how to customize the behavior. Note that `BundleLineCreator` can also be subclassed to 
customize its behavior farther.

##

### Usage examples

Using all the customization options when running `BundleLinesCreator`.

In a JVM app:

```kotlin
BundleLinesCreator.execute(
  targetPackage = "com.mycompany.mygame",
  explicitParentDirectory = "android/assets/bundles",
  searchSubdirectories = false,
  targetSourceDirectory = "mygamecore/src",
  enumClassName = "Keys"
)
```

In a `build.gradle` file to customize `createBundleLines` task behavior:

```groovy
ktxTools {
  createBundleLines.targetPackage = "com.mycompany.mygame"
  createBundleLines.explicitParentDirectory = "android/assets/bundles"
  createBundleLines.searchSubdirectories = false
  createBundleLines.targetSourceDirectory = "mygamecore/src"
  createBundleLines.enumClassName = "Keys"
}
```

