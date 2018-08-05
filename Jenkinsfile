def push_tar() {
    nexusArtifactUploader artifacts: [[artifactId: 'pipeline-akavaleu', classifier: '',
                                       file: 'pipeline-akavaleu-${BUILD_NUMBER}.tar.gz', type: 'tar.gz']],
            credentialsId: '1dc8df2b-fb7f-45ce-b268-a3953340ef26', groupId: 'pre-prod', nexusUrl: 'epbyminw2467.epam.com/nexus',
            nexusVersion: 'nexus3', protocol: 'http', repository: 'tar-deploy-artifacts', version: '${BUILD_NUMBER}'
}

def pull_tar() {
    sh 'wget --user=nexus-service-user --password=service http://epbyminw2467/nexus/repository/tar-deploy-artifacts/pre-prod/pipeline-akavaleu/${BUILD_NUMBER}/pipeline-akavaleu-${BUILD_NUMBER}.tar.gz'
}


node("${SLAVE}"){
    def giturl = 'https://github.com/MNT-Lab/p323line.git'

    stage('Clone sources') {
        checkout([$class: 'GitSCM', branches: [[name: '*/akavaleu']], doGenerateSubmoduleConfigurations: false,
                  extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: giturl]]])

    }

    stage('Maven build') {
        withMaven(jdk: 'java', maven: 'Maven_3_5_4') {
            sh 'mvn -f helloworld-ws/pom.xml package'
        }
    }

    stage('Testing') {
        withMaven(jdk: 'java', maven: 'Maven_3_5_4') {
            parallel(
                    pre_integration_test:{
                        sh 'mvn -f helloworld-ws/pom.xml package pre-integration-test'
                        sleep(20)
                    },
                    integration_test:{
                        sh 'mvn -f helloworld-ws/pom.xml package integration-test'
                        sleep(20)
                    },
                    post_integration_test:{
                        sh 'mvn -f helloworld-ws/pom.xml package post-integration-test'
                        sleep(20)
                    }
            )
        }
    }

    stage ('Trigger job, fetch artifact'){
        build job: 'MNTLAB-akavaleu-child1-build-job' ,
                parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: "akavaleu"]]
        copyArtifacts filter: 'akavaleu_dsl_script.tar.gz',
                fingerprintArtifacts: true, projectName: 'MNTLAB-akavaleu-child1-build-job', selector: lastSuccessful()
    }

    stage('Packaging and Publishing results'){
        sh 'tar -zxf akavaleu_dsl_script.tar.gz'
        sh 'cp -f helloworld-ws/target/helloworld-ws.war .'
        sh 'tar -czf pipeline-akavaleu-$BUILD_NUMBER.tar.gz helloworld-ws.war jobs.groovy Jenkinsfile'
        sleep(5)
        push_tar()
        archiveArtifacts 'pipeline-akavaleu-$BUILD_NUMBER.tar.gz'
    }

    stage ('Asking for manual approval')
            {
                script {
                    timeout(time: 1, unit: 'MINUTES') {
                        input(id: "Deploy Gate", message: "Deploy ?", ok: 'Deploy')
                    }
                }
            }

    stage ('Deployment')
            {
                pull_tar()
                sh 'tar -xzf $WORKSPACE/pipeline-akavaleu-$BUILD_NUMBER.tar.gz'
                sh 'scp $WORKSPACE/helloworld-ws.war root@10.0.0.70:/opt/tomcat/webapps/'
                sh 'ssh root@10.0.0.70 "rm -f /opt/backup/helloworld-ws.war"'
                sh 'ssh root@10.0.0.70 "cp /opt/tomcat/webapps/helloworld-ws.war /opt/backup/"'
            }
}
