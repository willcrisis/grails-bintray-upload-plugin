includeTargets << grailsScript("_GrailsInit")

target(uploadToBintray: "uploads artifacts to bintray if conditions are met") {
    //noinspection GroovyAssignabilityCheck
    depends(checkConditions, printStats, uploadArtifacts)
}

target(checkConditions: "check whether or not we can upload an artifact to bintray") {

}

target(printStats: "print basic stats before uploading artifacts") {

}

target(uploadArtifacts: "upload artifacts to bintray") {

}