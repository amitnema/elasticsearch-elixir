#!/bin/bash

set -euo pipefail

# Prevent accidental execution outside of Travis:
if [ -z "${TRAVIS+false}" ]
then
  echo "TRAVIS environment variable is not set"
  exit 1
fi

function jdk_switcher {
  DIR=$1
  if [ ! -d "$DIR" ]; then
    echo "Not found: $DIR"
    exit 1
  fi
  export JAVA_HOME="$DIR"
  export JDK_HOME="${JAVA_HOME}"
  export JAVAC="${JAVA_HOME}/bin/javac"
  export PATH="${JAVA_HOME}/bin:${PATH}"
}

# Switch to desired JDK, download if required:
function install_jdk {
  JDK_URL=$1

  FILENAME="${JDK_URL##*/}"

  rm -rf /tmp/jdk/$JDK
  mkdir -p /tmp/jdk/$JDK

  if [ ! -f "/tmp/jdk/$FILENAME" ]
  then
    curl -L $JDK_URL -o /tmp/jdk/$FILENAME
  fi

  tar -xzf /tmp/jdk/$FILENAME -C /tmp/jdk/$JDK --strip-components 1

  if [ -z "${2+false}" ]
  then
    jdk_switcher "/tmp/jdk/$JDK"
  fi
}

# Preinstalled JDKs:
ls -lA /usr/lib/jvm/


case "$JDK" in
6 | 7 | 8)
  jdk_switcher /usr/lib/jvm/java-8-oracle
  ;;
9)
  jdk_switcher /usr/lib/jvm/java-9-oracle
  ;;
10)
  install_jdk $JDK10_URL
  ;;
esac

# Do not use "~/.mavenrc" set by Travis (https://github.com/travis-ci/travis-ci/issues/3893),
export MAVEN_SKIP_RC=true

# Build:
case "$JDK" in
6 | 7 | 8 | 9)
  mvn -V -B -e clean org.jacoco:jacoco-maven-plugin:prepare-agent verify sonar:sonar -Djdk.version=${JDK} -Dbytecode.version=${JDK} -Decj=${ECJ:-}
  ;;
*)
  echo "Incorrect JDK [$JDK]"
  exit 1;
  ;;
esac
