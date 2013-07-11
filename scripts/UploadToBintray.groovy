import groovy.json.JsonSlurper

includeTargets << new File("${releasePluginDir}/scripts/_GrailsMaven.groovy")

String bintrayOrg
String bintrayRepo
String bintrayPackage
String version
String repoName
boolean conditionsMet = true
boolean failOnBadCondition = true
boolean artifactAlreadyExistsOrIsSnapshot = false

target(uploadToBintray: "uploads artifacts to bintray if conditions are met") {
    //noinspection GroovyAssignabilityCheck
    depends(parseArguments, checkConditions)
    failOnBadCondition = Boolean.valueOf(argsMap.failOnBadCondition ?: true)

    if(!conditionsMet && failOnBadCondition) {
        grailsConsole.error "One or more conditions have not been met"
        exit(8)
    }

    if(!conditionsMet && !failOnBadCondition) {
        grailsConsole.warn "One or more conditions were not met"
    }

    if (conditionsMet) {
        depends(mavenDeploy, publishBintrayArtifacts)
    }
}

target(checkConditions: "check whether or not we can upload an artifact to bintray") {
    depends(checkJavaVersion)

    if(conditionsMet) {
        depends(checkAndSetBintrayArgs)
    }

    if(conditionsMet) {
        depends(checkProjectVersion)
    }
}

target(checkAndSetBintrayArgs: "makes sure the repo url is in fact a bintray url") {
    depends(init) //from the maven plugin
    repoName = argsMap.repository ?: grailsSettings.config.grails.project.repos.default
    def repo = repoName ? distributionInfo.remoteRepos[repoName] : null
    if (!repo) {
        grailsConsole.error "No repository has been set"
        exit(2)
    }

    //set the url that will be used by the rest of the application
    url = repo?.args?.url
    if (!url) {
        grailsConsole.error "url has not been set for repo $repoName"
        exit(3)
    }

    //group 1 is the org / user name, 2 the repo, and 3 the package
    def bintrayMavenApiUrl = /https:\/\/api.bintray.com\/maven\/([^\/]+)\/([^\/]+)\/([^\/]+)$/
    def matcher = url =~ bintrayMavenApiUrl
    if (!matcher.matches()) {
        grailsConsole.error "the url $url must be in the form of $bintrayMavenApiUrl to upload to bintray"
        exit(4)
    }

    bintrayOrg = matcher.group(1)
    bintrayRepo = matcher.group(2)
    bintrayPackage = matcher.group(3)
}

target(checkProjectVersion: "check if the package was already deployed") {
    depends(checkAndSetBintrayArgs)

    if (isPluginProject) {
        if (!pluginSettings.basePluginDescriptor.filename) {
            grailsConsole.error "PluginDescripter not found to get version"
            exit 5
        }

        File file = new File(pluginSettings.basePluginDescriptor.filename)
        String descriptorContent = file.text

        def pattern = ~/def\s*version\s*=\s*"(.*)"/
        def matcher = (descriptorContent =~ pattern)

        if (matcher.size() > 0) {
            version = matcher[0][1]
        }
        else {
            grailsConsole.error "version not found in plugin"
            exit 6
        }
    }
    else {
        version = metadata.'app.version'
        if (!version) {
            grailsConsole.error "version not found in application"
            exit 6
        }
    }

    if (version.endsWith("SNAPSHOT")) {
        grailsConsole.info "you cannot deploy SNAPSHOTs to bintray, skipping upload"
        conditionsMet = false
    }

    if (!artifactAlreadyExistsOrIsSnapshot) {
        def json = new URL("https://api.bintray.com/packages/$bintrayOrg/$bintrayRepo/$bintrayPackage").text
        def slurper = new JsonSlurper()
        def versions = slurper.parseText(json).versions
        artifactAlreadyExistsOrIsSnapshot = versions.contains(version)

        if (artifactAlreadyExistsOrIsSnapshot) {
            grailsConsole.info "version $version has already been deployed to bintray"
            conditionsMet = false
        }
    }
}

target(checkJavaVersion: "checks the java version") {
    String javaVersion = System.getProperty("java.version")
    def m = javaVersion =~ /^1\.(\d+)/
    m.lookingAt()
    int majorVersion = m.group(1).toInteger()
    if (majorVersion < 7) {
        grailsConsole.warn "you can't upload to bintray unless you are using at least java 7"
        conditionsMet = false
    }
}

target(publishBintrayArtifacts: "publishes bintray artifacts") {
    def restClient = classLoader.loadClass("grails.plugins.rest.client.RestBuilder").newInstance()
    def projectConfig = grailsSettings.config.grails.project
    def username = projectConfig.repos."${repoName}".username
    def password = projectConfig.repos."${repoName}".password

    def url = "https://api.bintray" +
            ".com/content/$bintrayOrg/$bintrayRepo/$bintrayPackage/$version/publish"
    grailsConsole.info "publishing artifacts with POST to $url"

    def response = restClient.post(url) {
        auth username, password
    }

    if (response.getClass().getSimpleName() == "ErrorResponse") {
        grailsConsole.error "response returned with error status $response.status and body:\n $response.text"
        exit 7
    }

    grailsConsole.info "artifacts uploaded and published"
}

setDefaultTarget(uploadToBintray)