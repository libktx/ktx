# Contribution guidelines

## Issues

- Before submitting a bug-related issue, make sure that its source is not the LibGDX itself.
- Feel free to create issues with feature requests and questions about the libraries. Issues are among the fastest way
to contact the developers.

## Pull requests

- The latest changes are always in the `develop` branch. `master` branch always matches the latest stable release. Make
sure to checkout `develop` branch before starting your work and set `develop` as the target branch before creating
a pull request.
- Include issue or pull request IDs in related commit messages. This makes it easier to locate more information about
the changes. For example:

> Kotlin version update. #250

- Use IntelliJ Kotlin [code formatter](../intellij-formatter.xml) included in the root directory.
- Make sure to include unit tests of your code. Test names should use the `` `backtick method name` `` syntax.
JUnit and [Spek](http://spekframework.org/) can be used to write tests, although JUnit is encouraged for its commonness.
Use [Mockito-Kotlin](https://github.com/nhaarman/mockito-kotlin) for mocking.
- If your pull request is not a simple bug fix or small utility, make sure to link it to an existing issue or create
an issue with your proposal first. Major API changes or new modules have to be discussed with the maintainers first.
Skipping the issue will not get your pull request rejected outright, but note that undiscussed major changes might
require a rewrite.
- All notable changes should be added to the [changelog](../CHANGELOG.md) with an appropriate label:
  - **[FEATURE]** - a new functionality.
  - **[CHANGE]** - an API breaking change.
  - **[UPDATE]** - an update of one of the major dependencies.
  - **[FIX]** - a bug fix.
  - **[MISC]** - other changes (e.g. related to documentation or the project itself).
- Most libraries list _all_ features in the `README.md` files to ease their usage. When adding a new feature,
please check the  `README.md` file of the module and add description of your change when appropriate. Any major feature
should include a usage example in the module guide. Make sure to add all of the necessary imports in the usage examples
in `README.md` files to make it easier to try them out.

## Working from sources

```bash
git clone https://github.com/libktx/ktx.git
cd ktx
git checkout develop
```

### Gradle

The project itself is managed by [Gradle](http://gradle.org/). Gradle wrapper is included, but you can use a local
Gradle installation - scripts should be compatible with Gradle `5.+`. Gradle projects are handled out of the box by
IntelliJ, so KTX should be relatively easy to import.

Some useful Gradle tasks include:

- `build install` - builds the libraries archives and pushes them to _Maven Local_. Useful for local tests.
- `check` - runs all tests in all projects.
- `clean` - removes the `build` directories, which forces rebuilds of the modules.
- `distZip` - prepares a zip archive with all jars in `build/distributions` folder. Useful for releases.
- `uploadArchives` - pushes the archives to _Maven Central_. Requires proper `gradle.properties` with archive signing
and _Sonatype_ logging data.
- `closeAndPromoteRepository` - closes and promotes Nexus repository. Should be run after `uploadArchives` in
case of a non-snapshot upload to _Maven Central_. Might fail at times on the promotion task; running `promoteRepository`
separately usually fixes the issue.

Note that since [Dokka](https://github.com/Kotlin/dokka) is used to generate documentation archives, Java 8 is required
to fully build the libraries due to a [Dokka bug](https://github.com/Kotlin/dokka/issues/294). If you prefer to develop
the library using JDK 11 or newer, you can still install JDK 8 alongside your version7 and run specific tasks with Java 8:

```bash
./gradlew clean test jar
# Pass the correct path to JDK 8:
./gradlew -Dorg.gradle.java.home=/usr/lib/jvm/java-1.8.0-openjdk-amd64 dokkaJavadoc
./gradlew uploadArchives
```

### Adding a new KTX module

- Create folder matching module name in root of the repository. Modules should generally be named with a single word.
When multiple words are necessary, use a single dash (`-`) as the word separator.
- Add folder name to `settings.gradle` file. This will also serve as the project identifier that you use in
`build.gradle` scripts and to run individual Gradle tasks (e.g. `gradle actors:test`).
- Create `src/main/kotlin` and `src/test/kotlin` directories in your module folder. They will be automatically marked
as source thanks to Gradle. You should also create package structure matching `ktx/your/module` in each source folder.
- Add `gradle.properties` file with the following properties:

```properties
projectName=ktx-your-module
projectDesc=Description of your module as it will appear in Maven Central.
```

- Add a `build.gradle` file. It should contain dependencies specific to your module. If there are none, you can leave it
empty.
- Add a `README.md` file describing your module. Refer to other `README.md` files for guidelines. `README.md` files
should generally consist of the following sections:
  - _General description_ - in a single sentence, what problem does the module solve?
  - _Motivation_ - why was the module created?
  - _Guide_ - what features does the module provide? Does it require additional setup?
  - _Usage examples_ - how to use the module?
  - _Synergy_ - is the module complemented by any other KTX libraries?
  - _Alternatives_ - are there any other libraries or modules that can be used instead?
  - _Additional documentation_ - are there any other guides or articles on the topic?
- Add _Maven Central_ badge to the top of the `README.md` to ease inclusion of the library:

```markdown
[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-your-module.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-your-module)
```

- Your final module structure should roughly match this schema:

```
> your-module/
  > src/
    > main/kotlin/ktx/your/module/
      - yourModule.kt
    > test/kotlin/ktx/your/module/
      - yourModuleTest.kt
  - build.gradle
  - gradle.properties
  - README.md
```

## Maintenance

The following sections are for the maintainers of the repository.

### Updating dependencies

Dependencies versions are stored in the [gradle.properties](../gradle.properties) file. Snapshot releases should keep
all of the dependencies (outside of testing scope) up-to-date. Major dependencies updating:

- **LibGDX**: update `gdxVersion` in the properties file and LibGDX version in the tag on the top of the 
[README.md](../README.md) file. Note that updating LibGDX also affects the KTX version, so make sure to update
[version.txt](../version.txt) as well.
- **Kotlin**: update the `kotlinVersion` property and the Kotlin tag in the [README.md](../README.md).
- **Kotlin Coroutines**: update `kotlinCoroutinesVersion` property and the tag in the
`ktx-async` [README.md](../async/README.md).
- **Gradle**: run `gradle wrapper` in the root project folder. Make sure that the
[Gradle wrapper properties file](../gradle/wrapper/gradle-wrapper.properties) points the `all` Gradle release under
`distributionUrl` rather than just the binaries (`bin`).
- **VisUI**: update `visUiVersion` in the properties file and VisUI version in the tag on the top of the 
[vis/README.md](../vis/README.md) file.
- **Ashley**: update `ashleyVersion` in the properties file and Ashely version in the tag on the top of the 
[ashley/README.md](../ashley/README.md) file.

All of the major dependencies updates should be added to the [changelog](../CHANGELOG.md).

### Versioning and uploading

#### Stable release

- Create a new issue on GitHub. Include the number of the issue in commit messages of all commits related to the release.
Apply `dev` label and milestone corresponding to the LibGDX version. An example can be found
[here](https://github.com/libktx/ktx/issues/191).
- Change `libVersion` setting in the [`version.txt`](../version.txt). KTX uses the same versioning schema as LibGDX
(mimicking the LibGDX version that it was compiled against) with a suffix depending on the version status.
- Create a pull request from the `develop` branch to the `master` branch. Review and merge the changes to the `master`
branch.
- Checkout the `master` branch. Fetch the latest changes.
- Run `gradle build install uploadArchives closeAndPromoteRepository` to push artifacts to both _Maven Local_ and
_Maven Central_. Note that the Maven plugin has its issues and you might need to run `gradle promoteRepository` after
the previous task sequence (if it fails on the `closeAndPromoteRepository` task).
- Run `gradle distZip` to prepare an archive with KTX sources, compiled binary and documentation.
- Upload the archive to [releases](https://github.com/libktx/ktx/releases) section. The tag should be made from the
`master` branch and its name should match the released version. Name of the release should match `KTX $libVersion`.
Add a short release summary and copy the latest [changelog](../CHANGELOG.md) entries to the release description.
- If there are any known issues with the previous or current versions, please attach additional _Known issues:_ section
with the following labels:
  - **[BUG]** - a known bug in the release that is or will be fixed in the following versions.
  - **[INCOMPATIBILITY]** - incompatibility with one of the previously supported or currently released versions of one
  of the major dependencies.
  - **[REMOVAL]** - temporary or permanent removal of a major feature (e.g. disabling a module for a single release).
- Checkout the `develop` branch.
- Change `libVersion` setting in the [`version.txt`](../version.txt) to the next snapshot release. The name should
match the used LibGDX version followed by the `-SNAPSHOT` suffix.

#### Snapshot release

- Make sure that the [`version.txt`](../version.txt) ends with the `-SNAPSHOT` suffix and matches the LibGDX version
that the library was compiled against.
- Run `gradle build install uploadSnapshot` to push artifacts to both _Maven Local_ and _Sonatype_ snapshots repository.
This task will do nothing if the current [version](../version.txt) is not a snapshot to avoid accidentally pushing
a stable release.

Note that snapshots are automatically uploaded to Maven Central (Sonatype) snapshots repository after pushing
to the `develop` branch.

#### Automated tasks

Tasks automated with [GitHub actions](https://github.com/libktx/ktx/actions):

* [build](workflows/build.yml) - compiles and tests all KTX modules. Triggered by pushing and setting up pull requests
to `master` and `develop` branches.
* [upload-snapshot](workflows/upload-snapshot.yml) - compiles all KTX modules and uploads a new snapshot release.
Triggered by pushing to the `develop` branch.
* [publish-documentation](workflows/publish-documentation.yml) - builds and replaces the Dokka documentation published
to [the official website](https://libktx.github.io/ktx/). Triggered by pushing to the `master` branch, which is
generally only done before stable releases.
