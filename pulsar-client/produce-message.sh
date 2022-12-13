# Produce a new message on the given tenant/namespace/topic
./bin/pulsar-client produce \
    ${TENANT}/${NAMESPACE}/${TOPIC} \
    --messages "Hi there" \
    --num-produce 1