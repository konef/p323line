

String student = 'aandryieuski'


node{
    tool name: 'mavenLocal', type: 'maven'
    tool name: 'java8', type: 'jdk'
    def mvn_version = 'mavenLocal'
    def java_version = 'java8'
    stage('Preparation') {
        deleteDir()
        git branch: 'aandryieuski', poll: false, url: 'https://github.com/MNT-Lab/p323line.git'
        echo "\u2776: Preparation Stage is done \u2705"
    }
    stage('Building code'){
        //def mvn_version = 'mavenLocal'
        withEnv(["PATH+MAVEN=${tool mvn_version}/bin"]) {
            sh 'mvn -f helloworld-ws/pom.xml clean package'
        }
        echo "\u2777: Building code Stage is done \u2705"
    }
    stage('Testing'){
        //sh 'mkdir PreIntegrationTest IntegrationTest PostIntegrationTest'
        withEnv(["JAVA_HOME=${tool java_version}"]) {
            parallel PreIntegrationTest: {
                try {
                    echo "\u27A1 Build pre-integration-test parallel stage"
                    withEnv(["PATH+MAVEN=${tool mvn_version}/bin"]) {
                        sh 'mvn -f helloworld-ws/pom.xml pre-integration-test'
                    }
                }
                finally {
                    sh 'echo "Finished this stage"'

                }
            }, IntegrationTest: {
                try {
                    echo "\u27A1 Build integration-test parallel stage"
                    withEnv(["PATH+MAVEN=${tool mvn_version}/bin"]) {
                        sleep 30
                        sh 'mvn -f helloworld-ws/pom.xml integration-test'
                    }
                }
                finally {
                    sh 'echo "Finished this stage"'

                }
            }, PostIntegrationTest: {
                try {
                    echo "\u27A1 Build post-integration-test parallel stage"
                    withEnv(["PATH+MAVEN=${tool mvn_version}/bin"]) {
                        sleep 60
                        sh 'mvn -f helloworld-ws/pom.xml post-integration-test'
                    }
                }
                finally {
                    sh 'echo "Finished this stage"'

                }
            }, failFast: true
        }
        echo "\u2778: Testing Stage is done \u2705"
    }
    stage('Triggering job and fetching artefact after finishing'){
        build job: "MNTLAB-${student}-child1-build-job", parameters: [string(name: 'BRANCH_NAME', value: student)]
        copyArtifacts filter: "${student}_dsl_script.tar.gz", projectName: "MNTLAB-${student}-child1-build-job", selector: lastSuccessful()
        sh 'ls -la'
        echo "\u2779: Triggering job and fetching artefact after finishing Stage is done \u2705"
    }

}

/*
 err = caughtError
 currentBuild.result = "FAILURE"
 String recipient = 'infra@lists.jenkins-ci.org'
 mail subject: "${env.JOB_NAME} (${env.BUILD_NUMBER}) failed",
         body: "It appears that ${env.BUILD_URL} is failing, somebody should do something about that",
           to: recipient,
      replyTo: recipient,
 from: 'noreply@ci.jenkins.io'
*/