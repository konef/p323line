@Grapes(
        @Grab(group='org.apache.httpcomponents', module='httpcore', version='4.4.10')
)
import org.apache.http.*
hostname="192.168.1.4:8081"
username="jenkins"
password="jenkins"
reponame="mvnrepo"

switch (args[1]){
    case "push":
        push(args[2])
        break
    case "pull":
        pull(args[2])
        break
    default:
        print("WRONG ARGUMENTS!${args}")
}

void pull(artifact) {
    println("Pushing artifact: ${artifact}")
    def File = new File (artifact).getBytes()
    def connection = new URL( "${hostname}/repository/${reponame}/${artifact}")
            .openConnection() as HttpURLConnection
    def auth = (username + ":" + password).getBytes().encodeBase64().toString()
    connection.setRequestMethod("PUT")
    connection.doOutput = true
    connection.setRequestProperty("Authorization" , "Basic ${auth}")
    connection.setRequestProperty( "Content-Type", "application/octet-stream" )
    connection.setRequestProperty( "Accept", "*/*" )
    def writer = new DataOutputStream(connection.outputStream)
    writer.write (File)
    writer.close()
    println connection.responseCode
}

void push(artifact) {
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
