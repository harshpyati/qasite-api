sh /usr/share/tomcat9/bin/catalina.sh stop
mvn clean package && cp target/qasite.war /usr/share/tomcat9/webapps
sh /usr/share/tomcat9/bin/catalina.sh run & .