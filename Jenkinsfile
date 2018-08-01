node {
   stage('Preparation (Checking out)') {
       checkout([$class: 'GitSCM', branches: [[name: '*/ypapkou']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p323line/']]])

   }
   
   stage('Build') { 
       withMaven(maven: 'mavenLocal')
       {
           sh "mvn package -f helloworld-ws/pom.xml"
       }
   }
   
   stage('pre-integration-test') {
       withMaven(maven: 'mavenLocal')
       {
           sh "mvn pre-integration-test -f helloworld-ws"
       }
   }
   
   stage('integration-test') {
       withMaven(maven: 'mavenLocal')
       {
           sh "mvn integration-test -f helloworld-ws"
       }
   }
   
   stage('post-integration-test') {
       withMaven(maven: 'mavenLocal')
       {
           sh "mvn post-integration-test -f helloworld-ws"
       }
   }
}
