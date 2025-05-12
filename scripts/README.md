# manual_jarsigner

This tools manually signs the distribution jars, rather than relying on the sign-jars target in the ant build script (phase2ui/nbproject/jnlp-impl.xml) (org.netbeans.modules.javawebstart.anttasks.SignJarsTask class). This allows us to specify a tsa argument to timestamp the signing, which might help after the certificate has expired.

This has now been modified to work with the new hardware token, this will only work on an Ubuntu 24.04 machine with the safenet authentication client tools installed.

Setup the store password from the password list as follows:
setenv JARSIGNER_STOREPASS "Keystore password"

before invoking the script from this directory. You will also need to update the certificate_alias inside the script each year (or whenever the certificate is renewed). This method also requires a valid Linux eToken.cfg config file.


# manual_jarsigner.bat

This tools manually signs the distribution jars on a windows machine. The StorePass variable needs setting in the script (see the password sheet for details). An eToken.cfg file needs to be written. See the Phase2UICertificates wikiword for how to use the script and write the config files.

