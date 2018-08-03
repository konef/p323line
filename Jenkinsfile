properties([
  parameters([
    string(name: 'student', defaultValue: 'ypapkou', description: 'Branch name.', )
   ])
])
node("EPBYMINW1766") {
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
           parallel (
               "Pre-integration test" : {
               sh "mvn pre-integration-test -f helloworld-ws"
               },
               "Integration test" : {
                   sleep 15
                   sh "mvn integration-test -f helloworld-ws"
               },
               "Post-integration test" : {
                   sleep 30
                   sh "mvn post-integration-test -f helloworld-ws"
               })
       }
   }
   
   stage('Triggering job and fetching artefact after finishing') {
       build job: 'MNTLAB-ypapkou-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: "$student")]
       copyArtifacts filter: '${student}_dsl_script.tar.gz, output.txt', fingerprintArtifacts: true, projectName: 'MNTLAB-ypapkou-child1-build-job', selector: lastSuccessful()
   }
   
   stage('Packaging and Publishing results') {
       sh 'tar -zxvf ${student}_dsl_script.tar.gz'
       sh 'tar -zcvf pipeline-$student-$BUILD_NUMBER.tar.gz jobs.groovy Jenkinsfile -C helloworld-ws/target/ helloworld-ws.war'
       archiveArtifacts 'pipeline-$student-$BUILD_NUMBER.tar.gz'
       sh 'GROOVY_HOME=/home/student/Downloads/groovy-2.5.1/bin/; PATH=$PATH:$GROOVY_HOME; groovy push.groovy'
   }
   
   stage('Asking for manual approval') {
       timeout(time: 1, unit: 'MINUTES') {
           input "This is a stage before deployment to production tomcat. Are sure to proceed?"
       }
   }
   
   stage('Deployment'){
       env.art_name="pipeline-${student}-${BUILD_NUMBER}.tar.gz"
       sh '[ -d tmp ] || mkdir tmp; echo $art_name; cd tmp; GROOVY_HOME=/home/student/Downloads/groovy-2.5.1/bin/; PATH=$PATH:$GROOVY_HOME; groovy ../pull.groovy'
       sh 'cd tmp; tar -zxvf pipeline-$student-$BUILD_NUMBER.tar.gz'
       sh 'cd tmp; scp -P 2201 helloworld-ws.war vagrant@epbyminw1766:/usr/local/tomcat/apache-tomcat-8.5.32/webapps/'
   }
}

