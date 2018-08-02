node {
    def app

    stage('Clone repository') {
        checkout([$class                           : 'GitSCM', branches: [[name: '*/hviniarski']],
                  doGenerateSubmoduleConfigurations: false, extensions: [],
                  submoduleCfg                     : [], userRemoteConfigs:
                          [url: 'git@github.org:MNT-Lab/d323dsl.git']])
    }

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
                        sh "mvn -f helloworld-ws/pom.xml integration-test"
                    },
                    'post-integration-test': {
                        sh "mvn -f helloworld-ws/pom.xml post-integration-test"
                    }
            )
        }


    stage('Triggering job') {
        build job: 'test/MNTLAB-hviniarski-child1-build-job', parameters: [[$class: 'GitParameterValue', name: 'BRANCH_NAME', value: 'hviniarski']]
        copyArtifacts filter: 'hviniarski_dsl_script.tar.gz', projectName: 'test/MNTLAB-hviniarski-child1-build-job', selector: lastSuccessful()
    }
}