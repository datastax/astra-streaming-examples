// tag::build-client[]
using DotPulsar;
using DotPulsar.Extensions;

const string myTopic = "persistent://public/default/mytopic";

await using var client = PulsarClient.Builder()
                                     .Build();
// end::build-client[]

// tag::build-producer[]
await using var producer = client.NewProducer(Schema.String)
                                 .Topic(myTopic)
                                 .Create();
// end::build-producer[]

// tag::produce-message[]
_ = await producer.Send("Hello World"); // Send a message and ignore the returned MessageId
// end::produce-message[]

// tag::build-consumer[]
await using var consumer = client.NewConsumer(Schema.String)
                                 .SubscriptionName("MySubscription")
                                 .Topic(myTopic)
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