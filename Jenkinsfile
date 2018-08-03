properties([
  parameters([
    string(name: 'student', defaultValue: 'ypapkou', description: 'Branch name.', )
   ])
])
node("EPBYMINW1766") {
   stage('Preparation (Checking out)') {
       try {
           checkout([$class: 'GitSCM', branches: [[name: '*/ypapkou']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p323line/']]])
       }
       catch (exc) {
           mail bcc: '', body: "\"${STAGE_NAME}\" stage is failed. Build number is $BUILD_NUMBER.", cc: '', from: '', replyTo: '', subject: "INFO:$STAGE_NAME, BUILD:$BUILD_NUMBER", to: 'psqsaxxsldvr@gmail.com'
           throw exc
       }
       

   }
   
   stage('Build code') {
       try {
           withMaven(maven: 'mavenLocal')
           {
               sh "mvn package -f helloworld-ws/pom.xml"
           }
       }
       catch (exc) {
           mail bcc: '', body: "\"${STAGE_NAME}\" stage is failed. Build number is $BUILD_NUMBER.", cc: '', from: '', replyTo: '', subject: "INFO:$STAGE_NAME, BUILD:$BUILD_NUMBER", to: 'psqsaxxsldvr@gmail.com'
           throw exc
       }
   }
   
   stage('Testing') {
       try {
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
       catch (exc) {
           mail bcc: '', body: "\"${STAGE_NAME}\" stage is failed. Build number is $BUILD_NUMBER.", cc: '', from: '', replyTo: '', subject: "INFO:$STAGE_NAME, BUILD:$BUILD_NUMBER", to: 'psqsaxxsldvr@gmail.com'
           throw exc
       }
   }
   
   stage('Triggering job and fetching artefact after finishing') {
       try {
           build job: 'MNTLAB-ypapkou-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: "$student")]
           copyArtifacts filter: '${student}_dsl_script.tar.gz, output.txt', fingerprintArtifacts: true, projectName: 'MNTLAB-ypapkou-child1-build-job', selector: lastSuccessful()
       }
       catch (exc) {
           mail bcc: '', body: "\"${STAGE_NAME}\" stage is failed. Build number is $BUILD_NUMBER.", cc: '', from: '', replyTo: '', subject: "INFO:$STAGE_NAME, BUILD:$BUILD_NUMBER", to: 'psqsaxxsldvr@gmail.com'
           throw exc
       }
   }
   
   stage('Packaging and Publishing results') {
       try {
           sh 'tar -zxvf ${student}_dsl_script.tar.gz'
           sh 'tar -zcvf pipeline-$student-$BUILD_NUMBER.tar.gz jobs.groovy Jenkinsfile -C helloworld-ws/target/ helloworld-ws.war'
           archiveArtifacts 'pipeline-$student-$BUILD_NUMBER.tar.gz'
           sh 'GROOVY_HOME=/home/student/Downloads/groovy-2.5.1/bin/; PATH=$PATH:$GROOVY_HOME; groovy push.groovy'
       }
       catch (exc) {
           mail bcc: '', body: "\"${STAGE_NAME}\" stage is failed. Build number is $BUILD_NUMBER.", cc: '', from: '', replyTo: '', subject: "INFO:$STAGE_NAME, BUILD:$BUILD_NUMBER", to: 'psqsaxxsldvr@gmail.com'
           throw exc
       }
   }
   
   stage('Asking for manual approval') {
       try {
           timeout(time: 1, unit: 'MINUTES') {
               input "This is a stage before deployment to production tomcat. Are sure to proceed?"
           }
       }
       catch (exc) {
           echo "No reply!"
           currentBuild.result = 'FAILURE'
           mail bcc: '', body: "\"${STAGE_NAME}\" stage is failed. Build number is $BUILD_NUMBER.", cc: '', from: '', replyTo: '', subject: "INFO:$STAGE_NAME, BUILD:$BUILD_NUMBER", to: 'psqsaxxsldvr@gmail.com'
           throw exc
       }
   }
   
   stage('Deployment') {
       try {
           env.art_name="pipeline-${student}-${BUILD_NUMBER}.tar.gz"
           sh '[ -d tmp ] || mkdir tmp; echo $art_name; cd tmp; GROOVY_HOME=/home/student/Downloads/groovy-2.5.1/bin/; PATH=$PATH:$GROOVY_HOME; groovy ../pull.groovy'
           sh 'cd tmp; tar -zxvf pipeline-$student-$BUILD_NUMBER.tar.gz'
           sh 'cd tmp; scp -P 2201 helloworld-ws.war vagrant@epbyminw1766:/usr/local/tomcat/apache-tomcat-8.5.32/webapps/'
           mail bcc: '', body: "\"${STAGE_NAME}\" stage has passed. Build number is $BUILD_NUMBER.", cc: '', from: '', replyTo: '', subject: "ATTENTION:$STAGE_NAME, BUILD:$BUILD_NUMBER", to: 'psqsaxxsldvr@gmail.com'
       }
       catch (exc) {
           mail bcc: '', body: "\"${STAGE_NAME}\" stage is failed. Build number is $BUILD_NUMBER.", cc: '', from: '', replyTo: '', subject: "WARNING:$STAGE_NAME, BUILD:$BUILD_NUMBER", to: 'psqsaxxsldvr@gmail.com'
           throw exc
       }
   }
}

