CliBuilder cli = new CliBuilder(
        usage: 'groovy pull-push.groovy -p {PULLPUSH}  -a {art_name} ')
cli.with {
    p longOpt: 'PULLPUSH', args: 1, required: true, values: ['pull','push'], 'pull/push artifact'
    a longOpt: 'art_name', args: 1, 'art_name from job Jenkins'

}
def options = cli.parse(args)
if (!options) {
    return
}

def art_name= options.a
def PULLPUSH = options.p

def cred = "nexus-service-user:admin123"
def repo = "maven-releases/HW-ws"
def way = "http://192.168.100.13:8081"

if("$PULLPUSH"=="push"){
    println "push ${art_name}"
    def preart = art_name.substring(0, art_name.lastIndexOf("-"))
    def build_number = art_name.replaceAll("\\D+","")
    println "Artifact suffix = ${preart}"
    def File = new File ("war/target/${preart}-${build_number}.war").getBytes()
    def connection = new URL( "${way}/repository/${repo}/${preart}/${build_number}/${preart}-${build_number}.war" )
            .openConnection() as HttpURLConnection
    def auth = "${cred}".getBytes().encodeBase64().toString()
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
else {
    println "pull ${art_name}"
    def pre_art = art_name.substring(0, art_name.lastIndexOf("-"))
    def build_number = art_name.replaceAll("\\D+", "")
    new File ("$art_name").withOutputStream { out ->
        def url = new URL("${way}/repository/${repo}/${pre_art}/${build_number}/${art_name}").openConnection()
        def remoteAuth = "Basic " + "${cred}".bytes.encodeBase64()
        url.setRequestProperty("Authorization", remoteAuth);
        out << url.inputStream
    }
}