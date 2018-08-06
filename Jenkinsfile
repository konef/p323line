def push() {
    sh 'curl -v --user "myuser:pass" --upload-file pipeline-apatapniou-${BUILD_NUMBER}.tar.gz http://epbyminw2470/nexus/repository/Pipeline/pipe-task11/${BUILD_NUMBER}/'
  }
def pull(){
  sh "wget --user=myuser --password=pass http://epbyminw2470/nexus/repository/Pipeline/pipe-task11/${BUILD_NUMBER}/pipeline-apatapniou-${BUILD_NUMBER}.tar.gz"
}
node ("${SLAVE}"){
  try {
    stage ('Checkout'){
      checkout([$class: 'GitSCM', branches: [[name: '*/apatapniou']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p323line']]])
    }
  }
  catch (all)
  {
    mail bcc: '', body: 'HEY! Whats wrong with you ?!! ERROR WITH CHECKOUT !!', cc: '', from: '', replyTo: '', subject: 'Jenkins', to: 'apotapnyov@yandex.by'
  }
  try {
    stage('Build'){
      withMaven(maven: 'mavenLocal') {
        sh "mvn -f ./helloworld-ws/pom.xml package"
      }
    }
  }
  catch (all)
  {
    mail bcc: '', body: 'HEY! Whats wrong with you ?!! ERROR WITH BUILD !!', cc: '', from: '', replyTo: '', subject: 'Jenkins', to: 'apotapnyov@yandex.by'
  }
  try {
    stage ('Testing')
            {
              withMaven(maven: 'Maven') {
                parallel (
                        "pre-integration-test":  {
                          sh "mvn -f ./helloworld-ws/pom.xml pre-integration-test"
                          sleep(30)
                        },
                        "integration-test": {
                          sh "mvn -f ./helloworld-ws/pom.xml integration-test"
                          sleep(60)

                        },
                        "post-integration-test": {
                          sh "mvn -f ./helloworld-ws/pom.xml post-integration-test"
                        }
                )
              }
            }
  }
  catch (all)
  {
    mail bcc: '', body: 'HEY! Whats wrong with you ?!! ERROR WITH TESTING BUILD_NUMBER: ${BUILD_NUMBER}!!', cc: '', from: '', replyTo: '', subject: 'Jenkins', to: 'apotapnyov@yandex.by'
  }
  try {
    stage ('Triggering job and fetching artefact after finishing'){
      build job: 'MNTLAB-apatapniou-child1-build-job', parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: "apatapniou"]]
      step([  $class: 'CopyArtifact',
              filter: '*.tar.gz',
              fingerprintArtifacts: true,
              projectName: 'MNTLAB-apatapniou-child1-build-job',
              selector: lastSuccessful()
      ])
    }
  }
  catch (all)
  {
    mail bcc: '', body: 'HEY! Whats wrong with you ?!! ERROR WITH TRIGGERING BUILD_NUMBER: ${BUILD_NUMBER} !!', cc: '', from: '', replyTo: '', subject: 'Jenkins', to: 'apotapnyov@yandex.by'
  }
    try{
      stage ('Packaging and Publishing results'){
        sh "tar -xzvf apatapniou_dsl_script.tar.gz"
        sh "cp helloworld-ws/target/helloworld-ws.war ."
        sh "tar -czf pipeline-apatapniou-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy helloworld-ws.war"
        archiveArtifacts 'pipeline-apatapniou-${BUILD_NUMBER}.tar.gz'
        push()
      }
}
        catch (all)
    {
      mail bcc: '', body: 'HEY! Whats wrong with you ?!! ERROR WITH PACKAGING AND PUBLISING !!', cc: '', from: '', replyTo: '', subject: 'Jenkins', to: 'apotapnyov@yandex.by'
    }
    try {
      stage ('Asking for manual approval')
              {
                script {
                  timeout(time: 1, unit: 'MINUTES') {
                    input(id: "Deploy Gate", message: "Deploy ?", ok: 'Deploy')
                  }
                }
              }
    }
    catch (all)
    {
      mail bcc: '', body: 'HEY! Whats wrong with you ?!! ERROR WITH ASKING MANUAL APPROVAL !!', cc: '', from: '', replyTo: '', subject: 'Jenkins', to: 'apotapnyov@yandex.by'
    }

      stage ('Deployment')
              {
                sh "rm -rf pipeline-apatapniou-${BUILD_NUMBER}.tar.gz"
                pull()
                //sh "mv pipeline-apatapniou-${BUILD_NUMBER}.tar.gz artifacts/"
                sh "tar -xzvf pipeline-apatapniou-${BUILD_NUMBER}.tar.gz"
                //sh "scp -P2200 helloworld-ws.war root@epbyminw2470:/opt/tomcat/webapps && rm -rf helloworld-ws.war"
                sh "rm -rf pipeline-apatapniou-${BUILD_NUMBER}.tar.gz"
                sh "scp helloworld-ws.war root@EPBYMINW2470:/opt/tomcat/webapps/"
                //sh "ssh root@192.168.100.120 'cd /opt/tomcat/webapps/ && tar xzf pipeline-apapniou-${BUILD_NUMBER}.tar.gz && rm -rf pipeline-apatapniou-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy'"  
              }
   
    mail bcc: '', body: 'SUCCESS! YOU ARE GENIOUS! ', cc: '', from: '', replyTo: '', subject: 'Jenkins', to: 'apotapnyov@yandex.by'

  }
