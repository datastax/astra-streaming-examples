# Create a new persistent topic in an existing pulsar tenant/namespace
./bin/pulsar-admin topics create persistent://${TENANT}/${NAMESPACE}/${TOPIC}

# Or create a non-persistent topic
#./bin/pulsar-admin topics create non-persistent://${TENANT}/${NAMESPACE}/${TOPIC}