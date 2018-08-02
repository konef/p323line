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
           parallel ("Pre-integration test" : {sh "mvn pre-integration-test -f helloworld-ws"},
           "Integration test" : {sh "mvn integration-test -f helloworld-ws"},
           "Post-integration test" : {sh "mvn post-integration-test -f helloworld-ws"})
       }
   }
   
   stage('Triggering job and fetching artefact after finishing') {
       
   }
   
   stage('Packaging and Publishing results') {
       
   }
   
   stage('Asking for manual approval') {
       
   }
   
   stage('Deployment'){
       
   }
}
