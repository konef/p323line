node("${SLAVE}"){
    result = ""
    test_result = ""
    try{
        try{
            stage('Checkout'){
                checkout([$class: 'GitSCM', branches: [[name: 'mznak']],
                          doGenerateSubmoduleConfigurations: false,
                          extensions: [], submoduleCfg: [],
                          userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p323line']]])
            }
        }
        catch(all){
            result = "FAIL CHECKOUT"
            throw new Exception()
        }

        try{
            stage('Building code'){
                withMaven(maven: 'mavenLocal'){
                    sh "mvn -f helloworld-ws/pom.xml package"
                }
            }
        }
        catch(all){
            result = "FAIL Building code"
            throw new Exception()
        }

        try{
            parallel(
                    'pre-integration-test':{
                        stage ('pre-integration-test')
                                {
                                    withMaven(maven: 'mavenLocal'){
                                        sh "mvn -f helloworld-ws/pom.xml pre-integration-test"
                                    }
                                }
                    },
                    'integration-test':{
                        stage ('integration-test')
                                {
                                    withMaven(maven: 'mavenLocal'){
                                        sh "mvn -f helloworld-ws/pom.xml integration-test"
                                    }
                                }
                    },
                    'post-integration-test':{
                        stage ('post-integration-test')
                                {
                                    withMaven(maven: 'mavenLocal'){
                                        sh "mvn -f helloworld-ws/pom.xml post-integration-test"
                                    }
                                }
                    }
            )
        }
        catch(all){
            test_result = "FAIL Fail Tests"
        }

        try{
            stage ('Triggering job'){
                build job: 'MNTLAB-mznak-child1-build-job', parameters: [[$class: 'GitParameterValue', name: 'BRANCH_NAME', value: 'mznak']]
                copyArtifacts filter: 'mznak_dsl_script.tar.gz', projectName: 'MNTLAB-mznak-child1-build-job', selector: workspace()
            }
        }
        catch(all){
            result = "FAIL Triggering job"
            throw new Exception()
        }

        try{
            stage ('Packaging and Publishing results'){
                sh "tar -xzvf  mznak_dsl_script.tar.gz"
                sh "tar -czvf pipeline-mznak-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy -C ./helloworld-ws/target/ helloworld-ws.war "
                archiveArtifacts 'pipeline-mznak-${BUILD_NUMBER}.tar.gz'
                sh "groovy ./download.groovy push"
            }
        }
        catch (all){
            result = "FAIL Packaging and Publishing results"
            throw new Exception()
        }

        try{
            stage ('Asking for manual approval'){
                input "Deploy artefact?"
            }
        }
        catch (all){
            result = "FAIL Asking for manual approval"
            throw new Exception()
        }


        try{
            stage ('Deployment'){
                sh "groovy ./download.groovy pull"
                sh '''ssh  -p2202 root@epbyminw7425 '/usr/local/tomcat/webapps/backup.sh'
                scp -P 2202 $WORKSPACE/pipeline-mznak-${BUILD_NUMBER}.tar.gz root@epbyminw7425:/usr/local/tomcat/webapps/helloworld.tar.gz
                exit;'''
                sh '''ssh  -p2202 root@epbyminw7425 <<EOF
                tar -xzvf /usr/local/tomcat/webapps/helloworld.tar.gz -C /usr/local/tomcat/webapps/
                rm -f /usr/local/tomcat/webapps/Jenkinsfile /usr/local/tomcat/webapps/jobs.groovy
                sleep 10
                exit
                EOF
                '''
                sh '''
                status=$( curl -I http://epbyminw7425/helloworld-ws/ | grep HTTP | awk '{print $2;}' )
                if [ $( echo $status )  != '200' ]
                then
                    ssh -p2202 root@epbyminw7425 """
                    rm -f /usr/local/tomcat/webapps/helloworld-ws.war 
                    tar -xzvf /usr/local/tomcat/webapps/backup.tar.gz -C /usr/local/tomcat/webapps/
                    rm -f /usr/local/tomcat/webapps/backup.tar.gz
                    ls -la /usr/local/tomcat/webapps/
                    """
                else
                    ssh -p2202 root@epbyminw7425  'rm -f /usr/local/tomcat/webapps/backup.tar.gz'
                fi
                '''
            }
        }
        catch (all){
            result = "FAIL Deployment"
            throw new Exception()
        }


        try{
            stage ('Successful deployment report'){
                        mail bcc: '', body: "Success ${test_result}", cc: '', from: '', replyTo: '', subject: 'Success', to: 'mikhailznak@gmail.com'

            }
        }
        catch (all){
            result = "FAIL Successful deployment report"
            throw new Exception()
        }
    }
    catch(all) {
        mail bcc: '', body: "Problem: ${result}", cc: '', from: '', replyTo: '', subject: 'Problem', to: 'mikhailznak@gmail.com'
    }
}
