#!/bin/bash

######################################
# Description:
# 	Install Apache Pulsar
# Returns:
#   0: success
#   1: error
#######################################
function install(){
	wget https://archive.apache.org/dist/pulsar/pulsar-"${version}"/apache-pulsar-"${version}"-bin.tar.gz
  tar xvfz apache-pulsar-2.10.1-bin.tar.gz

	return 0
}

version=""

while [ $# -ne 0 ]
do
	name="$1"
	case "$name" in
		-v|--version|-[Vv]ersion)
				shift
				version="$1"
				;;
	esac

	shift
done

#INSTALL PULSAR - if not installed
#command -v pulsar-admin >/dev/null 2>&1 || install
#export PATH="${pwd}/apache-pulsar-2.10.1/bin:${PATH}"
# export PATH="/home/ddieruf/apache-pulsar-2.10.1/bin:${PATH}"