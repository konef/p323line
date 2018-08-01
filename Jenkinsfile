
node{

    tool name: 'mavenLocal', type: 'maven'
    tool name: 'java8', type: 'jdk'

    stage('Preparation') {
        deleteDir()
        //checkout([$class: 'GitSCM', branches: [[name: '*/aandryieuski']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p323line.git']]])
        git branch: 'aandryieuski', poll: false, url: 'https://github.com/MNT-Lab/p323line.git'
        echo "\u2705"
    }
    stage('Building code'){
        def mvn_version = 'mavenLocal'
        withEnv(["PATH+MAVEN=${tool mvn_version}/bin"]) {
            sh 'mvn -f helloworld-ws/pom.xml package'
            sh 'ls -la helloworld-ws/'
        }
    }
}

/*
 err = caughtError
 currentBuild.result = "FAILURE"
 String recipient = 'infra@lists.jenkins-ci.org'
 mail subject: "${env.JOB_NAME} (${env.BUILD_NUMBER}) failed",
         body: "It appears that ${env.BUILD_URL} is failing, somebody should do something about that",
           to: recipient,
      replyTo: recipient,
 from: 'noreply@ci.jenkins.io'
*/