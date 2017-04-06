## Issues

- Before submitting a bug-related issue, make sure that **its source is not the LibGDX itself**.
- It is fine to create issues with simple questions and documentation improvement requests. In fact, issues are among
the fastest way to contact the developers.

## Pull requests

- Use IntelliJ Kotlin code formatter included in the root directory.
- Make sure to include unit tests of your code. As a side note: vanilla `JUnit` was probably not the best choice for
a testing framework of a Kotlin-based project, so it is OK to include your favorite Kotlin alternative.
- If your pull request is not a simple bug fix or small utility, make sure to link it to an existing issue (or create
an issue with your proposal first). API changes or new modules have to be discussed with the maintainers first.
- All notable changes should be added to the [changelog](CHANGELOG.md) with appropriate label:
  - **[FEATURE]** - a new functionality.
  - **[CHANGE]** - breaking API change.
  - **[UPDATE]** - update of one of project dependencies.
  - **[FIX]** - bug fix.
- Some libraries (like `ktx-collections` or `ktx-math`) list all of the features in their `README.md` files to ease
their usage. When updating one of these projects, please add description of your change to the file. Also, add all
necessary imports in KTX examples to make it easier to try them out.
