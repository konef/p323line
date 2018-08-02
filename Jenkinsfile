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
    parallel(
            'pre-integration-test':{
                stage ('pre-integration-test')
                        {
                            withMaven(maven: 'Maven3'){
                                sh "mvn -f helloworld-ws/pom.xml pre-integration-test"
                            }
                        }
            },
            'integration-test':{
                stage ('integration-test')
                        {
                            withMaven(maven: 'Maven3'){
                                sh "mvn -f helloworld-ws/pom.xml integration-test"
                            }
                        }
            },
            'post-integration-test':{
                stage ('post-integration-test')
                        {
                            withMaven(maven: 'Maven3'){
                                sh "mvn -f helloworld-ws/pom.xml post-integration-test"
                            }
                        }
            }
    )
}