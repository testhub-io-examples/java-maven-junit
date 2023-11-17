import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.amazonEC2CloudImage
import jetbrains.buildServer.configs.kotlin.amazonEC2CloudProfile
import jetbrains.buildServer.configs.kotlin.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.matrix
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2023.11"

project {

    buildType(ThirdBuildFromTheTemplate)
    buildType(Build)
    buildType(SecondBuildFromTemplate)

    template(BuildConfigurationWithPrAndMatrix)

    features {
        amazonEC2CloudImage {
            id = "PROJECT_EXT_4"
            profileId = "amazon-1"
            agentPoolId = "-2"
            imagePriority = 5
            name = "Ubuntu with PowerShell Agent Image "
            vpcSubnetId = "subnet-0c23f411b0800b216"
            keyPairName = "daria.krupkina"
            instanceType = "t2.medium"
            securityGroups = listOf("sg-072d8bfa0626ea2a6")
            source = Source("ami-0817025aa39c203c6")
        }
        amazonEC2CloudProfile {
            id = "amazon-1"
            name = "Amazon AWS EC2 Cloud Profile"
            serverURL = "http://10.128.93.57:8134"
            terminateIdleMinutes = 30
            region = AmazonEC2CloudProfile.Regions.EU_WEST_DUBLIN
            authType = accessKey {
                keyId = "credentialsJSON:a778a904-1b51-41c6-906b-81e57b5c7a7e"
                secretKey = "credentialsJSON:ec56aca9-5346-4c26-b964-49b3a9384fc9"
            }
        }
    }
}

object Build : BuildType({
    templates(BuildConfigurationWithPrAndMatrix)
    name = "Build"
})

object SecondBuildFromTemplate : BuildType({
    templates(BuildConfigurationWithPrAndMatrix)
    name = "SecondBuildFromTemplate"
})

object ThirdBuildFromTheTemplate : BuildType({
    templates(BuildConfigurationWithPrAndMatrix)
    name = "Third build from the template"

    features {
        matrix {
            id = "matrix"
            param("custom_param", listOf(
                value("param_5"),
                value("param_6")
            ))
            param("parameter_OS", listOf(
                value("OS_Linux"),
                value("OS_MacOS")
            ))
        }
    }
})

object BuildConfigurationWithPrAndMatrix : Template({
    name = "Build Configuration with PR and Matrix"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            id = "Maven2"
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
            id = "TRIGGER_2"
        }
    }

    features {
        perfmon {
            id = "perfmon"
        }
        pullRequests {
            id = "BUILD_EXT_3"
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            provider = github {
                authType = vcsRoot()
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
            }
        }
        commitStatusPublisher {
            id = "BUILD_EXT_6"
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = vcsRoot()
            }
        }
        matrix {
            id = "matrix"
            param("custom_param", listOf(
                value("param_1"),
                value("param_2")
            ))
        }
    }
})
