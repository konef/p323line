node("${SLAVE}") {

    try {
        stage('Clone repository')
        git branch: 'hviniarski', url: 'https://github.com/MNT-Lab/p323line'


        stage('Build') {
            withMaven(maven: 'Maven3',) {
                // Run the maven build
                sh "mvn -f ./helloworld-ws/pom.xml package"
            }
        }

        stage("Testing")
        withMaven(maven: 'Maven3') {
            parallel(
                    'pre-integration-test': {
                        sh "mvn -f helloworld-ws/pom.xml pre-integration-test"
                    },
                    'integration-test': {
                        sleep 15
                        sh "mvn -f helloworld-ws/pom.xml integration-test"
                    },
                    'post-integration-test': {
                        sleep 30
                        sh "mvn -f helloworld-ws/pom.xml post-integration-test"
                    }
            )
        }


        stage('Triggering job') {
            build job: 'MNTLAB-hviniarski-child1-build-job', parameters: [[$class: 'GitParameterValue', name: 'BRANCH_NAME', value: 'hviniarski']]
            copyArtifacts filter: 'hviniarski_dsl_script.tar.gz', projectName: 'MNTLAB-hviniarski-child1-build-job', selector: lastSuccessful()
        }
        stage("Packaging and Publishing artifact") {
            sh "tar -xvf hviniarski_dsl_script.tar.gz"
            sh "tar -czf pipeline-hviniarski-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C helloworld-ws/target/ helloworld-ws.war"
            sh '''
        export GROOVY_HOME=/home/student/groovy-2.5.1
        export PATH=$PATH:$GROOVY_HOME/bin
        groovy push_pull.groovy push
        '''
        }
        stage("Asking for manual approval") {
            input 'Approve?'
        }
        stage("Deployment"){
            sh "groovy ./push_pull.groovy pull"
            sh '''
        export GROOVY_HOME=/home/student/groovy-2.5.1
        export PATH=$PATH:$GROOVY_HOME/bin
        groovy push_pull.groovy pull
        '''
        }
        archiveArtifacts 'pipeline-hviniarski-${BUILD_NUMBER}.tar.gz'
        cleanWs()
        currentBuild.result = 'SUCCESS'
    }
    catch (err) {
        currentBuild.result = 'FAILURE'
    }
    finally {
        mail bcc: '', body: "${env.BUILD_URL} has resulted in ${currentBuild.result}", cc: '', from: '', replyTo: '', subject: "Status of pipeline: ${currentBuild.fullDisplayName}", to: 'glebko123@gmail.com'
    }
}