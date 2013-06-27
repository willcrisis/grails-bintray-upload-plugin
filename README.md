grails-bintray-plugin
=====================

uses the release plugin to upload to bintray provided certain conditions are met:

* Using java 7 (you cannot upload to bintray with 6 or below due to a java bug), exits as error if condition not met
* Not a SNAPSHOT (bintray does not support SNAPSHOTs), exits with no error if condition not met
* Doesn't already exist, exits with no error if condition not met
* Bintray url is in the correct form, exits with error if condition not met

Install
-------
Plugin will most likely be used during the build phase

```groovy
//BuildConfig.groovy

plugins {
    build(":bintray:<version>") {
      export = false
    }
}
```

Usage
-----
Configure everything as you would with the release plugin, but instead of calling `maven-deploy` or `publish-plugin`,
call `upload-to-bintray`.  When conditions are not met, script exits with or without errors based on accommodating 
continuous integration.
If the application is a SNAPSHOT, or has already been deployed, the script will exit silently indicating why it skipped
uploading to bintray.  If the wrong 
version of java is being used or the bintray repo url is not correct, an error will occurr.
