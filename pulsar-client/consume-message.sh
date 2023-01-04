# Consume a new message on the given tenant/namespace/topic
./bin/pulsar-client consume \
    "$TENANT/$NAMESPACE/$TOPIC" \
    --subscription-name "examples-subscriber" \
    --num-messages 1