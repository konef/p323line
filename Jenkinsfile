node {
    stage ('Checkout'){
 checkout([$class: 'GitSCM', branches: [[name: '*/apatapniou']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p323line']]])
}
    stage('Build'){
        withMaven(maven: 'Maven') {
      sh "mvn -f ./helloworld-ws/pom.xml install"
}
    }
    stage ('Testing')
    {
        withMaven(maven: 'Maven') {
          parallel (
            "pre-integration-test":  {
                sh "mvn -f ./helloworld-ws/pom.xml pre-integration-test"
            },
            "integration-test": {
                sh "mvn -f ./helloworld-ws/pom.xml integration-test"
                sleep(15)
                
            },
            "post-integration-test": {
                sh "mvn -f ./helloworld-ws/pom.xml post-integration-test"
                sleep(30)
                
            }
            )
        }        
    }
   
        stage ('Triggering job and fetching artefact after finishing'){
        build job: 'MNTLAB-apatapniou-child1-build-job', parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: "apatapniou"]]
        step([  $class: 'CopyArtifact',
                            filter: '*.tar.gz',
                            fingerprintArtifacts: true,
                            projectName: 'MNTLAB-apatapniou-child1-build-job',
                            selector: lastSuccessful()
])
    }
    stage ('Packaging and Publishing results'){
    sh "tar -xzvf apatapniou_dsl_script.tar.gz"
    sh "cp helloworld-ws/target/helloworld-ws.war ."   
    }
    }
}
