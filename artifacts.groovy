#!/bin/groovy
@Grab(group='org.apache.httpcomponents', module='httpcore', version='4.4.10')
import org.apache.http.*
hostname="192.168.1.4:8081"
username="jenkins"
password="jenkins"
reponame="mvnrepo"

switch (args[0]){
    case "push":
        return push(args[1], args[2], args[3])
        break
    case "pull":
        pull(args[1])
        break
    default:
        print("WRONG ARGUMENTS!${args}")
        return 1
}

int push(artifact, buildnum) {
    println("Pushing artifact: ${artifact}")
    def File = new File (artifact).getBytes()
    print("Sending request: \"http://${hostname}/repository/${reponame}/${artifact}\"")
    def connection = new URL( "http://${hostname}/repository/${reponame}/${buildnum}/${artifact}")
            .openConnection() as HttpURLConnection
    def credentials = "${username}:${password}"
    println("Creds: ${credentials}")
    def auth = credentials.getBytes().encodeBase64().toString()
    connection.setRequestMethod("PUT")
    connection.doOutput = true
    connection.setRequestProperty("Authorization" , "Basic ${auth}")
    connection.setRequestProperty( "Content-Type", "application/octet-stream" )
    connection.setRequestProperty( "Accept", "*/*" )
    def writer = new DataOutputStream(connection.outputStream)
    writer.write (File)
    writer.close()
    println(connection.responseCode)
    if(connection.responseCode != 200){
        return 1
    }
}
/*
void pull(artifact) {
    restClient = new RESTClient("http://${hostname}/repository/${reponame}/")
    restClient.auth.basic 'Artifacts-service-user', 'Artifacts'
    restClient.encoder.'application/zip' = this.&setZipMimeType
    restClient.put(
            path: "http://${hostname}/repository/${reponame}/${artifact}",
            body: new File(artifact),
            requestContentType: 'application/zip'
    )
}

FileEntity setZipMimeType(Object data) throws UnsupportedEncodingException {
    def entity = new FileEntity((File) data, 'application/zip')
    entity.setContentType('application/zip')
    return entity
}
*/
