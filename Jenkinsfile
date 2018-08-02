def SendEmail(status, stag, err) {
    def date = new Date()
    mail bcc: '', body: "$date: ${env.JOB_NAME} Build_Id ${env.BUILD_ID} - $status\n$stag: $err", cc: '', from: 'Jenkins', replyTo: '', subject: 'Build status', to: 'oleg_monko@epam.com, dev@ep.am'
}

node('${SLAVE}') {
	stage ('Preparation (Checking out)') {
	    try {
		    git branch: 'omonko', url: 'https://github.com/MNT-Lab/p323line.git'
		} 
		catch (err) {
            SendEmail('Failure', 'Preparation (Checking out)', err)
            throw err;
		}
	}
	stage ('Building code') {
	    try {
		    withMaven(maven: 'mavenLocal') {
                sh "mvn -f ./helloworld-ws/pom.xml package"
            }
	    }
	    catch (err) {
            SendEmail('Failure', 'Building code', err)
            throw err;
		}
	}
	stage ('Testing') {
	    try {
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
		catch (err) {
            SendEmail('Failure', 'Testing', err)
            throw err;
		}
	}
	stage ('Triggering job and fetching artefact after finishing') {
	    try {
    	    build job: 'MNTLAB-omonko-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'omonko')]
    	    step([  $class: 'CopyArtifact',
                            filter: 'omonko_dsl_script.tar.gz',
                            fingerprintArtifacts: true,
                            projectName: 'MNTLAB-omonko-child1-build-job'
            ])
	    }
	    catch (err) {
            SendEmail('Failure', 'Triggering job and fetching artefact after finishing', err)
            throw err;
		}
	}
	stage ('Packaging and Publishing results') {
	    try {
    	    sh '''tar -xvzf omonko_dsl_script.tar.gz;
    	    tar -cvzf pipeline-omonko-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy -C helloworld-ws/target/ helloworld-ws.war''' 	
    	    archiveArtifacts 'pipeline-omonko-${BUILD_NUMBER}.tar.gz'
    	    //nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'maven-releases', packages: [[$class: 'MavenPackage', mavenAssetList: [], mavenCoordinate: [artifactId: 'pipeline-omonko-${BUILD_NUMBER}.tar.gz', groupId: 'Task11', packaging: 'zip', version: '${BUILD_NUMBER}']]]
    	    nexusArtifactUploader artifacts: [[artifactId: 'pipeline-omonko', classifier: '', file: 'pipeline-omonko-${BUILD_NUMBER}.tar.gz', type: 'tar.gz']], credentialsId: 'nexus', groupId: 'Task11', nexusUrl: 'nexus', nexusVersion: 'nexus3', protocol: 'http', repository: 'maven-releases', version: '$BUILD_NUMBER'
	    }
	    catch (err) {
            SendEmail('Failure', 'Packaging and Publishing results', err)
            throw err;
		}
	}
	stage ('Asking for manual approval') {
	    try {
    	    timeout(time: 3, unit: 'MINUTES') {
                input(id: "Deploy Gate", message: "Deploy ${params.project_name}?", ok: 'Deploy')
            }
	    }
        catch (err) {
            SendEmail('Failure', 'Asking for manual approval', err)
            throw err;
		}
	}
	stage ('Deployment') {
	    try {
    	    sh 'wget http://nexus/repository/maven-releases/Task11/pipeline-omonko/${BUILD_NUMBER}/pipeline-omonko-${BUILD_NUMBER}.tar.gz'
    	    sh 'tar -xzvf pipeline-omonko-${BUILD_NUMBER}.tar.gz helloworld-ws.war'
    	    //sshPublisher(publishers: [sshPublisherDesc(configName: 'Tomcat', transfers: [sshTransfer(excludes: '', execCommand: '', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: 'helloworld-ws.war')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
    	    sh 'scp helloworld-ws.war root@tomcat:/share/tomcat/webapps'
	    }
        catch (err) {
            SendEmail('Failure', 'Deployment', err)
            throw err;
		}
	    SendEmail('Success', 'Deployment', 'Well done!')
	}
}

