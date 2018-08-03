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
           sh "tar -xf ${student}_dsl_script.tar.gz jobs.groovy"
           sh "tar -czf pipeline-${student}-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C build/libs gradle-simple.jar"
           archiveArtifacts "pipeline-${student}-${BUILD_NUMBER}.tar.gz"
           sh "groovy pull_push.groovy -p push -a pipeline-${student}-${BUILD_NUMBER}.tar.gz"
    }
}