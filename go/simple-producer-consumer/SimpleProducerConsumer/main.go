// tag::build-client[]
package main

import (
	"context"
	"fmt"
	"github.com/apache/pulsar-client-go/pulsar"
	"log"
)

func main() {
	log.Println("Pulsar Producer")

	serviceUrl := "<REPLACE_WITH_SERVICE_URL>"
	pulsarToken := "<REPLACE_WITH_PULSAR_TOKEN>"

	tenantName := "<REPLACE_WITH_TENANT_NAME>"
	namespace := "<REPLACE_WITH_NAMESPACE>"
	topicName := "<REPLACE_WITH_TOPIC>"

	topic := fmt.Sprintf("persistent://%v/%v/%v", tenantName, namespace, topicName)

	token := pulsar.NewAuthenticationToken(pulsarToken)

	client, err := pulsar.NewClient(pulsar.ClientOptions{
		URL:            serviceUrl,
		Authentication: token,
	})

	if err != nil {
		log.Fatalf("Could not instantiate Pulsar client: %v", err)
	}

	defer client.Close()
// end::build-client[]

// tag::build-producer[]
	log.Printf("creating producer...")

	// Use the client to instantiate a producer
	producer, err := client.CreateProducer(pulsar.ProducerOptions{
		Topic: topic,
	})

	log.Printf("checking error of producer creation...")
	if err != nil {
		log.Fatal(err)
	}

	defer producer.Close()

	ctx := context.Background()
// end::build-producer[]

// tag::produce-message[]
	asyncMsg := pulsar.ProducerMessage{
		Payload: []byte(fmt.Sprintf("sent message")),
	}

	// Attempt to send the message asynchronously and handle the response
	producer.SendAsync(ctx, &asyncMsg, func(msgID pulsar.MessageID, msg *pulsar.ProducerMessage, err error) {
		if err != nil {
			log.Fatal(err)
		}

		log.Printf("the %s successfully published with the message ID %v", string(msg.Payload), msgID)
	})
// end::produce-message[]

// tag::build-consumer[]
	consumer, err := client.Subscribe(pulsar.ConsumerOptions{
		Topic:                       topic,
		SubscriptionName:            "examples-subscription",
		SubscriptionInitialPosition: pulsar.SubscriptionPositionEarliest,
	})

	if err != nil {
		log.Fatal(err)
	}

	defer consumer.Close()

	ctx = context.Background()
// end::build-consumer[]

// tag::receive-message[]
	msg, err := consumer.Receive(ctx)
	if err != nil {
		log.Fatal(err)
	} else {
		log.Printf("Received message : %s", string(msg.Payload()))
	}

	err = consumer.Ack(msg)
	if err != nil {
		log.Fatal(err)
		return
	}
// end::receive-message[]
}
