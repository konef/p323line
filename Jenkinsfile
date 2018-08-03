node {
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
}