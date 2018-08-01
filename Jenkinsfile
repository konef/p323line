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
	/*stage ('Testing') {
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
	}*/
	stage ('Triggering job and fetching artefact after finishing') {
	    build job: 'MNTLAB-omonko-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'omonko')]
	    step([  $class: 'CopyArtifact',
                        filter: 'omonko_dsl_script.tar.gz	',
                        fingerprintArtifacts: true,
                        projectName: 'MNTLAB-omonko-child1-build-job'
        ])
	}
}
