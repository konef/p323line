node() {
	stage ('Preparation (Checking out)') {
		git branch: 'omonko', url: 'https://github.com/MNT-Lab/p323line.git'
	}
	stage ('Building code') {
		withMaven(maven: 'mavenLocal') {
           	     sh "mvn -f ./helloworld-ws/pom.xml package"
        	}
	}
}
