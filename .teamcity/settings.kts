import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.amazonEC2CloudImage
import jetbrains.buildServer.configs.kotlin.amazonEC2CloudProfile
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.script
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

version = "2024.03"

project {

    buildType(Build)

    features {
        amazonEC2CloudImage {
            id = "PROJECT_EXT_5"
            profileId = "amazon-4"
            imagePriority = 5
            name = "Spot Fleet Ubuntu agent"
            source = SpotFleetConfig("""
                {
                    "IamFleetRole": "arn:aws:iam::913206223978:role/aws-ec2-spot-fleet-tagging-role",
                    "AllocationStrategy": "priceCapacityOptimized",
                    "TargetCapacity": 3,
                    "ValidFrom": "2024-05-03T09:06:36.000Z",
                    "ValidUntil": "2025-05-03T09:06:36.000Z",
                    "TerminateInstancesWithExpiration": true,
                    "Type": "request",
                    "TargetCapacityUnitType": "units",
                    "LaunchSpecifications": [
                        {
                            "ImageId": "ami-0817025aa39c203c6",
                            "KeyName": "daria.krupkina",
                            "BlockDeviceMappings": [
                                {
                                    "DeviceName": "/dev/sda1",
                                    "Ebs": {
                                        "DeleteOnTermination": true,
                                        "SnapshotId": "snap-08e52b439cb6eade3",
                                        "VolumeSize": 16,
                                        "VolumeType": "gp2",
                                        "Encrypted": false
                                    }
                                },
                                {
                                    "DeviceName": "/dev/sdb",
                                    "VirtualName": "ephemeral0",
                                    "Ebs": {}
                                },
                                {
                                    "DeviceName": "/dev/sdc",
                                    "VirtualName": "ephemeral1",
                                    "Ebs": {}
                                }
                            ],
                            "SubnetId": "subnet-0e8a4581403f50fbf",
                            "InstanceRequirements": {
                                "VCpuCount": {
                                    "Min": 1,
                                    "Max": 4
                                },
                                "MemoryMiB": {
                                    "Min": 0,
                                    "Max": 4096
                                }
                            },
                            "TagSpecifications": [
                                    {
                                        "ResourceType": "instance",
                                        "Tags": [
                                            {
                                                "Key": "Owner",
                                                "Value": "daria.krupkina@jetbrains.com"
                                            }
                                        ]
                                    }
                                ]
                        }
                    ],
                   "TagSpecifications": [
                            {
                                "ResourceType": "spot-fleet-request",
                                "Tags": [
                                    {
                                        "Key": "Owner",
                                        "Value": "daria.krupkina@jetbrains.com"
                                    }
                                ]
                            }        
                   ]
                }
            """.trimIndent())
        }
        amazonEC2CloudProfile {
            id = "amazon-4"
            name = "Cloud AWS EC2 Fleet"
            serverURL = "http://10.128.93.57:8211/"
            terminateIdleMinutes = 30
            region = AmazonEC2CloudProfile.Regions.EU_WEST_DUBLIN
            authType = instanceIAMRole()
        }
    }
}

object Build : BuildType({
    name = "Build"

    params {
        param("teamcity.agent.temporary.authorizationToken", "  ")
    }

    steps {
        maven {
            id = "Maven2"
            enabled = false
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        script {
            id = "simpleRunner"
            scriptContent = "echo %teamcity.agent.temporary.authorizationToken%"
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
