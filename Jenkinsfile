def push() {
 nexusArtifactUploader artifacts: [[artifactId: 'pipeline-apatapniou', classifier: '', file: 'pipeline-apatapniou-${BUILD_NUMBER}.tar.gz', type: 'tar.gz']], credentialsId: 'd94be367-3162-4284-9eac-fd7a3ce92a42', groupId: 'pipe-task11', nexusUrl: 'epbyminw2470/nexus', nexusVersion: 'nexus3', protocol: 'http', repository: 'Pipeline', version: '${BUILD_NUMBER}'
}
def pull(){
  sh "wget --user=myuser --password=pass http://epbyminw2470/nexus/repository/Pipeline/pipe-task11/pipeline-apatapniou/${BUILD_NUMBER}/pipeline-apatapniou-${BUILD_NUMBER}.tar.gz"
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
                          //sleep(10)
                        },
                        "integration-test": {
                          sh "mvn -f ./helloworld-ws/pom.xml integration-test"
                          //sleep(30)

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

      stage ('Packaging and Publishing results'){
        sh "tar -xzvf apatapniou_dsl_script.tar.gz"
        sh "cp helloworld-ws/target/helloworld-ws.war ."
        sh "tar -czf pipeline-apatapniou-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy helloworld-ws.war"
        archiveArtifacts 'pipeline-apatapniou-${BUILD_NUMBER}.tar.gz'
        push()
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
    try {
      stage ('Deployment')
              {
                sh "rm -rf pipeline-apatapniou-${BUILD_NUMBER}.tar.gz"
                pull()
                //sh "mv pipeline-apatapniou-${BUILD_NUMBER}.tar.gz artifacts/"
                sh "tar -xzvf pipeline-apatapniou-${BUILD_NUMBER}.tar.gz"
                sh "scp -P2200 helloworld-ws.war root@127.0.0.1:/opt/tomcat/webapps && rm -rf helloworld-ws.war"
                sh "rm -rf pipeline-apatapniou-${BUILD_NUMBER}.tar.gz"
                }
    }
    catch (all)
    {
      mail bcc: '', body: 'HEY! Whats wrong with you ?!! ERROR WITH DEPLOYMENT !!', cc: '', from: '', replyTo: '', subject: 'Jenkins', to: 'apotapnyov@yandex.by'
    }
    mail bcc: '', body: 'SUCCESS! YOU ARE GENIOUS! ', cc: '', from: '', replyTo: '', subject: 'Jenkins', to: 'apotapnyov@yandex.by'

  }
