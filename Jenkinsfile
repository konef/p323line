node("${SLAVE}") {
    stage('Preparation (Checking out)') {
        git branch: 'stsitou', url: 'https://github.com/MNT-Lab/p323line.git'
    }
    stage('Building code') {
        withMaven(maven: 'mavenLocal') {
            sh "mvn -f ./helloworld-ws/pom.xml package"
        }
    }
    stage("Testing")
    withMaven(maven: 'mavenLocal') {
        parallel(
                'pre-integration-test': {
                    sh "mvn -f ./helloworld-ws/pom.xml pre-integration-test"
                },
                'integration-test': {
                    sh "mvn -f ./helloworld-ws/pom.xml integration-test"
                },
                'post-integration-test': {
                    sh "mvn -f ./helloworld-ws/pom.xml post-integration-test"
                }
        )
    }
    stage("Triggering job and fetching artifact after finishing"){
        echo "Starting another job"
        def childjob  = build job: 'EPBYMINW2466/MNTLAB-stsitou-child1-build-job', parameters: ["name: 'BRANCH_NAME', value: 'stsitou'"]
        echo "Job status: ${childjob.result}"
    }

}
