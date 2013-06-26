grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.repos.bintray.url = "https://api.bintray.com/maven/upennlib/test/bintray"

grails.project.dependency.resolution = {
    inherits("global")
    repositories {
        grailsCentral()
    }

    plugins {
        build(":release:2.2.1",
                ":rest-client-builder:1.0.3",
                ":squeaky-clean:0.1") {
            export = false
        }
    }
}
