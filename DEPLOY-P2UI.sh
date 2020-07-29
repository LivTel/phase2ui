#!/bin/bash 

# -----------------------------------------------------------------------------------------------

# script to deploy P2UI to webapp container and stand-alone zip files
# script assumes that a new dist directory has been copied into the /home/eng/download directory

# ----------------------------------------------------------------------------------------------

# set up vars                                                                                                                                          
WEBSTART_NAME="launch_oss"
WEBAPPS_ROOT="/usr/local/tomcat/webapps/ROOT"
DIST_SOURCE="/home/eng/download/dist"

APP_ROOT=${WEBAPPS_ROOT}/os-x_app
APP_JAVA_ROOT=${APP_ROOT}/LT_Phase2UI.app/Contents/Resources/Java
ZIP_FILE_PATH=/usr/local/tomcat/webapps/ROOT/os-x_app/LT_Phase2UI.app

dd=`date -u "+%Y%m%d"` 


# ---------------------------------------------------------------------------------------------------------------
# WebStart application
# ---------------------------------------------------------------------------------------------------------------
echo "-------------------------------"
echo "Deploying web-start application"
echo "-------------------------------"

# backup launch_oss directory in the webapps/ROOT dir to a date-stamped directory

echo cp -r ${WEBAPPS_ROOT}/${WEBSTART_NAME} ${WEBAPPS_ROOT}/${WEBSTART_NAME}.${dd}
cp -r ${WEBAPPS_ROOT}/${WEBSTART_NAME} ${WEBAPPS_ROOT}/${WEBSTART_NAME}.${dd}

# delete the lib/ directory and the Phase2UI.jar file from the web-start directory

echo rm -rf ${WEBAPPS_ROOT}/${WEBSTART_NAME}/lib
rm -rf ${WEBAPPS_ROOT}/${WEBSTART_NAME}/lib
echo rm -rf ${WEBAPPS_ROOT}/${WEBSTART_NAME}/Phase2UI.jar
rm -rf ${WEBAPPS_ROOT}/${WEBSTART_NAME}/Phase2UI.jar

# copy the lib/ directory and the Phase2UI.jar file from dist directory into the web-start directory

echo cp -r ${DIST_SOURCE}/lib ${WEBAPPS_ROOT}/${WEBSTART_NAME}/
cp -r ${DIST_SOURCE}/lib ${WEBAPPS_ROOT}/${WEBSTART_NAME}/
echo cp -r ${DIST_SOURCE}/Phase2UI.jar ${WEBAPPS_ROOT}/${WEBSTART_NAME}/
cp -r ${DIST_SOURCE}/Phase2UI.jar ${WEBAPPS_ROOT}/${WEBSTART_NAME}/


# ---------------------------------------------------------------------------------------------------------------
# OS-X stand-alone app 
# ---------------------------------------------------------------------------------------------------------------

echo "---------------------------"
echo OS-X Stand-alone application
echo "---------------------------"

# backup current zip (to date stamped zip)
echo
echo cp ${ZIP_FILE_PATH}.zip ${ZIP_FILE_PATH}.${dd}.zip
cp ${ZIP_FILE_PATH}.zip ${ZIP_FILE_PATH}.${dd}.zip

# delete current zip
echo
echo rm ${ZIP_FILE_PATH}.zip
rm ${ZIP_FILE_PATH}.zip

# delete .jars inside standalone source directories
echo
echo rm -rf ${APP_ROOT}/Contents/Resources/Java/*.jar
rm -rf ${APP_ROOT}/Contents/Resources/Java/*.jar

# copy .jar files from new web-start deployment directories into standalone source directories 
echo
echo cp ${WEBAPPS_ROOT}/launch_oss/lib/*.jar ${APP_JAVA_ROOT}
cp ${WEBAPPS_ROOT}/launch_oss/lib/*.jar ${APP_JAVA_ROOT}

echo
echo cp ${WEBAPPS_ROOT}/launch_oss/Phase2UI.jar ${APP_JAVA_ROOT}
cp ${WEBAPPS_ROOT}/launch_oss/Phase2UI.jar ${APP_JAVA_ROOT}

# zipup standalone source directories to create downloadable .zip
echo
echo cd ${APP_ROOT}
cd ${APP_ROOT}
echo
echo zip -r LT_Phase2UI.app.zip ./LT_Phase2UI.app/
zip -r LT_Phase2UI.app.zip ./LT_Phase2UI.app/
