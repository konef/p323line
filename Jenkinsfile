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
                        filter: 'omonko_dsl_script.tar.gz',
                        fingerprintArtifacts: true,
                        projectName: 'MNTLAB-omonko-child1-build-job'
        ])
	}
	stage ('Packaging and Publishing results') {
	   sh '''tar -xvzf omonko_dsl_script.tar.gz;
	   tar -cvzf pipeline-omonko-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy -C helloworld-ws/target/ helloworld-ws.war''' 	
	   archiveArtifacts 'pipeline-omonko-${BUILD_NUMBER}.tar.gz'
	   //nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'maven-releases', packages: [[$class: 'MavenPackage', mavenAssetList: [], mavenCoordinate: [artifactId: 'pipeline-omonko-${BUILD_NUMBER}.tar.gz', groupId: 'Task11', packaging: 'zip', version: '${BUILD_NUMBER}']]]
	   nexusArtifactUploader artifacts: [[artifactId: 'pipeline-omonko', classifier: '', file: 'pipeline-omonko-${BUILD_NUMBER}.tar.gz', type: 'tar.gz']], credentialsId: 'nexus', groupId: 'Task11', nexusUrl: '192.168.1.3', nexusVersion: 'nexus3', protocol: 'http', repository: 'maven-releases', version: '$BUILD_NUMBER'

	}
}
