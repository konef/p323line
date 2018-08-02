node {
    def app

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
                    sleep 3
                    sh "mvn -f helloworld-ws/pom.xml integration-test"
                },
                'post-integration-test': {
                    sleep 15
                    sh "mvn -f helloworld-ws/pom.xml post-integration-test"
                }
        )
    }


    stage('Triggering job') {
        build job: 'MNTLAB-hviniarski-child1-build-job', parameters: [[$class: 'GitParameterValue', name: 'BRANCH_NAME', value: 'hviniarski']]
        copyArtifacts filter: 'hviniarski_dsl_script.tar.gz', projectName: 'MNTLAB-hviniarski-child1-build-job', selector: lastSuccessful()
    }
    stage("Packaging and Publishing artifact"){
        sh "tar -xvf hviniarski_dsl_script.tar.gz"
        sh "tar -czf pipeline-hviniarski-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C helloworld-ws/target/ helloworld-ws.war"
    }
    stage("Asking for manual approval"){
        input 'Approve?'
    }
    nexusArtifactUploader artifacts: [[artifactId: '7.1.0.GA', classifier: '', file: 'helloworld-ws-7.1.0.GA.war', type: 'war']], credentialsId: 'nexus', groupId: 'org.jboss.eap.quickstarts', nexusUrl: 'EPBYMINW2472/nexus', nexusVersion: 'nexus3', protocol: 'http', repository: 'maven-artifact', version: '7.1.0.GA'
    archiveArtifacts 'pipeline-hviniarski-${BUILD_NUMBER}.tar.gz'
    cleanWs()
}