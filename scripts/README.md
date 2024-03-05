# manual_jarsigner

This tools manually signs the distribution jars, rather than relying on the sign-jars target in the ant build script (phase2ui/nbproject/jnlp-impl.xml) (org.netbeans.modules.javawebstart.anttasks.SignJarsTask class). This allows us to specify a tsa argument to timestamp the signing, which might help after the certificate has expired.

Setup the store and key passwords from the password list as follows:
setenv jarsigner_storepass "Keystore password"
setenv jarsigner_keypass "Key password"

before invoking the script.

