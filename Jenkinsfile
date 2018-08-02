
node{

    tool name: 'mavenLocal', type: 'maven'
    tool name: 'java8', type: 'jdk'
    def mvn_version = 'mavenLocal'
    def java_version = 'java8'
    stage('Preparation') {
        deleteDir()
        git branch: 'aandryieuski', poll: false, url: 'https://github.com/MNT-Lab/p323line.git'
        echo "Preparation Stage is done \u2705"
    }
    stage('Building code'){
        //def mvn_version = 'mavenLocal'
        withEnv(["PATH+MAVEN=${tool mvn_version}/bin"]) {
            sh 'mvn -f helloworld-ws/pom.xml package'
            sh 'ls -la helloworld-ws/'
        }
        echo "Building code Stage is done \u2705"
    }
    stage('Testing'){
        withEnv(["JAVA_HOME=${tool java_version}"]) {
            echo "${env.JAVA_HOME}"
            parallel PreIntegrationTest: {
                try {
                    sh 'echo "Build pre-integration-test parallel stage"'
                    withEnv(["PATH+MAVEN=${tool mvn_version}/bin"]) {
                        sh 'mvn -f helloworld-ws/pom.xml pre-integration-test'
                    }
                }
                finally {
                    sh 'echo "Finished this stage"'
                    echo "PreIntegrationTest is done \u2705"
                }
            }, IntegrationTest: {
                try {
                    sh 'echo "Build integration-test parallel stage"'
                    withEnv(["PATH+MAVEN=${tool mvn_version}/bin"]) {
                        sh 'mvn -f helloworld-ws/pom.xml integration-test'
                    }
                }
                finally {
                    sh 'echo "Finished this stage"'
                    echo "IntegrationTest is done \u2705"
                }
            }, PostIntegrationTest: {
                try {
                    sh 'echo "Build post-integration-test parallel stage"'
                    withEnv(["PATH+MAVEN=${tool mvn_version}/bin"]) {
                        sh 'mvn -f helloworld-ws/pom.xml post-integration-test'
                    }
                }
                finally {
                    sh 'echo "Finished this stage"'
                    echo "PostIntegrationTest is done \u2705"
                }
            }, failFast: true
        }
        echo "Testing Stage is done \u2705"
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