#!/bin/bash

set -e
set -o errtrace

export ROOT_FOLDER="$( pwd )"
export THIS_FOLDER="$( dirname "${BASH_SOURCE[0]}" )"

while [ $# -ne 0 ]; do
	name="$1"
	case "$name" in
	--tenant)
		shift
		TENANT="$1"
		;;
	--namespace)
		shift
		NAMESPACE="$1"
		;;
	--topic)
		shift
		TOPIC="$1"
		;;
	--webserviceurl)
		shift
		WEB_SERVICE_URL="$1"
		;;
	--pulsartoken)
		shift
		PULSAR_TOKEN="$1"
		;;
	esac

	shift
done

#######################################
#       Validate required
#######################################
[[ -z $TENANT ]] && (echo "TENANT is a required value" && exit 1)
[[ -z $NAMESPACE ]] && (echo "NAMESPACE is a required value" && exit 1)
[[ -z ${TOPIC} ]] && (echo "TOPIC is a required value" && exit 1)
[[ -z $WEB_SERVICE_URL ]] && (echo "WEB_SERVICE_URL is a required value" && exit 1)
[[ -z ${PULSAR_TOKEN} ]] && (echo "PULSAR_TOKEN is a required value" && exit 1)

#######################################
#       Source needed functions
#######################################
source "${ROOT_FOLDER}/common/tests/install-pulsar.sh" --version "2.10.1"
source "${ROOT_FOLDER}/common/tests/script-runner.sh"

#######################################
#       Begin task
#######################################
#set -x #echo all commands
echo "--------------------------------------------------------"
echo "Testing config.sh"
echo "--------------------------------------------------------"

echo "  Running script"
if ! ret=$(runScript "$TENANT" \
                "$NAMESPACE" \
                "${TOPIC}" \
                "${sinkName}" \
                "" \
                "" \
                "./astra-cli/config.sh"  2>&1); then (echo "    ERROR: $ret" && exit 1) fi

echo "  Validating action"
[[ "astra-streaming-example" != "$(astra -o json config list | jq .[1])"]] && echo "    ERROR: expected config section not found" && exit 1

echo "--------------------------------------------------------"
echo "Testing create-namespace.sh"
echo "--------------------------------------------------------"

echo "  Running script"
if ! ret=$(runScript "$TENANT" \
                "$NAMESPACE" \
                "${TOPIC}" \
                "${sinkName}" \
                "" \
                "" \
                "./astra-cli/create-namespace.sh"  2>&1); then (echo "    ERROR: $ret" && exit 1) fi

echo "  Validating action"
[[ "$NAMESPACE" != "$(astra -o json streaming namespace list | jq .[1])"]] && echo "    ERROR: expected namespace not found" && exit 1

echo "All tests passed"