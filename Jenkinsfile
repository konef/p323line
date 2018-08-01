node {
	stage('Preparating (Checking out)') 
    	git branch: 'ymaniukevich', 
    	url: 'https://github.com/MNT-Lab/p323line'
    stage('Building code')
        withMaven(maven: 'maven') {
            sh "mvn -f ./helloworld-ws/pom.xml clean install"
        }
        
    stage("Testing")
        withMaven(maven: 'maven'){
        parallel (
        "pre-Integration test": {
            sh "mvn -f ./helloworld-ws/pom.xml pre-integration-test"
        },
        "Integration test": {
            sh "mvn -f ./helloworld-ws/pom.xml integration-test"
        },
        "post-Integration test": {
            sh "mvn -f ./helloworld-ws/pom.xml post-integration-test"
        }
        )
        }
    stage("Triggering job and fetching"){
        build job: 'MNTLAB-ymaniukevich-child1-build-job', 
        parameters: [string(name: 'BRANCH_NAME', value: 'ymaniukevich')]
        copyArtifacts filter: 'ymaniukevich_dsl_script.tar.gz',
        fingerprintArtifacts: true, 
        projectName: 'MNTLAB-ymaniukevich-child1-build-job', 
        selector: lastSuccessful()
        }

        
    }
    

