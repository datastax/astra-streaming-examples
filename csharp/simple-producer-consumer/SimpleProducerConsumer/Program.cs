// tag::build-client[]
using DotPulsar;
using DotPulsar.Extensions;

var serviceUrl = "<REPLACE_WITH_SERVICE_URL>";
var pulsarToken = "<REPLACE_WITH_PULSAR_TOKEN>";

var tenantName = "<REPLACE_WITH_TENANT_NAME>";
var nmspace = "<REPLACE_WITH_NAMESPACE>";
var topicName = "<REPLACE_WITH_TOPIC>";

var topic = $"persistent://{tenantName}/{nmspace}/{topicName}";

await using var client = PulsarClient.Builder()
                                      .serviceUrl(serviceUrl)
                                      .authentication(
                                        AuthenticationFactory.token(pulsarToken)
                                      )
                                      .Build();
// end::build-client[]

// tag::build-producer[]
await using var producer = client.NewProducer(Schema.String)
                                 .Topic(topic)
                                 .Create();
// end::build-producer[]

// tag::produce-message[]
_ = await producer.Send("Hello World"); // Send a message and ignore the returned MessageId
Console.WriteLine("Sent message");
// end::produce-message[]

// tag::build-consumer[]
await using var consumer = client.NewConsumer(Schema.String)
                                 .SubscriptionName("examples-subscription")
                                 .Topic(topic)
                                 .InitialPosition(SubscriptionInitialPosition.Earliest)
                                 .Create();
// end::build-consumer[]

// tag::consumer-loop[]
await foreach (var message in consumer.Messages())
{
    Console.WriteLine($"Received: {message.Value()}");
    await consumer.Acknowledge(message);
}
// end::consumer-loop[]