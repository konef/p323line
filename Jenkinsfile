node {
   stage('Preparation (Checking out)') {
       checkout([$class: 'GitSCM', branches: [[name: '*/ypapkou']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p323line/']]])

   }
   
   stage('Build') { 
       withMaven(maven: 'mavenLocal')
       {
           sh "mvn clean install -f helloworld-ws/pom.xml"
       }
   }
}
