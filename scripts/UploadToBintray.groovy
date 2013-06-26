includeTargets << new File("${bintrayPluginDir}/scripts/_UploadToBintray.groovy")

target(main: "uploads artifacts to bintray if conditions are met") {
    depends(uploadToBintray)
}

setDefaultTarget(main)
