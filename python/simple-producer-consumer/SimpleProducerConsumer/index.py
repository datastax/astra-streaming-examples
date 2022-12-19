import pulsar

serviceUrl = "<REPLACE_WITH_SERVICE_URL>";
pulsarToken = "<REPLACE_WITH_PULSAR_TOKEN>";

tenantName = "<REPLACE_WITH_TENANT_NAME>";
namespace = "<REPLACE_WITH_NAMESPACE>";
topicName = "<REPLACE_WITH_TOPIC>";

topic = "persistent://{0}/{1}/{2}".format(tenantName, namespace, topicName)

client = pulsar.Client(serviceUrl, authentication=pulsar.AuthenticationToken(pulsarToken))


producer = client.create_producer(topic)

producer.send('Hello World'.encode('utf-8'))

consumer = client.subscribe(topic, 'my-subscription')

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

client.close()

client.close()
