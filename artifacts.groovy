#!/bin/groovy
nexus_hostname="192.168.1.4:8081/nexus"
username="jenkins"
password="jenkins"
reponame="mvnrepo"

switch (args[0]){
    case "push":
        push(args[1], args[2])
        break
    case "pull":
        pull(args[1], args[2])
        break
    default:
        print("WRONG ARGUMENTS!${args}")
        return 1
}

void push(artifact, buildnum) {
    println("Pushing artifact: ${artifact}...")
    def fileToSend = new File ("./${artifact}").getBytes()
    print("Sending request: \"http://${nexus_hostname}/repository/${reponame}/${buildnum}/${artifact}\"")
    def connection = new URL( "http://${nexus_hostname}/repository/${reponame}/${buildnum}/${artifact}")
            .openConnection() as HttpURLConnection
    def credentials = "${username}:${password}"
    println("Creds: ${credentials}")
    def auth = "${credentials}".getBytes().encodeBase64().toString()
    connection.setRequestMethod("PUT")
    connection.doOutput = true
    connection.setRequestProperty("Authorization" , "Basic ${auth}")
    connection.setRequestProperty( "Content-Type", "application/octet-stream" )
    connection.setRequestProperty( "Accept", "*/*" )
    def writer = new DataOutputStream(connection.outputStream)
    writer.write (fileToSend)
    writer.close()
    println(connection.responseCode)
    if(connection.responseCode != 201){
        throw new Exception("Artifact was not pushed")
    }
}

void pull(artifact, buildnum) {
    def credentials = "${username}:${password}"
    def auth = "${credentials}".getBytes().encodeBase64().toString()
    println "Pulling ${artifact}..."
    new File ("${artifact}").withOutputStream { out ->
        def url = new URL("http://${nexus_hostname}/repository/${reponame}/${buildnum}/${artifact}").openConnection()
        url.setRequestProperty("Authorization" , "Basic ${auth}")
        out << url.inputStream
    }
}
