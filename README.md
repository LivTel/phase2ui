This is the Liverpool Telescope Phase2UI Java NetBeans project code.
This should be built in netbeans (usually netbeans-8.2).
The dist directory should then contain a distribution for deployment.

##Deployment

After copying the dist directory into ltproxy:/home/eng/download/
The ltproxy:/home/eng/DEPLOY-P2UI.sh script should be run (as root) to deploy the software.
It is worth backing up ltproxy:/usr/local/tomcat/webapps/ before attempting this.

The launch_phase2gui.jnlp is hand-coded and should be hand copied to
ltproxy:/usr/local/tomcat/webapps/ROOT/launch_oss. The launch.jnlp in the dist directory is not used.

There is a copy of the DEPLOY-P2UI.sh in this repo, that should be manually installed in ltproxy:/home/eng/

##Installing netbeans

Installed netbeans-8.2 from netbeans-8.2-javase-linux.sh (copied from ltobs9)

Loaded phase2ui project from /home/cjm/netbeans-workspace/phase2ui/


Errors:
* JiBX library could not be found
* JiBX_SOAP library could not be found
* Log4j could not be found
* jfreechart-1.0.2.jar file could not be found.
* jcommon-1.0.5.jar file could not be found
* JDK_1.6 platform could not be found

###JibX
* Tools->Libraries
* New Library button:
* Library Name: JiBX
* Library Type: Class libraries
* OK
* Add JAR/Folder: /home/dev/bin/javalib_third_party/jibx/, Select all jars

###Tools->Libraries
* New Library button:
* Library Name: JiBX_SOAP
* Library Type: Class libraries
* OK
* Add JAR/Folder: /home/dev/bin/javalib_third_party/jibx_soap/, Select all jars

###Log4j
* Tools->Libraries
* New Library button:
* Library Name: Log4j
* Library Type: Class libraries
* OK
* Add JAR/Folder: /home/dev/bin/javalib_third_party/log4j-1.2.13.jar

###jcommon/jfreechart

* Phase2UI Project, Right click->Properties
* Libraries tab
* Add Jar/Folder /home/dev/bin/javalib_third_party/jcommon-1.0.18.jar, /home/dev/bin/javalib_third_party/jfreechart-1.0.14.jar
* Remove broken jcommon/jfreechart references

### JDK_1.6

* Tools-> Java Platforms
* Add Platform
* Java Standard Edition
* /usr/lib/jvm/jdk1.6.0_45
* Platform Name should be JDK_1.6
* Use defaults...

Window->Output to see compilation output.
