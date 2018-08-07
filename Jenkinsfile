
def SendEmail(status, stag, err) {
    def date = new Date()
    mail bcc: '', body: "$date: ${env.JOB_NAME} Build_Id ${env.BUILD_ID} - $status\n$stag: $err", cc: '', from: 'Jenkins', replyTo: '', subject: 'Build status', to: 'uladzimir_kuchynski@epam.com'
}

def push() {
    nexusArtifactUploader artifacts: [[artifactId: 'pipeline-ukuchynski', classifier: '', file: 'pipeline-ukuchynski-${BUILD_NUMBER}.tar.gz', type: 'tar.gz']], credentialsId: 'nexus', groupId: 'Task11', nexusUrl: 'nexus', nexusVersion: 'nexus3', protocol: 'http', repository: 'maven-releases', version: '$BUILD_NUMBER' 
}

def pull() {
    sh 'wget http://192.168.1.11:8081/repository/maven-releases/Task11/pipeline-ukuchynski/${BUILD_NUMBER}/pipeline-ukuchynski-${BUILD_NUMBER}.tar.gz'
}

node("${SLAVE}") {
	stage ('Preparation (Checking out)') {
	    try {
		    git branch: 'ukuchynski', url: 'https://github.com/MNT-Lab/p323line.git'
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
			sleep 5
                        sh "mvn -f ./helloworld-ws/pom.xml integration-test" 
                    },
                    'post-integration-test': { 
			sleep 10
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
    	    build job: 'MNTLAB-ukuchynski-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'ukuchynski')]
    	    step([  $class: 'CopyArtifact',
                            filter: 'ukuchynski_dsl_script.tar.gz',
                            fingerprintArtifacts: true,
                            projectName: 'MNTLAB-ukuchynski-child1-build-job'
            ])
	    }
	    catch (err) {
            SendEmail('Failure', 'Triggering job and fetching artefact after finishing', err)
            throw err;
		}
	}
	stage ('Packaging and Publishing results') {
	    try {
    	    sh '''tar -xvzf ukuchynski_dsl_script.tar.gz;
    	    tar -cvzf pipeline-ukuchynski-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy -C helloworld-ws/target/ helloworld-ws.war''' 	
    	    archiveArtifacts 'pipeline-ukuchynski-${BUILD_NUMBER}.tar.gz'
    	    push ()
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
    	    pull ()
    	    sh 'tar -xzvf pipeline-ukuchynski-${BUILD_NUMBER}.tar.gz helloworld-ws.war'
    	    sh 'scp helloworld-ws.war root@tomcat:/share/tomcat/webapps'
	    }
        catch (err) {
            SendEmail('Failure', 'Deployment', err)
            throw err;
		}
	    //SendEmail('Success', 'Deployment', 'Well done!')
	}
}






 
