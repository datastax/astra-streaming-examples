const Pulsar = require("pulsar-client");
const https = require('https');
const avro = require('avro-js');


(async () => {
  // Token based on authentication
  const tokenStr =
    " YOUR PULSAR TOKEN ";
  const pulsarUri =
    "pulsar+ssl://pulsar-aws-useast2.streaming.datastax.com:6651";
  const adminUrl = "https://pulsar-aws-useast2.api.streaming.datastax.com";
  const topicName =
    "njcdcawsuseast2/astracdc/data-6ee78bd3-78af-4ddd-be73-093f38d094bd-ks1.tbl1";
  const subscriptionName = "my-subscription21";


  // CentOS RHEL:
//   const trustStore = "/etc/ssl/certs/ca-bundle.crt";
  // Debian Ubuntu:
  // const trustStore = '/etc/ssl/certs/ca-certificates.crt'
//   const trustStore = "/System/Library/Keychains/SystemCACertificates.keychain";

  const auth = new Pulsar.AuthenticationToken({ token: tokenStr });

  let url = adminUrl + "/admin/v2/schemas/" + topicName + "/schema"
  var options = {
      'headers' : {
        'Authorization': 'Bearer ' + tokenStr
      }
  }

  var topicSchema;
  var keyAvroSchema;
  var valAvroSchema;



  https.get(url,options, (res) => {
    var chunks = [];
  
    res.on("data", function (chunk) {
      chunks.push(chunk);
    });
  
    res.on("end", function (chunk) {
      var body = Buffer.concat(chunks);
      var bodyString = body.toString()
      console.log(bodyString);
      var replace = bodyString.replace(/\\/g,'');

      replace = replace.replace(/"{/g,'{');
      replace = replace.replace(/}"/g,'}');
      console.log(replace);
      topicSchema = JSON.parse(replace);

      var keySchema = topicSchema.data.key;
      var ns = keySchema.namespace.replace(/\d.*_/,'');
      keySchema.namespace=ns;
      keyAvroSchema = avro.parse(keySchema);

      var valSchema = topicSchema.data.value;
      var valns = valSchema.namespace.replace(/\d.*_/,'');
      valSchema.namespace=valns;
      valAvroSchema = avro.parse(valSchema);


      
    });
  
    res.on("error", function (error) {
      console.error(error);
    });
  });

  // Create a client
  const client = new Pulsar.Client({
    serviceUrl: pulsarUri,
    authentication: auth,
    // tlsTrustCertsFilePath: trustStore,
    operationTimeoutSeconds: 30,
  });

  // Create consumer
  const consumer = await client.subscribe({
    topic: "persistent://"+topicName,
    subscription: subscriptionName,
    subscriptionType: "KeyShared",
    subscriptionInitialPosition: "Earliest",
    ackTimeoutMs: 10000,
  });

  // Receive and acknowledge messages
  for (let i = 0; i < 100; i += 1) {
    const msg = await consumer.receive();

    // The PartitionKey is Base64 Encoded, so it needs to be decoded
    let partitionKey = msg.getPartitionKey()
    let buff = new Buffer(partitionKey,'base64')
    let decodedPartitionKey = buff.toString('ascii')
    console.log(decodedPartitionKey);

    let decodedKey = keyAvroSchema.fromBuffer(buff)
    console.log(decodedKey);

    let decodedVal = valAvroSchema.fromBuffer(msg.getData())
    console.log(decodedVal);

    consumer.acknowledge(msg);
  }

  await consumer.close();
  await client.close();
})();
