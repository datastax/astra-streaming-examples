// tag::init-app[]
package org.example;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class App {
  private static final String username = "";
  private static final String password = "<REPLACE_WITH_PULSAR_TOKEN>";
  private static final String host = "<REPLACE_WITH_SERVICE_URL>";
  private static final int port = 5671;
  private static final String virtual_host = "<REPLACE_WITH_TENANT_NAME>/<REPLACE_WITH_NAMESPACE>"; //The "rabbitmq" namespace should have been created when you enabled S4R
  private static final String queueName = "<REPLACE_WITH_TOPIC_NAME>"; //This will get created automatically
  private static final String amqp_URI = String.format("amqps://%s:%s@%s:%d/%s", username, password, host, port, virtual_host.replace("/","%2f"));

  public static void main(String[] args) throws IOException, TimeoutException, URISyntaxException, NoSuchAlgorithmException, KeyManagementException, InterruptedException {
// end::init-app[]

// tag::create-queue[]
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri(amqp_URI);

    /*
    You could also set each value individually
      factory.setHost(host);
      factory.setPort(port);
      factory.setUsername(username);
      factory.setPassword(password);
      factory.setVirtualHost(virtual_host);
      factory.useSslProtocol();
     */

    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.queueDeclare(queueName, false, false, false, null);
// end::create-queue[]

// tag::build-producer[]
    String publishMessage = "Hello World!";
    channel.basicPublish("", queueName, null, publishMessage.getBytes());
    System.out.println(" Sent '" + publishMessage + "'");
// end::build-producer[]

// tag::build-consumer[]
    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      String consumeMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);
      System.out.println(" Received '" + consumeMessage + "'");
    };

    channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

    Thread.sleep(4000); // wait a bit for messages to be received

    channel.close();
    connection.close();
  }
}
// end::build-consumer[]