// tag::init-app[]
package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class App {
  private static String bootstrapServers = "kafka-gcp-uscentral1.streaming.datastax.com:9093";
  //"<REPLACE_WITH_BOOTSTRAP_SERVER_URL>";
  private static String pulsarToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE2NzM1MzkzMDIsImlzcyI6ImRhdGFzdGF4Iiwic3ViIjoiY2xpZW50OzM0ZDBkZjZiLTRlMTEtNDgzMi1hOWExLTRlZmI3NTc1MThkNDtiWGt0ZEdWdVlXNTBMVEF3Tnc9PTs5OTU2MDI5N2QzIiwidG9rZW5pZCI6Ijk5NTYwMjk3ZDMifQ.dqkh7btrvbVItng43-xcF-V5LWoo0x5YlyY-zfpZoNUqIUfww2UvBsMi4oxdk9iNj7vJIR07Pp62h-B0QUYZlEhgYEdcS6aFd0juajhMY90wGiV2CqjU0jyUXdJYNtUbav4UdugAkDRhqrI7CxRN3m5iaE4fpTRTdc1F_TRo0Nuqe9miS7nBXp9lEff9KUtHIEnuEy_AmxnJPAp6Xf5pJv6Qg-dsLDSp-a4iOlYl9XZ04C1mDU8BthtXR6Ls6_G0EP0xzQC7O74RFEZ1LY994bWrGtI9OcXrrjNDZUxCFqCDmrdymcxZ1PhuW2kLdTwmxctp1GKPX8joNS3wq0Q2dg";
  //"<REPLACE_WITH_PULSAR_TOKEN>";
  private static String tenantName = "my-tenant-007";
  //"<REPLACE_WITH_TENANT_NAME>";
  private static final String namespace = "my-namespace";
  //"<REPLACE_WITH_NAMESPACE>";
  private static final String topicName = "my-topic";
  //"<REPLACE_WITH_TOPIC>";
  private static final String topic = String.format("persistent://%s/%s/%s", tenantName,namespace,topicName);

  public static void main(String[] args) {
// end::init-app[]
// tag::build-config[]
    Properties config = new Properties();
    config.put("bootstrap.servers",bootstrapServers);
    config.put("security.protocol","SASL_SSL");
    config.put("sasl.jaas.config", String.format("org.apache.kafka.common.security.plain.PlainLoginModule required username='%s' password='token:%s';", tenantName, pulsarToken));
    config.put("sasl.mechanism","PLAIN");
    config.put("session.timeout.ms","45000");
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    config.put("group.id", "my-consumer-group");
// end::build-config[]

// tag::build-producer[]
    KafkaProducer<Long, String> producer = new KafkaProducer<>(config);

    final ProducerRecord<Long, String> producerRecord = new ProducerRecord<>(topic, System.currentTimeMillis(), "Hello World");
    producer.send(producerRecord, new Callback() {
      public void onCompletion(RecordMetadata metadata, Exception e) {
        if (e != null)
          System.out.println(String.format("Send failed for record, %s. \nRecord data: %s",e.getMessage(), producerRecord));
        else
          System.out.println("Successfully sent message");
      }
    });

    producer.flush();
    producer.close();
// end::build-producer[]

// tag::build-consumer[]
    final KafkaConsumer<Integer, String> consumer = new KafkaConsumer<Integer, String>(config);

    consumer.subscribe(Collections.singletonList(topic));
    ConsumerRecords<Integer, String> consumerRecords = consumer.poll(Duration.ofMillis(5000));

    System.out.println(String.format("Found %d total record(s)", consumerRecords.count()));

    for (ConsumerRecord<Integer, String> consumerRecord : consumerRecords) {
      System.out.println(consumerRecord);
    }

    consumer.commitSync();
    consumer.close();
  }
}
// end::build-consumer[]