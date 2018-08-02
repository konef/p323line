properties([
  parameters([
    string(name: 'student', defaultValue: 'ypapkou', description: 'Branch name.', )
   ])
])
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
   
   /*stage('Testing') {
       withMaven(maven: 'mavenLocal')
       {
           parallel ("Pre-integration test" : {sh "mvn pre-integration-test -f helloworld-ws"},
           "Integration test" : {sh "mvn integration-test -f helloworld-ws"},
           "Post-integration test" : {sh "mvn post-integration-test -f helloworld-ws"})
       }
   }*/
   
   stage('Triggering job and fetching artefact after finishing') {
       build job: 'MNTLAB-ypapkou-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: "$student")]
       copyArtifacts filter: '${student}_dsl_script.tar.gz, output.txt', fingerprintArtifacts: true, projectName: 'MNTLAB-ypapkou-child1-build-job', selector: lastSuccessful()
   }
   
   stage('Packaging and Publishing results') {
       sh 'tar -zxvf ${student}_dsl_script.tar.gz'
       sh 'tar -zcvf pipeline-$student-$BUILD_NUMBER.tar.gz jobs.groovy Jenkinsfile -C helloworld-ws/target/ helloworld-ws.war'
       archiveArtifacts 'pipeline-$student-$BUILD_NUMBER.tar.gz'
       sh 'GROOVY_HOME=/root/.jenkins/tools/hudson.plugins.groovy.GroovyInstallation/groovy_interpreter/bin; PATH=$PATH:$GROOVY_HOME; groovy push.groovy'
   }
   
   stage('Asking for manual approval') {
       input "This is a stage before deployment to production tomcat. Are sure to proceed?"
   }
   
   stage('Deployment'){
       
   }
}

