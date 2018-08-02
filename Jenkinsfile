node("${SLAVE}") {
    tool name: 'mavenLocal', type: 'maven'
    tool name: 'java8', type: 'jdk'
    def mvn_v = 'mavenLocal'
    def java_v = 'java8'
    def groovy_v = 'groovy4'
	
    try {
	withEnv(["PATH+MAVEN=${tool mvn_v}/bin"],["JAVA_HOME=${tool java_version}"]){
	stage('Preparating (Checking out)') 
    	git branch: 'ymaniukevich', 
    	url: 'https://github.com/MNT-Lab/p323line'
    stage('Building code')
            sh "mvn -f ./helloworld-ws/pom.xml clean install"
    stage("Testing")
        parallel (
        "pre-Integration test": {
            sh "mvn -f ./helloworld-ws/pom.xml pre-integration-test"
        },
        "Integration test": {
            sh "mvn -f ./helloworld-ws/pom.xml integration-test"
        },
        "post-Integration test": {
            sh "mvn -f ./helloworld-ws/pom.xml post-integration-test"
        })}
    stage("Triggering job and fetching"){
        build job: 'MNTLAB-ymaniukevich-child1-build-job', 
        parameters: [string(name: 'BRANCH_NAME', value: 'ymaniukevich')]
        copyArtifacts filter: 'ymaniukevich_dsl_script.tar.gz',
        fingerprintArtifacts: true, 
        projectName: 'MNTLAB-ymaniukevich-child1-build-job', 
        selector: lastSuccessful()
        }
    stage("Packaging and Publishing results"){
        sh "tar -xvf ymaniukevich_dsl_script.tar.gz"
        sh "tar -czf pipeline-ymaniukevich-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C helloworld-ws/target/ helloworld-ws.war"
        sh "groovy ./push.groovy"
	sh "which groovy"
    }
	stage("Deployment"){
	    sh "groovy ./pull.groovy"
	    sh "scp -P2200 jboss-parent-23.tar.gz  jboss-parent-23.tar.gz vagrant@EPBYMINW7296:/opt/tomcat/latest/webapps"
	    sh "ssh -p2200 vagrant@EPBYMINW7296 'cd /opt/tomcat/latest/webapps/ && tar xzf jboss-parent-23.tar.gz && rm -rf jboss-parent-23.tar.gz Jenkinsfile jobs.groovy'"
	    }
    currentBuild.result = 'SUCCESS'
    }
catch (err) {
    currentBuild.result = 'FAILURE'
  }
  finally {
	/*mail to: 'manukevich96@gmail.com',
      subject: "Status of pipeline: ${currentBuild.fullDisplayName}",
      body: "${env.BUILD_URL} has result ${currentBuild.result}"*/
}
}
