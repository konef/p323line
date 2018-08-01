node() {
	stage ('Preparation (Checking out)') {
		git branch: 'omonko', url: 'https://github.com/MNT-Lab/p323line.git'
	}
	stage ('Building code') {
		withMaven(maven: 'mavenLocal') {
            // Run the maven build
            sh "mvn -f ./helloworld-ws/pom.xml package"
        }
	}
	stage ('Testing') {
	    withMaven(maven: 'mavenLocal') {
		    parallel (
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
	}
	/*stage ('Triggering job and fetching artefact after finishing') {
		
	}*/
}
