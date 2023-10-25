import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.amazonEC2CloudImage
import jetbrains.buildServer.configs.kotlin.amazonEC2CloudProfile
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
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

version = "2023.05"

project {

    buildType(BuildFromMetaRunner)
    buildType(Build)

    features {
        amazonEC2CloudImage {
            id = "PROJECT_EXT_3"
            profileId = "amazon-1"
            agentPoolId = "-2"
            imagePriority = 5
            name = "Ubuntu Agent"
            vpcSubnetId = "subnet-0c23f411b0800b216"
            keyPairName = "daria.krupkina"
            instanceType = "t2.medium"
            securityGroups = listOf("sg-072d8bfa0626ea2a6")
            source = Source("ami-0817025aa39c203c6")
        }
        amazonEC2CloudImage {
            id = "PROJECT_EXT_5"
            profileId = "amazon-1"
            agentPoolId = "-2"
            imagePriority = 10
            name = "Ubuntu Agent (Highest Priority)"
            vpcSubnetId = "subnet-0c23f411b0800b216"
            keyPairName = "daria.krupkina"
            instanceType = "t2.medium"
            securityGroups = listOf("sg-072d8bfa0626ea2a6")
            source = Source("ami-0817025aa39c203c6")
        }
        amazonEC2CloudProfile {
            id = "amazon-1"
            name = "Cloud AWS Profile"
            serverURL = "http://10.128.93.57:8124"
            terminateIdleMinutes = 30
            region = AmazonEC2CloudProfile.Regions.EU_WEST_DUBLIN
            authType = accessKey {
                keyId = "credentialsJSON:c0beb179-a7a4-44f1-9f81-ffe1641fda6c"
                secretKey = "credentialsJSON:ec56aca9-5346-4c26-b964-49b3a9384fc9"
            }
        }
    }
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            id = "Maven2"
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        maven {
            name = "New build step (Edited)"
            id = "Maven_New_Step"
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})

object BuildFromMetaRunner : BuildType({
    name = "Build from Meta Runner"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        step {
            name = "Meta-runner Maven step"
            id = "JavaMavenJunit_Build"
            type = "JavaMavenJunit_Build"
            executionMode = BuildStep.ExecutionMode.DEFAULT
            param("teamcity.step.phase", "")
        }
        step {
            name = "The second Maven"
            id = "JavaMavenJunit_Build_New"
            type = "JavaMavenJunit_Build"
            enabled = false
            executionMode = BuildStep.ExecutionMode.DEFAULT
            param("teamcity.step.phase", "")
        }
        step {
            name = "New Maven step"
            id = "JavaMavenJunit_Build_1"
            type = "JavaMavenJunit_Build"
            executionMode = BuildStep.ExecutionMode.DEFAULT
            param("teamcity.step.phase", "")
        }
    }
})
