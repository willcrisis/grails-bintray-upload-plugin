class BintrayGrailsPlugin {
    def version = "0.1"
    def grailsVersion = "2.0 > *"
    def title = "Bintray Plugin"
    def author = "Tommy Barker"
    def authorEmail = "mingus.karate@gmail.com"
    def description = "Uploads artifacts to bintray if appropriate conditions are met"
    def organization = [name: "University of Pennsylvania Libraries", url: "https://github.com/upenn-libraries"]
    def developers = [[name: "Tommy Barker", email: "mingus.karate@gmail.com"]]
    def issueManagement = [system: "GitHub", url: "https://github" +
            ".com/upenn-libraries/grails-bintray-upload-plugin/issues"]
    def documentation = "https://github.com/upenn-libraries/grails-bintray-upload-plugin"
    def scm = [url: "https://github.com/upenn-libraries/grails-bintray-upload-plugin"]
    def license = "ECL2"
}
