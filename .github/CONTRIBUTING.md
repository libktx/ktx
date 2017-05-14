## Issues

- Before submitting a bug-related issue, make sure that **its source is not the LibGDX itself**.
- Issues are among the fastest way to contact the developers. It is fine to create issues with feature requests and
questions about the library.

## Pull requests

- The latest changes are always in the `develop` branch. `master` branch always matches the latest stable release. Make
sure to checkout `develop` branch before starting your work and set `develop` as the target branch before creating
a pull request.
- Use IntelliJ Kotlin [code formatter](../intellij-formatter.xml) included in the root directory.
- Make sure to include unit tests of your code. Test names should use the `` `backtick method name` `` syntax.
JUnit and [KotlinTest](https://github.com/kotlintest/kotlintest) can be used to write tests. Use
[Mockito-Kotlin](https://github.com/nhaarman/mockito-kotlin) for mocking.
- If your pull request is not a simple bug fix or small utility, make sure to link it to an existing issue or create
an issue with your proposal first. API changes or new modules have to be discussed with the maintainers first.
- All notable changes should be added to the [changelog](../CHANGELOG.md) with an appropriate label:
  - **[FEATURE]** - a new functionality.
  - **[CHANGE]** - breaking API change.
  - **[UPDATE]** - update of one of project dependencies.
  - **[FIX]** - bug fix.
- Some libraries (like `ktx-collections` or `ktx-math`) list _all_ features in the `README.md` files to ease their
usage. When adding new feature to these projects, please add description of your change to the file. Also, add all
necessary imports in KTX examples in `README.md` files to make it easier to try them out.

## Working from sources

```bash
git clone https://github.com/libktx/ktx.git
cd ktx
git checkout develop
```

### Build tool

The project itself is managed by [Gradle](http://gradle.org/). Gradle wrapper is not included, so you might want to
install it locally. Scripts should be compatible with Gradle `3.+`. If you consider working from sources, these are
some useful Gradle tasks that you can look into:

- `gradle build install` - builds the libraries archives and pushes them to Maven Local.
- `gradle check` - runs all tests in all projects.
- `gradle clean` - removes `build` directories.
- `gradle distZip` - prepares a zip archive with all jars in `build/distributions` folder. Useful for releases.
- `gradle uploadArchives` - pushes the archives to Maven Central. Requires proper `gradle.properties` with signing and
logging data.
- `gradle closeAndPromoteRepository` - closes and promotes Nexus repository. Should be run after `uploadArchives` in
case of a non-snapshot upload to Maven Central.

### Versioning and uploading

Releasing a new KTX version:

- Change `libVersion` settings in `version.txt`. KTX uses the same versioning schema as LibGDX (mimicking the
LibGDX version that it was compiled against) with optional `-b#` or `-SNAPSHOT` suffixes depending on version status.
- Run `gradle build install uploadArchives closeAndPromoteRepository` to push artifacts to both _Maven Local_ and
_Maven Central_.
- Run `gradle distZip` to prepare archive with KTX sources, compiled binary and documentation.
- Upload the archive to [releases](https://github.com/libktx/ktx/releases) section. Tag should match released version.
Name of the release should match `KTX $libVersion`. Copy latest [changelog](../CHANGELOG.md) entries to release
description. Note that a release is not necessary for snapshot versions.

### Adding a new KTX module

Adding a new library to KTX:
- Create folder matching module name in root of the repository. We generally name the modules with a single word,
separated with a dash when necessary.
- Add folder name to `settings.gradle` file. This will be the project identifier that you use to run individual Gradle
tasks, like `gradle actors:test` for example.
- Create `src/main/kotlin` and `src/test/kotlin` directories in your module folder. They will be automaticall marked
as source thanks to Gradle. You can also create package structure matching `ktx/your/module` in each source folder.
- Add `gradle.properties` file with the following properties:

```properties
projectName=ktx-your-module
projectDesc=Description of your module as it will appear in Maven Central.
```

- Add `build.gradle` file. It should contain dependencies specific to your module. If there are none, you can leave it
empty.
- Add `README.md` file describing your module. Refer to other `README.md` files for guidelines.
- Your final module structure should roughly match this schema:

```
> your-module/
  > src/
    > main/kotlin/ktx/your/module/
      > yourModule.kt
    > test/kotlin/ktx/your/module/
      > yourModuleTest.kt
  > build.gradle
  > gradle.properties
  > README.md
```

