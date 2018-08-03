def nexus(String stap) {

    def nexus_path = "192.168.1.2:8081"
    def group = "project"
    def artifact = "helloworld"
    def version = "1.${BUILD_NUMBER}"
    def repo = "project-releases"

    if (stap == "push") {
        def File = new File("pipeline-ymaniukevich-${build_num}.tar.gz").getBytes()
        def con = new URL("http://${nexus_path}/repository/${repo}/${group}/${artifact}/${version}/pipeline-ymaniukevich-${build_num}.tar.gz").openConnection()
        def authString = "admin:admin123".getBytes().encodeBase64().toString()
        con.setRequestProperty("Authorization", "Basic ${authString}")
        con.setRequestMethod("PUT")
        con.doOutput = true
        con.setRequestProperty("Content-Type", "application/x-gzip")
        def writer = new DataOutputStream(con.outputStream)
        writer.write(File)
        writer.close()
        println con.responseCode
    } else {
        new File("/${work_path}/${artifact}-${version}.tar.gz").withOutputStream { out ->
            def url = new URL("http://${nexus_path}/repository/${repo}/${group}/${artifact}/${version}/pipeline-ymaniukevich-${build_num}.tar.gz").openConnection()
            out << url.inputStream
        }
    }
}
