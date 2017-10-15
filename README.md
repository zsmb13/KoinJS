# KoinJS

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
    compile "co.zsmb:koinjs:0.1.2"
}
```

### Tests

Tests have been ported from the original library, and they use Kotlin-test and mocha. You can run them with the `gradlew test` command.
 
Alternatively, you can in IntelliJ if you have the NodeJS plugin installed:

- Create a new Mocha run configuration
- Use `...\KoinJS\node_modules\mocha` as the Mocha package
- Set `...\KoinJS\build\classes\test` as the Test directory, make sure to include subdirectories
- Run tests by executing the run config
