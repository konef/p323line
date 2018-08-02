node{
    stage('Checkout'){
        checkout([$class: 'GitSCM', branches: [[name: 'hviniarski']],
                  doGenerateSubmoduleConfigurations: false,
                  extensions: [], submoduleCfg: [],
                  userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p323line']]])
    }
    stage('Build'){
        withMaven(maven: 'Maven3'){
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

    stage ('Triggering job'){
        build job: 'test/MNTLAB-hviniarski-child1-build-job', parameters: [[$class: 'GitParameterValue', name: 'BRANCH_NAME', value: 'hviniarski']]
        copyArtifacts filter: 'hviniarski_dsl_script.tar.gz', projectName: 'test/MNTLAB-hviniarski-child1-build-job', selector: workspace()
    }

    stage ('Packaging and Publishing results'){
        sh "tar -xzvf  hviniarski_dsl_script.tar.gz"
        sh "tar -czvf pipeline-hviniarski-${BUILD_NUMBER}.tar.gz Jenkisfile jobs.groovy -C ./helloworld-ws/target/ helloworld-ws.war "
    }
}