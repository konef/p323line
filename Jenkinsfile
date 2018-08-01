node {
    stage ('Preparation (Checking out)') {
        checkout([$class: 'GitSCM', branches: [[name: 'mpiatliou']], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p323line.git']]])
    }
    stage ('Building code') {
        withMaven(maven: 'mavenLocal') {
            sh "mvn -f ./helloworld-ws/pom.xml package"
        }
    }
}
