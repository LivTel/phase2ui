This is the Liverpool Telescope Phase2UI Java NetBeans project code.
This should be built in netbeans.
The dist directory should then contain a distribution for deployment.

After copying the dist directory into ltproxy:/home/eng/download/
The ltproxy:/home/eng/DEPLOY-P2UI.sh script should be run (as root) to deploy the software.
It is worth backing up ltproxy:/usr/local/tomcat/webapps/ before attempting this.

The launch_phase2gui.jnlp is hand-coded and should be hand copied to
ltproxy:/usr/local/tomcat/webapps/ROOT/launch_oss. The launch.jnlp in the dist directory is not used.

There is a copy of the DEPLOY-P2UI.sh in this repo, that should be manually installed in ltproxy:/home/eng/
