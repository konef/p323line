node {
       stage('Clone sources') {
        git url: 'https://github.com/MNT-Lab/p323line.git', branch: 'dzhukova';}
stage('Build') {
           withMaven(maven: 'mavenLocal')
        {
           sh "mvn -f ./helloworld-ws/pom.xml clean install"
        }
}
parallel('pre-integration-test': {
stage('pre-integration-test') {
           withMaven(maven: 'mavenLocal')
        {
           sh "mvn -f ./helloworld-ws/pom.xml pre-integration-test"
        }
}},
'integration-test': { 
    stage('integration-test') {
           withMaven(maven: 'mavenLocal')
        {
           sh "mvn -f ./helloworld-ws/pom.xml integration-test"
        }
}},
'post-integration-test': {
    stage('post-integration-test') {
           withMaven(maven: 'mavenLocal')
        {
           sh "mvn -f ./helloworld-ws/pom.xml post-integration-test"
        }
}})

}
