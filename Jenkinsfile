node ("${SLAVE}") {
    try{
    stage('Preparation (Checking out)') {
    failed = STAGE_NAME
    git url: 'https://github.com/MNT-Lab/p323line.git', branch: 'dzhukova';
    }
    } catch(e) {
        mail bcc: '', body: 'failed stage at ', cc: '', from: '', replyTo: '', subject: "stage failed ${failed}", to: 'zhukova.darya@gmail.com'
    }
    
    try{
    stage('Building code') {
        withMaven(maven: 'mavenLocal')
        {
           sh "mvn -f ./helloworld-ws/pom.xml clean install"
        }
    }
    } catch(e) {
        mail bcc: '', body: 'failed stage at ', cc: '', from: '', replyTo: '', subject: "stage failed ${failed}", to: 'zhukova.darya@gmail.com'
    }
    
    try{
    parallel('pre-integration-test': {
    stage('pre-integration-test') {
           withMaven(maven: 'mavenLocal1')
        {
           sh "mvn -f ./helloworld-ws/pom.xml pre-integration-test"
        }
    }},
    'integration-test': { 
    stage('integration-test') {
           withMaven(maven: 'mavenLocal')
        {
           sh "mvn -f ./helloworld-ws/pom.xml integration-test"
        }
    }},
    'post-integration-test': {
    stage('post-integration-test') {
           withMaven(maven: 'mavenLocal')
        {
           sh "mvn -f ./helloworld-ws/pom.xml post-integration-test"
        }
    }})
    } catch(e) {
        mail bcc: '', body: 'failed stage at ', cc: '', from: '', replyTo: '', subject: "stage failed: TESTS", to: 'zhukova.darya@gmail.com'
    }
    
    try{
    stage('Triggering job and fetching artefact') {
    failed = STAGE_NAME
    build job: 'dzhukova/MNTLAB-dzhukova-child1-build-job', parameters: [
        string(name: 'BRANCH_NAME', value: 'dzhukova')
    ]
    copyArtifacts filter: 'dzhukova_dsl_script.tar.gz', projectName: 'dzhukova/MNTLAB-dzhukova-child1-build-job', selector: lastSuccessful()
    }
    } catch(e) {
        mail bcc: '', body: 'failed stage at ', cc: '', from: '', replyTo: '', subject: "stage failed ${failed}", to: 'zhukova.darya@gmail.com'
    }
    
    try{
    stage('Packaging and Publishing results') {
    failed = STAGE_NAME
        sh 'tar -xvf dzhukova_dsl_script.tar.gz';
    sh "tar -czvf pipeline-dzhukova-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy -C helloworld-ws/target/ helloworld-ws.war "
    sh 'groovy all.groovy -c push'
    }
    } catch(e) {
        mail bcc: '', body: 'failed stage at ', cc: '', from: '', replyTo: '', subject: "stage failed ${failed}", to: 'zhukova.darya@gmail.com'
    }
    
    try{
    stage('Asking for manual approval') {
    //    input 'Would you like to continue?'
    }
    } catch(e) {
        mail bcc: '', body: 'failed stage at ', cc: '', from: '', replyTo: '', subject: "stage failed ${failed}", to: 'zhukova.darya@gmail.com'
    }
    
    try{
    stage('Deployment') {
    failed = STAGE_NAME
    sh 'groovy all.groovy -c pull'
    sh 'tar -xvf app.tar.gz'
    sh 'scp -v -P 2200 *.war root@EPBYMINW1969:/usr/local/bin/apache-tomcat-8.5.31/webapps/helloapp.war'
    }
    } catch(e) {
        mail bcc: '', body: 'failed stage at ', cc: '', from: '', replyTo: '', subject: "stage failed ${failed}", to: 'zhukova.darya@gmail.com'
    }
        mail bcc: '', body: 'SUCCESS', cc: '', from: '', replyTo: '', subject: "${currentBuild.currentResult}", to: 'zhukova.darya@gmail.com'
	
}
