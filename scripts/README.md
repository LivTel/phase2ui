# manual_jarsigner

This tools manually signs the distribution jars, rather than relying on the sign-jars target in the ant build script (phase2ui/nbproject/jnlp-impl.xml) (org.netbeans.modules.javawebstart.anttasks.SignJarsTask class). This allows us to specify a tsa argument to timestamp the signing, which might help after the certificate has expired.

This has now been modified to work with the new hardware token, this will only work on an Ubuntu 24.04 machine with the safenet authentication client tools installed.

Setup the store password from keeper (LTMisc->Code Signing Certificates->SafeNet eToken 5110+) as follows:
setenv JARSIGNER_STOREPASS "Keystore password"

before invoking the script from this directory. You will also need to update the certificate_alias inside the script each year (or whenever the certificate is renewed). This method also requires a valid Linux eToken.cfg config file.


# manual_jarsigner.bat

This tools manually signs the distribution jars on a windows machine. The StorePass variable needs setting in the script (see the password sheet for details). An eToken.cfg file needs to be written. See the Phase2UICertificates wikiword for how to use the script and write the config files.

# Current signing procedure

* Ensure code signing is set in netbeans:
  * Right click on the Phase2UI project, select Properties
  * Select Application->Web Start is the left hand list.
  * Signing, click the Customize button.
  * Sign by a specified key, fill in the details for the old certificate from the password list.
* Build the software using netbeans with "Web Start" rather than <default config> option set. This will sign the jars with the old, out of date phase2ui-css keystore.
* Put the token in your computer
* cd ~/netbeans-workspace/phase2ui/scripts
* Check manual_jarsigner script, ensure it only tries to sign the first jar (exit 1 is uncommented).
* setenv JARSIGNER_STOREPASS "Keystore password"
* ./manual_jarsigner
* This signs the jars with the new certificates on the hardware token. It now uses -sigfile PHASE2UI so the old ceritificate signing is overwritten. We appear to have to use the same name, otherwise javaws rejects the signed jars (even though they verify OK).
* Tar up the dist directory.
* Copy the tar to ltproxy:download.
* Untar the tarred dist.
* As root, run /home/eng/DEPLOY-P2UI.sh.

