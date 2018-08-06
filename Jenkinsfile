node("${SLAVE}") {
    stage('Preparating (Checking out)'){
        try {
            git branch: 'disakau',
                    url: 'https://github.com/MNT-Lab/p323line'
        }
        catch (err) {
            mail bcc: '', body: 'Failed', cc: '', from: '', replyTo: '', subject: "$STAGE_NAME stage failed ", to: 'danzisakau@gmail.com'
            throw err
        }
    }
    stage("Testing"){
        try {
            withMaven(maven: 'mavenLocal'){
                parallel (
                        "pre-Integration test": {
                            sh "mvn -f ./helloworld-ws/pom.xml pre-integration-test"},
                        "Integration test": {
                            sh "mvn -f ./helloworld-ws/pom.xml integration-test"},
                        "post-Integration test": {
                            sh "mvn -f ./helloworld-ws/pom.xml post-integration-test"})
            }
        }
        catch (err) {
            mail bcc: '', body: 'Failed', cc: '', from: '', replyTo: '', subject: "$STAGE_NAME stage failed ", to: 'danzisakau@gmail.com'
            throw err
        }
    }

    stage("Triggering job and fetching artefact after finishing"){
        try {
            build job: 'MNTLAB-disakau-child-1-build-job',
                    parameters: [string(name: 'BRANCH_NAME', value: 'disakau')]
            copyArtifacts filter: 'disakau_dsl_script.tar.gz',
                    fingerprintArtifacts: true,
                    projectName: 'MNTLAB-disakau-child-1-build-job',
                    selector: lastSuccessful()
        }
        catch (err) {
            mail bcc: '', body: 'Failed', cc: '', from: '', replyTo: '', subject: "$STAGE_NAME stage failed ", to: 'danzisakau@gmail.com'
            throw err
        }
    }

    stage ('Packaging and Publishing results') {
        try {
            sh "tar -xvf disakau_dsl_script.tar.gz"
            sh 'cp -f helloworld-ws/target/helloworld-ws.war .'
            sh "tar -czf pipeline-disakau-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile helloworld-ws.war"
            archiveArtifacts "pipeline-disakau-${BUILD_NUMBER}.tar.gz"
            sh "groovy pull_push.groovy -p push -a pipeline-disakau-${BUILD_NUMBER}.tar.gz"
        }
        catch (err) {
            mail bcc: '', body: 'Failed', cc: '', from: '', replyTo: '', subject: "$STAGE_NAME stage failed ", to: 'danzisakau@gmail.com'
            throw err
        }
    }

    stage ('Asking for manual approval') {
        try {
            timeout(time: 120, unit: 'SECONDS') {
                input message:'Do you approve that deployment?', ok: 'Yes'
            }
        }
        catch (err) {
            mail bcc: '', body: 'Failed', cc: '', from: '', replyTo: '', subject: "$STAGE_NAME stage failed ", to: 'danzisakau@gmail.com'
            throw err
        }
    }

    stage("Deployment") {
        try {
            sh "groovy pull_push.groovy -p pull -a pipeline-disakau-${BUILD_NUMBER}.tar.gz"
            sh "scp -P 2202 pipeline-disakau-${BUILD_NUMBER}.tar.gz vagrant@EPBYMINW0501:/vagrant/apache-tomcat-8.5.32/webapps/"
            sh "ssh -p2202 vagrant@EPBYMINW0501 'cd /vagrant/apache-tomcat-8.5.32/webapps/ && tar xzf pipeline-disakau-${BUILD_NUMBER}.tar.gz && rm -rf pipeline-disakau-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy'"
        }
        catch (err) {
            mail bcc: '', body: 'Failed', cc: '', from: '', replyTo: '', subject: "$STAGE_NAME stage failed ", to: 'danzisakau@gmail.com'
            throw err
        }
    }

    stage('Sending status') {
        mail bcc: '', body: 'Success', cc: '', from: '', replyTo: '', subject: "$STAGE_NAME stage failed ", to: 'danzisakau@gmail.com'
    }
}