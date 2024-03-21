import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.remoteParameters.hashiCorpVaultParameter

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

    buildType(Build)
    params {
        text("text_parameter", "2")      
        password("password_token", "credentialsJSON:99e25682-7bac-41fa-b7df-1ca76cdf8720", readOnly = true)
    }
}

object Build : BuildType({
    name = "Build"

    artifactRules = "creds.txt"
    params {
        text("any_text_parameter", "value", label = "Any text", description = "Text parameter with any value", readOnly = true, allowEmpty = true)
        checkbox("checkbox_parameter", "", label = "Checkbox parameter", description = "Parameter with 2 options to choose from", display = ParameterDisplay.PROMPT,
                  checked = "true", unchecked = "false")
        password("password_parameter", "credentialsJSON:76c0ae23-92e2-4050-bbf4-133faef57d6f", label = "Password", description = "Password parameter", readOnly = true)
        text("agentNumber", "21", allowEmpty = true)
        text("not_empty_text_parameter", "", label = "Not empty text", description = "Not empty text parameter", display = ParameterDisplay.PROMPT, allowEmpty = false)
        text("regex_text_parameter", "a12", label = "Regex parameter", description = "Regex text parameter", display = ParameterDisplay.HIDDEN,
              regex = "a1*", validationMessage = "Regex validation failed!")
        select("select_parameter", "a1", label = "Selector", description = "Selector with multiple values allowed",
                allowMultiple = true, valueSeparator = ";",
                options = listOf("a1" to "1", "a2" to "2", "a5" to "5", "a10" to "10"))
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "Echo parameters"
            id = "Echo_parameters"
            scriptContent = "echo %password_token% >> creds.txt"
        }
    }
})
