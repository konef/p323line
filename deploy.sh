#! /bin/bash

FILE=$(ls -la /tmp/jenkins_tmp/*.tar.gz | awk '{print $9}' | awk -F'/' '{print $NF}' )

a=$(cat /tmp/jenkins_tmp/$FILE.md5)
b=$(md5sum /tmp/jenkins_tmp/$FILE | awk '{print $1}')

sleep 2

if [[ "$a" != "$b" ]];then
	exit 1
fi

BUILD_NUMBER=$(echo $FILE | awk -F'-' '{print $2}')
echo "Build Number ==> $BUILD_NUMBER"

[[ -d /tmp/jenkins_tmp/war ]] || mkdir /tmp/jenkins_tmp/war

tar xfz /tmp/jenkins_tmp/$FILE -C /tmp/jenkins_tmp/war --strip=8

[[ -f /tmp/jenkins_tmp/war/helloworld.war_old ]] &&  mv /tmp/jenkins_tmp/war/helloworld.war_old /tmp/jenkins_tmp/war/helloworld.war_old_old
[[ -f /opt/cd-proc/jboss/server/default/deploy/helloworld.war  ]] && mv /opt/cd-proc/jboss/server/default/deploy/helloworld.war /tmp/jenkins_tmp/war/helloworld.war_old
cp /tmp/jenkins_tmp/war/helloworld-ws.war /opt/cd-proc/jboss/server/default/deploy/helloworld.war

echo "Making of the backup and deploy new build version"

sleep 5

curl http://localhost:8080/helloworld/status-page.html | grep "Build-Number:$BUILD_NUMBER"
a=$?
curl -I http://localhost:8080/helloworld/ | awk '{print $2}' | head -n 1 | grep 200
b=$?

sleep 2

if [ "$a" -eq 0 ] && [ "$b" -eq 0 ];then
    rm -rf /tmp/jenkins_tmp/war/helloworld.war_old_old
    echo "OK. $a,$b"
    exit 0
else
    rm -rf /opt/cd-proc/jboss/server/default/deploy/helloworld.war
    sleep 2
    mv /tmp/jenkins_tmp/war/helloworld.war_old /opt/cd-proc/jboss/server/default/deploy/helloworld.war
    sleep 2
    mv /tmp/jenkins_tmp/war/helloworld.war_old_old /tmp/jenkins_tmp/war/helloworld.war_old
    echo "FAIL - return to previous version. $a,$b"
    exit 1
fi
