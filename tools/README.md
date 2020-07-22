# KTX: Tools

Utilities for working with KTX or generating code for use with LibGDX.

### Why?

Like the LibGDX [`gdx-tools`](https://github.com/libgdx/libgdx/tree/master/extensions/gdx-tools) library, this library 
contains tools for development rather than use in your game itself.

### Guide

This module contains tools that should not be directly included in any application modules, including `core`. Instead, it 
should be imported as a `buildscript` `classpath` dependency so its Gradle plugin can be used. The plugin adds Gradle 
tasks to the project in the `ktx` group. It can be used by adding this to the `build.gradle` file:

    apply plugin: "io.github.libktx.tools"
    
The KTX tools can also be used within scripts, libraries or other separate development applications. In this case, you 
can import this library as a regular dependency of a JVM module. However, make sure this library is not included in your 
game modules (such as `core`), as it will unnecessarily increase the application size. Unlike the other KTX modules,
this one requires JDK8 and `kotlin-stdlib-jdk8` or higher.

#### BundleLineCreator

`BundleLineCreator` is used to generate `BundleLine` enum classes from properties files for use with [`ktx-i18n`](../i18n).
 It searches a directory for `.properties` files, extracts the property names, and adds a `BundleLine` enum class with 
corresponding enum values. The default behavior requires only an output package name. It automatically finds the 
`assets` directory as found in a project created with LibGDX Setup, and favors limiting its search to a sub-directory 
named `i18n` or `nls` if either exists. It combines property names into a single enum class named `Nls`, placed in the 
output package in the `core` module, overwriting if necessary.

To run this default behavior from a JVM app, call `BundleLineCreator.execute("com.mycompany.mygame")`.

To run it as a Gradle task, you can first set the output package name in the `gradle.build` using

```groovy
ktx {
    createBundleLines.targetPackage = "com.mycompany.mygame"
}
```

and then running the `createBundleLines` task in the `ktx` group. IntelliJ's 
[FileWatchers](https://www.jetbrains.com/help/idea/using-file-watchers.html) plugin may be of interest as it can be set 
up to run the task when a file changes.

See usage examples below for how to customize the behavior. Note that `BundleLineCreator` can also be subclassed to 
customize its behavior farther.

### Usage examples

Using all the customization options when running `BundleLinesCreator`.

In a `build.gradle` file to customize `createBundleLines` task behavior:

```groovy
ktx {
  createBundleLines.targetPackage = "com.mycompany.mygame"
  createBundleLines.explicitParentDirectory = "android/assets/bundles"
  createBundleLines.searchSubdirectories = false
  createBundleLines.targetSourceDirectory = "mygamecore/src"
  createBundleLines.enumClassName = "Keys"
}
```

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

