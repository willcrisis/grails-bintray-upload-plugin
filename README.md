grails-bintray-upload-plugin
============================

uses the release plugin to upload to bintray provided certain conditions are met:

* Using java 7 (you cannot upload to bintray with 6 or below due to a java bug)
* Not a SNAPSHOT (bintray does not support SNAPSHOTs)
* Doesn't already exist
* Bintray url is in the correct form, exits with error if condition not met regardless of `-failOnConditionMet` value

Install
-------
Plugin will most likely be used during the build phase

```groovy
//BuildConfig.groovy
repositories {
    ...
    mavenRepo "https://dl.bintray.com/willcrisis/plugins/"
}
plugins {
    build(":bintray-upload:0.2") {
      export = false
    }
}
```

The release plugin must be installed as well

Usage
-----
Configure everything as you would with the release plugin, but instead of calling `maven-deploy` or `publish-plugin`,
call `upload-to-bintray`.  By default, if conditions are not met, the script throws an exception.  You can change the 
flag `-failOnBadCondition` to `true` to have it just log the conditions that were not met and not exit with an error.

You can set your Bintray's username and API key in environment variables named `BINTRAY_USERNAME` and `BINTRAY_PASSWORD`,
or override it in your `BuildConfig.groovy` or `settings.groovy` file.
