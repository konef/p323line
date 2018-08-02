node{
    stage('Checkout'){
        checkout([$class: 'GitSCM', branches: [[name: 'mznak']],
                  doGenerateSubmoduleConfigurations: false,
                  extensions: [], submoduleCfg: [],
                  userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p323line']]])
    }
    stage('Building code'){
        withMaven(maven: 'mavenLocal'){
            sh "mvn -f ./helloworld-ws/pom.xml package"
        }
    }
    parallel(
            'pre-integration-test':{
                stage ('pre-integration-test')
                        {
                            withMaven(maven: 'mavenLocal'){
                                sh "mvn -f ./helloworld-ws/pom.xml pre-integration-test"
                            }
                        }
            },
            'integration-test':{
                stage ('integration-test')
                        {
                            withMaven(maven: 'mavenLocal'){
                                sh "mvn -f ./helloworld-ws/pom.xml integration-test"
                            }
                        }
            },
            'post-integration-test':{
                stage ('post-integration-test')
                        {
                            withMaven(maven: 'mavenLocal'){
                                sh "mvn -f ./helloworld-ws/pom.xml post-integration-test"
                            }
                        }
            }
    )

    stage ('Triggering job'){
        build job: 'test/MNTLAB-mznak-child1-build-job', parameters: [[$class: 'GitParameterValue', name: 'BRANCH_NAME', value: 'mznak']]
        copyArtifacts filter: 'mznak_dsl_script.tar.gz', projectName: 'test/MNTLAB-mznak-child1-build-job', selector: workspace()
    }

    stage ('Packaging and Publishing results'){
        sh "tar -xzvf  mznak_dsl_script.tar.gz"
        sh "tar -czvf pipeline-mznak-${BUILD_NUMBER}.tar.gz Jenkisfile jobs.groovy -C ./helloworld-ws/target/ helloworld-ws.war "
    }
}
