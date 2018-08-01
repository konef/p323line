node {
	def app

	stage('Clone repository') {
    	  checkout([$class: 'GitSCM', branches: [[name: '*/dzhukova']],
    	  doGenerateSubmoduleConfigurations: false, extensions: [],
    	  submoduleCfg: [], userRemoteConfigs:
    	  [url: 'git@github.org:MNT-Lab/d323dsl.git']])
	}

	stage('Build') {
    	  mvn package
	}

	stage('pre-integration-test') {
	mvn pre-integration-test
	}
 	stage('integration-test') {
        mvn integration-test
	}
 	stage('post-integration-test') {
        mvn post-integration-test
	}

	stage('Push') {
    	  echo 'Push image'
	}
}
