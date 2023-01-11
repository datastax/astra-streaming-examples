package com.datastax.astrastreaming.javaexamples.consumers;

import org.apache.pulsar.client.api.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.pulsar.client.api.schema.GenericRecord;
import org.apache.pulsar.common.schema.KeyValue;

public class CDCConsumer {

        private static final String SERVICE_URL = "pulsar+ssl://pulsar-aws-useast2.streaming.datastax.com:6651";
        private static final String TOPIC_NAME = "njcdcawsuseast2/astracdc/data-6ee78bd3-78af-4ddd-be73-093f38d094bd-ks1.tbl1";
        private static final String PULSAR_TOKEN = "YOUR PULSAR TOKEN";
        private static final String SUBSCRIPTION_NAME = "my-subscription";


        public static void main(String[] args) throws IOException
        {

            // Create client object
            PulsarClient client = PulsarClient.builder()
                    .serviceUrl(SERVICE_URL)
                    .authentication(
                        AuthenticationFactory.token(PULSAR_TOKEN)
                    )
                    .build();

            // Create consumer on a topic with a subscription
            Consumer<GenericRecord> consumer = client.newConsumer(Schema.AUTO_CONSUME())
                    .topic(TOPIC_NAME)
                    .subscriptionName(SUBSCRIPTION_NAME)
                    .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                    .subscribe();

            boolean receivedMsg = false;
            // Loop until a message is received
            do {
                // Block for up to 1 second for a message
                Message<GenericRecord> msg = consumer.receive(1, TimeUnit.SECONDS);

                if(msg != null){
                
                    GenericRecord input = msg.getValue();
                    //CDC Uses KeyValue Schema 
                    KeyValue<GenericRecord, GenericRecord> keyValue = (KeyValue<GenericRecord, GenericRecord>) input.getNativeObject();

                    GenericRecord keyGenRecord = keyValue.getKey();
                    displayGenericRecordFields("key",keyGenRecord);

                    GenericRecord valGenRecord = keyValue.getValue();
                    displayGenericRecordFields("value",valGenRecord);
                   
                    // Acknowledge the message to remove it from the message backlog
                    consumer.acknowledge(msg);

                    receivedMsg = true;
                }

            } while (!receivedMsg);

            //Close the consumer
            consumer.close();

            // Close the client
            client.close();

        }

        private static void displayGenericRecordFields(String recordName,GenericRecord genericRecord) {
            System.out.printf("---Fields in %s: ", recordName);
            genericRecord.getFields().stream().forEach((fieldtmp) -> 
                System.out.printf("%s=%s",fieldtmp.getName(),genericRecord.getField(fieldtmp) ));
            System.out.println(" ---");
        }

}


