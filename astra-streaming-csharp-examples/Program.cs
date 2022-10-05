// See https://aka.ms/new-console-template for more information

using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text.Json;
using Pulsar.Client.Api;
using Pulsar.Client.Common;

const string SERVICE_URL = "pulsar+ssl://pulsar-gcp-uscentral1.streaming.datastax.com:6651";
const string ADMIN_URL = "https://pulsar-gcp-uscentral1.api.streaming.datastax.com";
const string TOPIC_NAME = "<my-super-cool-tenant>/astracdc/data-6109b1d9-0217-4783-809e-ed19d1299cbf-xxxxxx.yyyyyyy";
const string PULSAR_TOKEN = "YOUR PULSAR TOKEN";
const string SUBSCRIPTION_NAME = "my-subscription";

//Retrieve the schemas
var (keyAvroSchema, valueAvroSchema) = await GetSchema($"{ADMIN_URL}/admin/v2/schemas/{TOPIC_NAME}/schema");

//Build the avro readers with schema info
var keyAvroReader = new Avro.Generic.GenericDatumReader<Avro.Generic.GenericRecord>(keyAvroSchema, keyAvroSchema);
var valueAvroReader = new Avro.Generic.GenericDatumReader<Avro.Generic.GenericRecord>(valueAvroSchema, valueAvroSchema);

/*
 *
 * NOTE: We are using the F# Pulsar client below, but you could easily switch to the DotPulsar client. This is because the client isn't doing any decoding.
 *   It's simply listening for a (binary) message and making the bytes available. All the decoding work is being done by the Avro libs.
 */

// Create client object
var client = await new PulsarClientBuilder()
	.ServiceUrl(SERVICE_URL)
	.Authentication(
		AuthenticationFactory.Token(PULSAR_TOKEN)
	)
	.BuildAsync();

// Create consumer on a topic with a subscription
var consumer = await client.NewConsumer()
	.Topic(TOPIC_NAME)
	.SubscriptionName(SUBSCRIPTION_NAME)
	.SubscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
	.SubscribeAsync();
	
var receivedMsg = false;

while (!receivedMsg)
{
	var msg = await consumer.ReceiveAsync();

	if (msg is null)
	{
		continue;
	}

	//The Key is Base64 Encoded, so it needs to be decoded
	var msgKeyDecoded = Convert.FromBase64String(msg.Key);
	
	// Stream Pulsar's binary bytes to an avro decoder and load into a reader
	using (var keyBytes = new MemoryStream(msgKeyDecoded))
	{
		var keyDecoder = new Avro.IO.BinaryDecoder(keyBytes);
		var messageKey = keyAvroReader.Read(null!, keyDecoder);
		Console.WriteLine(@messageKey);
	}

	// Stream Pulsar's binary bytes to an avro decoder and load into a reader
	using (var valueBytes = new MemoryStream(msg.Data))
	{
		var valueDecoder = new Avro.IO.BinaryDecoder(valueBytes);
		var messageValue = valueAvroReader.Read(null!, valueDecoder);
		Console.WriteLine(@messageValue);
	}

	// Acknowledge the message to remove it from the message backlog
	await consumer.AcknowledgeAsync(msg.MessageId);

	receivedMsg = true;
}

//Close the consumer
await consumer.DisposeAsync();

// Close the client
await client.CloseAsync();

async Task<(Avro.Schema, Avro.Schema)> GetSchema(string schemaUrl)
{
	using HttpClient httpClient = new();
	httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue($"Bearer",$"{PULSAR_TOKEN}");
	using var response = await httpClient.GetAsync(schemaUrl);
	
	// Throw an exception if request was unsuccessful
	response.EnsureSuccessStatusCode();
	
	// Parse the response
	var responseBody = await response.Content.ReadFromJsonAsync<JsonElement>();
	var responseData = responseBody.GetProperty("data").GetString() ?? throw new Exception("Could not find a data property in json response");
	
	// The contents of the data element in the response is a string, so we deserialize it as an object
	var dataElement = JsonSerializer.Deserialize<JsonElement>(responseData);
	
	var keySchema = dataElement.GetProperty("key");
	var valueSchema = dataElement.GetProperty("value");
	
	try
	{
		var keyAvroSchema = Avro.Schema.Parse(keySchema.GetRawText());
		var valueAvroSchema = Avro.Schema.Parse(valueSchema.GetRawText());

		return (keyAvroSchema, valueAvroSchema);
	}
	catch (Exception)
	{
		Console.WriteLine($"An error occurred trying to parse json schemas to avro");
		throw;
	}
}