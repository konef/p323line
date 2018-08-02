node {
    def giturl = 'https://github.com/MNT-Lab/p323line.git'

    stage('Clone sources') {
        checkout([$class: 'GitSCM', branches: [[name: '*/akavaleu']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: giturl]]])

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
                        sleep(10)
                    },
                    integration_test:{
                        sh 'mvn -f helloworld-ws/pom.xml package integration-test'
                        sleep(10)
                    },
                    post_integration_test:{
                        sh 'mvn -f helloworld-ws/pom.xml package post-integration-test'
                        sleep(10)
                    }
            )
        }
    }

    stage ('Trigger job, fetch artifact'){
        build job: 'MNTLAB-akavaleu-child1-build-job' , parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: "akavaleu"]]
        copyArtifacts filter: 'akavaleu_dsl_script.tar.gz', fingerprintArtifacts: true, projectName: 'MNTLAB-akavaleu-child1-build-job', selector: lastSuccessful()
    }

    stage('Packaging and Publishing results'){
        
    }
}
