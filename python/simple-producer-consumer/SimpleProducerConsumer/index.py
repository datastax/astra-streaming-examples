# tag::create-client[]
import pulsar

serviceUrl = "<REPLACE_WITH_SERVICE_URL>";
pulsarToken = "<REPLACE_WITH_PULSAR_TOKEN>";

tenantName = "<REPLACE_WITH_TENANT_NAME>";
namespace = "<REPLACE_WITH_NAMESPACE>";
topicName = "<REPLACE_WITH_TOPIC>";

topic = "persistent://{0}/{1}/{2}".format(tenantName, namespace, topicName)

client = pulsar.Client(serviceUrl, authentication=pulsar.AuthenticationToken(pulsarToken))
# end::create-client[]

# tag::create-producer[]
producer = client.create_producer(topic)
# end::create-producer[]

# tag::produce-message[]
producer.send('Hello World'.encode('utf-8'))
# end::produce-message[]

# tag::create-consumer[]
consumer = client.subscribe(topic, 'my-subscription')
# end::create-consumer[]

# tag::consume-message[]
waitingForMsg = True
while waitingForMsg:
    try:
        msg = consumer.receive(2000)
        print("Received message '{}' id='{}'".format(msg.data(), msg.message_id()))

        # Acknowledging the message to remove from message backlog
        consumer.acknowledge(msg)

        waitingForMsg = False
    except:
        print("Still waiting for a message...");

    time.sleep(1)
# end::consume-message[]

# tag::clean-up[]
client.close()
# end::clean-up[]