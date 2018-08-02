node ("${SLAVE}") {
       try{
       stage('Clone sources') {
       git url: 'https://github.com/MNT-Lab/p323line.git', branch: 'dzhukova';}
stage('Build') {
           withMaven(maven: 'mavenLocal')
        {
           sh "mvn -f ./helloworld-ws/pom.xml clean install"
        }
}
parallel('pre-integration-test': {
stage('pre-integration-test') {
           withMaven(maven: 'mavenLocal')
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

stage('build child job') {
    build job: 'dzhukova/MNTLAB-dzhukova-child1-build-job', parameters: [
        string(name: 'BRANCH_NAME', value: 'dzhukova')
    ]
}
stage('Copy Artifact') {
    copyArtifacts filter: 'dzhukova_dsl_script.tar.gz', projectName: 'dzhukova/MNTLAB-dzhukova-child1-build-job', selector: lastSuccessful()
    sh 'tar -xvf dzhukova_dsl_script.tar.gz';
    sh "tar -czvf pipeline-dzhukova-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy -C helloworld-ws/target/ helloworld-ws.war "
    sh 'groovy all.groovy -c push'
    input 'Would you like to continue?'   
 }
stage('Deploy Artifact') {
    sh 'groovy all.groovy -c pull'
    sh 'tar -xvf app.tar.gz'
sh 'scp -v -P 2200 *.war root@EPBYMINW1969:/usr/local/bin/apache-tomcat-8.5.31/webapps/helloapp.war'
}
} catch(e) {
        mail bcc: '', body: 'failed stage at ', cc: '', from: '', replyTo: '', subject: "stage failed ", to: 'zhukova.darya@gmail.com'
    }
       finally 
       { mail bcc: '', body: 'failed stage at ', cc: '', from: '', replyTo: '', subject: "stage failed ${currentBuild.fullDisplayName}", to: 'zhukova.darya@gmail.com'
	}
}

