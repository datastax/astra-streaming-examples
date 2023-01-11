# Create a namespace within a tenant in the "astra-streaming-examples" astra org
astra streaming pulsar-shell \
    --config "astra-streaming-examples" \
    "$TENANT" \
    -e "admin namespaces create $NAMESPACE"