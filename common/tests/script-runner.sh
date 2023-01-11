#!/bin/bash

# Silence pushd and popd
pushd () {
    command pushd "$@" > /dev/null
}
popd () {
    command popd "$@" > /dev/null
}

######################################
# Returns:
#   0: success
#   <non-zero>: error
#######################################
function runScript() {
	export TENANT="${1}"
  export NAMESPACE="${2}"
  export INPUT_TOPIC="${3}"
  export DESINATION_TOPIC="${3}"
  export SINK_NAME="${4}"
  export SOURCE_NAME="${4}"
  export WEB_SERVICE_URL="${5}"
  export ASTRA_STREAMING_TOKEN="${6}"
  local scriptPath="${7}"

  [[ ! -f "${scriptPath}" ]] && echo "No script was found at path '${scriptPath}'" && return 1

  if ! ret=$(exec "${scriptPath}" 2>&1 | head -n 2);
  then
    echo "${ret}"
    return 1
  fi

  if [[ -z ${ret} || ${ret,,} =~ "success" || ${ret,,} =~ "{" ]]; then
    return 0
  fi

  echo "$ret"
	return 1
}