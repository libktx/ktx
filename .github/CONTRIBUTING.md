# Contribution guidelines

## Issues

- Before submitting a bug report, verify that it is not caused by the libGDX framework itself.
- Feel free to create issues or start discussions with feature requests and questions about the libraries.
Issues and discussions are among the fastest way to contact the developers.

## Pull requests

- The latest changes are always in the `develop` branch. `master` branch always matches the latest stable release. Make
sure to checkout `develop` branch before starting your work and set `develop` as the target branch before creating
a pull request.
- Include issue or pull request IDs in related commit messages. This makes it easier to locate more information about
the changes. For example:

> Kotlin version update. #250

- Format your code changes with the `gradle format` task. We are relying on automatic formatting performed with
[ktlint](https://ktlint.github.io). Run `gradle linterIdeSetup` to apply formatter changes to your IntelliJ project.
- Follow our file naming convention:
  - Files with a single class: file name should match class name. E.g. `FileLoader.kt`.
  - Others: files with extension methods, top-level functions, utilities or multiple classes should use camel case
  nouns starting with a lower-cased letter. Should generally be in plural form. E.g. `fileLoaders.kt`.
- Make sure to include unit tests of your code. Test names should use the `` `backtick method name` `` syntax.
JUnit and [Spek](http://spekframework.org/) can be used to write tests, although JUnit is encouraged for its commonness.
Use [Mockito-Kotlin](https://github.com/nhaarman/mockito-kotlin) for mocking.
- In case of a larger pull request, make sure to link it to an existing issue or create a corresponding issue first.
Major API changes or new modules should be discussed with the maintainers. Skipping the issue will not get your pull
request rejected outright, but note that major changes without approval might require a rewrite.
- All notable changes should be added to the [changelog](../CHANGELOG.md) with an appropriate label:
  - **[FEATURE]** - a new functionality.
  - **[CHANGE]** - an API breaking change.
  - **[UPDATE]** - an update of one of the major dependencies.
  - **[FIX]** - a bug fix.
  - **[MISC]** - other changes (e.g. related to documentation or the project itself).
- Most libraries list _all_ features in the `README.md` files to ease their usage. When adding a new feature,
please check the  `README.md` file of the module, and add description of your change when appropriate. Any major feature
should include a usage example in the module guide. Make sure to add all the necessary imports in the usage examples
in `README.md` files to make it easier to try them out.

## Donations

<a href="https://opencollective.com/ktx">
  <img alt="Contribute on OpenCollective" src="https://opencollective.com/ktx/tiers/supporter.svg?avatarHeight=60&width=600" />
</a>

## Working from sources

```bash
git clone https://github.com/libktx/ktx.git
cd ktx
git checkout develop
```

### Gradle

The project itself is managed by [Gradle](http://gradle.org/). Gradle wrapper is included, but you can use a local
Gradle installation - scripts should be compatible with Gradle `7.+`. Gradle projects are handled out of the box by
IntelliJ, so **KTX** should be easy to import.

Some useful Gradle tasks include:

- `build install` - builds the libraries archives and pushes them to _Maven Local_. Useful for local tests.
- `format` - formats all Kotlin source files.
- `linterIdeSetup` - modifies local IntelliJ project setup for consistency with `ktlint` formatting.
- `check` - runs all tests in all projects.
- `clean` - removes the `build` directories, which forces rebuilds of the modules.
- `distZip` - prepares a zip archive with all jars in `build/distributions` folder. Useful for releases.
- `publish` - pushes the archives to _Maven Central_ or the snapshot repository, depending on the
[version](../version.txt). Requires complete `gradle.properties` with archive signing and _Sonatype_ logging data.
- `closeAndReleaseRepository` - closes and releases the Nexus repository. Should be run after `publish` in case of
a non-snapshot upload to _Maven Central_. Might fail at times on the release task; running `releaseRepository`
separately should fix the issue.

### Adding a new KTX module

- Create a folder matching module name in root of the repository. Modules should generally be named with a single word.
When multiple words are necessary, use a single dash (`-`) as the word separator.
- Add folder name to `settings.gradle.kts` file. This will also serve as the project identifier that you use in
`build.gradle` scripts and to run individual Gradle tasks (e.g. `gradle actors:test`).
- Create `src/main/kotlin` and `src/test/kotlin` directories in your module folder. They will be automatically marked
as source thanks to Gradle. You should also create package structure matching `ktx/your/module` in each source folder.
- Add `gradle.properties` file with the following properties:

```properties
projectName=ktx-your-module
projectDesc=Description of your module as it will appear in Maven Central.
```

- Add a `build.gradle.kts` file. It should contain dependencies specific to your module. If there are none, you can
leave it empty. By adding `import ktx.*` at the top of this file, you will be able to access the versions of major
dependencies of the modules as defined in the [`buildSrc`](../buildSrc) directory.
- Add a `README.md` file describing your module. Refer to other `README.md` files for examples. `README.md` files
can consist of the following sections:
  - _General description_ - in a single sentence, what problem does the module solve?
  - _Motivation_ - why was the module created?
  - _Guide_ - what features does the module provide? Does it require additional setup?
  - _Usage examples_ - how to use the module?
  - _Synergy_ - is the module complemented by any other **KTX** libraries?
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
  - build.gradle.kts
  - gradle.properties
  - README.md
```

- Include the module in the listing in the main [`README.md`](../README.md) file.

## Maintenance

The following sections are for the maintainers of the repository.

### Updating dependencies

Kotlin and plugin versions are stored in the [gradle.properties](../gradle.properties) file, while module dependencies
versions are stored with the [`Versions.kt`](../buildSrc/src/main/kotlin/ktx/Versions.kt) file. Snapshot releases
should keep all the dependencies (outside of testing scope) up-to-date. Major dependencies include:

- **libGDX**: update `gdxVersion` in the versions file and libGDX version in the tag on the top of the 
[README.md](../README.md) file. Note that updating libGDX also affects the **KTX** version and milestones, so make sure
to update the [version.txt](../version.txt) and [milestones](https://github.com/libktx/ktx/milestones) as well. After
the release, update GitHub project's `Custom properties`.
- **Kotlin**: update the `kotlinVersion` in the properties file and the Kotlin tag in the [README.md](../README.md).
After the release, update GitHub project's `Custom properties`.
- **Kotlin Coroutines**: update `kotlinCoroutinesVersion` in the versions file and the tag in the
`ktx-async` [README.md](../async/README.md).
- **Gradle**: run `gradle wrapper --distribution-type all` in the root project folder. Make sure that the
[Gradle wrapper properties file](../gradle/wrapper/gradle-wrapper.properties) points the `all` Gradle release under
`distributionUrl` rather than just the binaries (`bin`). Note that you do not have to install the corresponding Gradle
version locally: instead, you can update the version in `gradle-wrapper.properties` first, and then run `./gradlew wrapper`
to update the Gradle wrapper scripts and configuration files.
- **VisUI**: update `visUiVersion` in the versions file and VisUI version in the tag on the top of the 
[vis/README.md](../vis/README.md) file.
- **Ashley**: update `ashleyVersion` in the versions file and Ashley version in the tag on the top of the 
[ashley/README.md](../ashley/README.md) file.
- **Artemis-odb**: update `artemisOdbVersion` in the versions file and Artemis-odb version in the tag on the top of the
[artemis/README.md](../artemis/README.md) file.
- **gdxAI**: update `gdxAiVersion` in the versions file and gdxAI version in the tag on the top of the
[ai/README.md](../ai/README.md) file.

All the major dependencies updates should be added to the [changelog](../CHANGELOG.md).

### Versioning and uploading

#### Stable release

- Create a new issue on GitHub. Include the number of the issue in commit messages of all commits related to the release.
Apply `dev` label and milestone corresponding to the libGDX version. An example can be found
[here](https://github.com/libktx/ktx/issues/191).
- Change `libVersion` setting in the [`version.txt`](../version.txt). **KTX** uses the same versioning schema as libGDX
(mimicking the libGDX version that it was compiled against) with a suffix depending on the version status.
- Create a pull request from the `develop` branch to the `master` branch. Review and merge the changes to the `master`
branch.
- Checkout the `master` branch. Fetch the latest changes.
- Run `gradle build publish closeAndReleaseRepository` to push artifacts to _Maven Central_. Note that the Maven plugin
has its issues and might fail with an error, but usually the release will be successful. You can check if the staging
repository was properly close, promoted and released at [Nexus Repository Manager](https://oss.sonatype.org/).
- Run `gradle distZip` to prepare an archive with **KTX** sources, compiled binary and documentation.
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
match the used libGDX version followed by the `-SNAPSHOT` suffix.

#### Snapshot release

- Make sure the [`version.txt`](../version.txt) ends with the `-SNAPSHOT` suffix and matches the libGDX version
that the library was compiled against.
- Run `gradle build uploadSnapshot` to push artifacts to _Sonatype_ snapshots repository. This task will do nothing
if the current [version](../version.txt) is not a snapshot to avoid accidentally pushing a stable release.

Note that snapshots are automatically uploaded to Maven Central (OSS Sonatype)
[snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/io/github/libktx/) after pushing
to the `develop` branch.

#### Automated tasks

Tasks automated with [GitHub actions](https://github.com/libktx/ktx/actions):

* [build](workflows/build.yml) - compiles and tests all **KTX** modules. Triggered by pushing and setting up pull requests
to `master` and `develop` branches.
* [upload-snapshot](workflows/upload-snapshot.yml) - compiles all **KTX** modules and uploads a new snapshot release.
Triggered by pushing to the `develop` branch.
* [publish-documentation](workflows/publish-documentation.yml) - builds and replaces the Dokka documentation published
to [the official website](https://libktx.github.io/ktx/). Triggered by pushing to the `master` branch, which is
generally only done before stable releases.
- [publish-project-samples](workflows/publish-project-samples.yml) - generates sample projects with
[gdx-liftoff](https://github.com/tommyettinger/gdx-liftoff) based on the latest **KTX** version from the `master`
branch, and pushes them to the [ktx-sample-project](https://github.com/libktx/ktx-sample-project) and
[ktx-sample-web-project](https://github.com/libktx/ktx-sample-web-project) repositories. Triggered by creating new
release tags.
