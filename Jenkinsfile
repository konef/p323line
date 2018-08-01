node {
   stage('Preparation (Checking out)') {
       checkout([$class: 'GitSCM', branches: [[name: '*/ypapkou']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p323line/']]])

   }
   
   stage('Build code') { 
       withMaven(maven: 'mavenLocal')
       {
           sh "mvn package -f helloworld-ws/pom.xml"
       }
   }
   
   stage('Testing') {
       withMaven(maven: 'mavenLocal')
       {
           sh "mvn pre-integration-test -f helloworld-ws"
           sh "mvn integration-test -f helloworld-ws"
           sh "mvn post-integration-test -f helloworld-ws"
       }
   }
}
