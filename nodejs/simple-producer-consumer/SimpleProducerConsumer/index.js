// tag::create-client[]
const Pulsar = require("pulsar-client");

(async () => {
  const serviceUrl = "<REPLACE_WITH_SERVICE_URL>";
  const pulsarToken = "<REPLACE_WITH_PULSAR_TOKEN>";

  const tenantName = "<REPLACE_WITH_TENANT_NAME>";
  const namespace = "<REPLACE_WITH_NAMESPACE>";
  const topicName = "<REPLACE_WITH_TOPIC>";

  const topic = `persistent://${tenantName}/${namespace}/${topicName}`;

  // Debian Ubuntu:
  const trustStore = '/etc/ssl/certs/ca-certificates.crt'
  // CentOS RHEL:
  // const trustStore = "/etc/ssl/certs/ca-bundle.crt";

  const auth = new Pulsar.AuthenticationToken({ token: pulsarToken });

  const client = new Pulsar.Client({
    serviceUrl: serviceUrl,
    authentication: auth,
    tlsTrustCertsFilePath: trustStore,
    operationTimeoutSeconds: 30,
  });
// end::create-client[]

// tag::create-producer[]
  const producer = await client.createProducer({
    topic: topic,
  });
// end::create-producer[]

// tag::produce-message[]
  producer.send({
    data: Buffer.from("Hello World"),
  });
  console.log("sent message");
// end::produce-message[]

// tag::cleanup-producer[]
  await producer.flush();
  await producer.close();
// end::cleanup-producer[]

// tag::create-consumer[]
  const consumer = await client.subscribe({
    topic: topic,
    subscription: subscriptionName,
    subscriptionType: "Exclusive",
    ackTimeoutMs: 10000,
  });
// end::create-consumer[]

// tag::consume-message[]
  const msg = await consumer.receive();
  console.log(msg.getData().toString());
  consumer.acknowledge(msg);
// end::consume-message[]

// tag::cleanup-consumer[]
  await consumer.close();
  await client.close();
// end::cleanup-consumer[]
})();