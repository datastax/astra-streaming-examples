// tag::build-client[]
package org.example;

import org.apache.pulsar.client.api.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class App
{
  private static final String serviceUrl = "<REPLACE_WITH_SERVICE_URL>";
  private static final String pulsarToken = "<REPLACE_WITH_PULSAR_TOKEN>";
  private static final String tenantName = "<REPLACE_WITH_TENANT_NAME>";
  private static final String namespace = "<REPLACE_WITH_NAMESPACE>";
  private static final String topicName = "<REPLACE_WITH_TOPIC>";

  private static final String topic = String.format("persistent://%s/%s/%s", tenantName,namespace,topicName);

  public static void main( String[] args ) throws IOException
  {
    PulsarClient client = PulsarClient.builder()
                                      .serviceUrl(serviceUrl)
                                      .authentication(
                                          AuthenticationFactory.token(pulsarToken)
                                      )
                                      .build();
// end::build-client[]

// tag::build-producer[]
    Producer<String> producer = client.newProducer(Schema.STRING)
                              .topic(topic)
                              .create();
// end::build-producer[]

// tag::produce-message[]
    producer.send("Hello World");
// end::produce-message[]

// tag::close-producer[]
    producer.close();
// end::close-producer[]

// tag::build-consumer[]
    Consumer<String> consumer = client.newConsumer(Schema.STRING)
        .topic(topic)
        .subscriptionName("my-subscription")
        .subscribe();
// end::build-consumer[]

// tag::consumer-loop[]
    boolean receivedMsg = false;

    do {
      // Block for up to 1 second for a message
      Message<String> msg = consumer.receive(1, TimeUnit.SECONDS);

      if(msg != null){
        System.out.printf("Message received: %s", new String(msg.getData()));

        // Acknowledge the message to remove it from the message backlog
        consumer.acknowledge(msg);

        receivedMsg = true;
      }

    } while (!receivedMsg);
// end::consumer-loop[]

// tag::close-consumer[]
    consumer.close();
// end::close-consumer[]

// tag::close-client[]
    client.close();
// end::close-client[]

  }
}