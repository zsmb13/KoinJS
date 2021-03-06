# KoinJS  [![Build Status](https://travis-ci.org/zsmb13/KoinJS.svg?branch=master)](https://travis-ci.org/zsmb13/KoinJS)

This is a fork of the [Ekito/koin](https://github.com/Ekito/koin) project that targets JavaScript. 

### Installation

KoinJS is available from jcenter, make sure you have it in your repositories list:

```groovy
repositories {
    jcenter()
}
```

Then just add the dependency:

```groovy
dependencies {
    compile "co.zsmb:koinjs:0.9.1"
}
```

### Documentation

Please refer to the original library's [readme](https://github.com/Ekito/koin/blob/5e9896e8af64adad6540e686bf5e3f852f8ae9ea/README.md), wiki / website for documentation.

### Tests

Tests have been ported from the original library, and they use Kotlin-test and mocha. You can run them with the `gradlew test` command.
 
Alternatively, you can in IntelliJ if you have the NodeJS plugin installed:

- Create a new Mocha run configuration
- Use `...\KoinJS\node_modules\mocha` as the Mocha package
- Set `...\KoinJS\build\classes\test` as the Test directory, make sure to include subdirectories
- Run tests by executing the run config
