#!/bin/bash

# Outline installer for JGemini for Linux. There's no need to install --
#  you can just run "java -jar /path/to/jgemini-x.x.jar" from the 
#  command line. 
# If you do want to install, run this script from the main source directory, 
#  e.g., "sudo ./samples/install_linux.sh"

SHAREDIR=/usr/share
BINDIR=/usr/bin
VERSION=1.0
ICONDIR=${SHAREDIR}/icons/hicolor

APPDIR=$SHAREDIR/applications
MYSHAREDIR=${SHAREDIR}jgemini/

if [ -f pom.xml ]; then

  mkdir -p $MYSHAREDIR 
  cp binaries/jgemini-$VERSION.jar $MYSHAREDIR 
  cat << EOF > $BINDIR/jgemini 
  #!/bin/bash
  exec java -jar $MYSHAREDIR/jgemini-$VERSION.jar "\$@"
EOF

  chmod 755 $BINDIR/jgemini
  cp samples/jgemini.desktop $APPDIR

  convert -geometry 32x32 src/main/resources/images/jgemini.png ${ICONDIR}/32x32/apps/jgemini.png
  convert -geometry 48x48 src/main/resources/images/jgemini.png ${ICONDIR}/48x48/apps/jgemini.png
  convert -geometry 256x256 src/main/resources/images/jgemini.png ${ICONDIR}/256x256/apps/jgemini.png
  
else

  echo Run this from the source directory, e.g., 
  echo \"sudo ./samples/install_linux.sh\"

fi

