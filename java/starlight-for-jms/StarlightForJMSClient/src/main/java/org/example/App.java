// tag::init-app[]
package org.example;

import com.datastax.oss.pulsar.jms.PulsarConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import java.util.HashMap;
import java.util.Map;

public class App
{
  private static String webServiceUrl = "<REPLACE_WITH_WEB_SERVICE_URL>";
  private static String brokerServiceUrl = "<REPLACE_WITH_BROKER_SERVICE_URL>";
  private static String pulsarToken = "<REPLACE_WITH_PULSAR_TOKEN>";
  private static String tenantName = "<REPLACE_WITH_TENANT_NAME>";
  private static final String namespace = "<REPLACE_WITH_NAMESPACE>";
  private static final String topicName = "<REPLACE_WITH_TOPIC_NAME>";
  private static final String topic = String.format("persistent://%s/%s/%s", tenantName,namespace,topicName);
  public static void main( String[] args ) throws Exception
  {
// end::init-app[]
// tag::build-config[]
    Map<String, Object> properties = new HashMap<>();
    properties.put("webServiceUrl",webServiceUrl);
    properties.put("brokerServiceUrl",brokerServiceUrl);
    properties.put("authPlugin","org.apache.pulsar.client.impl.auth.AuthenticationToken");
    properties.put("authParams",pulsarToken);
// end::build-config[]
// tag::build-factory[]
    try (PulsarConnectionFactory factory = new PulsarConnectionFactory(properties); ){
      JMSContext context = factory.createContext();
      Queue queue = context.createQueue(topic);

      context.createConsumer(queue).setMessageListener(new MessageListener() {
        @Override
        public void onMessage(Message message) {
          try {
            System.out.println("Received: " + message.getBody(String.class));
          } catch (Exception err) {
            err.printStackTrace();
          }
        }
      });

      String message = "Hello there!";
      System.out.println("Sending: "+message);
      context.createProducer().send(queue, message);

      Thread.sleep(4000); //wait for the message to be consumed
    }
  }
}
// end::build-factory[]