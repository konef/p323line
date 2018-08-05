node("${SLAVE}") {
    stage('Preparating (Checking out)'){
        try {
            git branch: 'disakau',
            url: 'https://github.com/MNT-Lab/p323line'
        }
        catch (err) {
            Notification('Failure', 'Preparation (Checking out)', err)
            throw err;
        }
    }
    stage("Testing")
        withMaven(maven: 'mavenLocal'){
            parallel (
                    "pre-Integration test": {
                        sh "mvn -f ./helloworld-ws/pom.xml pre-integration-test"
                    },
                    "Integration test": {
                        sh "mvn -f ./helloworld-ws/pom.xml integration-test"
                    },
                    "post-Integration test": {
                        sh "mvn -f ./helloworld-ws/pom.xml post-integration-test"
                    })
        }
        stage("Triggering job and fetching artefact after finishing"){
            build job: 'MNTLAB-disakau-child-1-build-job',
                parameters: [string(name: 'BRANCH_NAME', value: 'disakau')]
        copyArtifacts filter: 'disakau_dsl_script.tar.gz',
                fingerprintArtifacts: true,
                projectName: 'MNTLAB-disakau-child-1-build-job',
                selector: lastSuccessful()
    }
     stage ('Packaging and Publishing results') {
           sh "tar -xvf disakau_dsl_script.tar.gz"
           sh 'cp -f helloworld-ws/target/helloworld-ws.war .'
           sh "tar -czf pipeline-disakau-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile helloworld-ws.war"
           archiveArtifacts "pipeline-disakau-${BUILD_NUMBER}.tar.gz"
           sh "groovy pull_push.groovy -p push -a pipeline-disakau-${BUILD_NUMBER}.tar.gz"
    }
    stage ('Asking for manual approval') {
      timeout(time: 120, unit: 'SECONDS') {
               input message:'Do you approve that deployment?', ok: 'Yes'
               }
    }
    stage("Deployment"){
            sh "groovy pull_push.groovy -p pull -a pipeline-disakau-${BUILD_NUMBER}.tar.gz"
    		sh "scp pipeline-disakau-${BUILD_NUMBER}.tar.gz vagrant@EPBYMINW0501:/opt/tomcat/latest/webapps"
    }
}