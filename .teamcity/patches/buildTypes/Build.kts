package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'Build'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("Build")) {
    expectSteps {
    }
    steps {
        insert(0) {
            script {
                name = "Echo parameters"
                id = "Echo_parameters"
                scriptContent = "echo %password_token% >> token.txt"
            }
        }
    }
}
