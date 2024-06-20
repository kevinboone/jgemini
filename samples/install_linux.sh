#!/bin/bash

# Outline installer for JGemini for Linux. There's no need to install --
#  you can just run "java -jar /path/to/jgemini-x.x.jar" from the 
#  command line. 
# If you do want to install, run this script from the main source directory, 
#  e.g., "sudo ./samples/install_linux.sh"

#DESTDIR=/
SHAREDIR=/usr/share
BINDIR=/usr/bin
ETCDIR=/etc
VERSION=1.0
ICONDIR=${SHAREDIR}/icons/hicolor

APPDIR=$SHAREDIR/applications
MYSHAREDIR=${SHAREDIR}/jgemini

if [ -f pom.xml ]; then

  mkdir -p $DESTDIR/$MYSHAREDIR 
  mkdir -p $DESTDIR/$BINDIR 
  mkdir -p $DESTDIR/$ICONDIR
  mkdir -p $DESTDIR/$SHAREDIR
  mkdir -p $DESTDIR/$APPDIR
  mkdir -p $DESTDIR/$ICONDIR/32x32/apps
  mkdir -p $DESTDIR/$ICONDIR/48x48/apps
  mkdir -p $DESTDIR/$ICONDIR/256x256/apps
  mkdir -p $DESTDIR/$ETCDIR/jgemini
  cp -p samples/*.properties $DESTDIR/$ETCDIR/jgemini/
  cp binaries/jgemini-$VERSION.jar $DESTDIR/$MYSHAREDIR 
  cat << EOF > $DESTDIR/$BINDIR/jgemini 
  #!/bin/bash
  exec java -jar $DESTDIR/$MYSHAREDIR/jgemini-$VERSION.jar "\$@"
EOF

  chmod 755 $DESTDIR/$BINDIR/jgemini
  cp samples/jgemini.desktop /$DESTDIR/$APPDIR/

  convert -geometry 32x32 src/main/resources/images/jgemini.png $DESTDIR/${ICONDIR}/32x32/apps/jgemini.png
  convert -geometry 48x48 src/main/resources/images/jgemini.png $DESTDIR/${ICONDIR}/48x48/apps/jgemini.png
  convert -geometry 256x256 src/main/resources/images/jgemini.png $DESTDIR/${ICONDIR}/256x256/apps/jgemini.png
  
else

  echo Run this from the source directory, e.g., 
  echo \"sudo ./samples/install_linux.sh\"

fi

