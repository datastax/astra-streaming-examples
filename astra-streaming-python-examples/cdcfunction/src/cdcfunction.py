from pulsar import Function
import base64
import io
import json
import re
import time
from urllib.request import Request, urlopen

import avro.schema

from urllib.request import Request, urlopen
from avro.io import BinaryDecoder, DatumReader

admin_url = "https://pulsar-gcp-uscentral1.api.streaming.datastax.com"
token = "YOUR PULSAR TOKEN"
topic_name = "njgcpuscentral/astracdc/data-6c36516e-ed75-4d17-be87-341b4595ff2b-ks1.tbl1"


def http_get(url):
    req = Request(url)
    req.add_header("Accept", "application/json")
    req.add_header("Authorization", "Bearer " + token)
    return urlopen(req).read()


def getSchema(logger):
    schema_url = "%s/admin/v2/schemas/%s/schema" % (admin_url, topic_name)
    topic_schema = http_get(schema_url).decode("utf-8")
    # This isn't great
    # the data part of the json has extra back slashes
    topic_schema = topic_schema.replace("\\", "")
    topic_schema = topic_schema.replace('data":"', 'data":')
    topic_schema = topic_schema.replace('}","properties', '},"properties')

    logger.info("Topic'{}' Schema='{}'".format(topic_name, topic_schema))

    schema_json = json.loads(topic_schema)

    data_schema = schema_json["data"]

    keyschema_json = data_schema["key"]
    valueschema_json = data_schema["value"]

    # the namespaces start with numbers and AVRO doesn't like it
    # so strip them out for now
    key_namespace = keyschema_json["namespace"]
    key_namespace = re.sub("\d.*_", "", key_namespace)
    keyschema_json["namespace"] = key_namespace

    value_namespace = valueschema_json["namespace"]
    value_namespace = re.sub("\d.*_", "", value_namespace)
    valueschema_json["namespace"] = value_namespace

    keyAvroSchema = avro.schema.parse(json.dumps(keyschema_json))
    valueAvroSchema = avro.schema.parse(json.dumps(valueschema_json))

    return keyAvroSchema, valueAvroSchema




class CDCExampleFunction(Function):

  def process(self, input, context):

    logger = context.get_logger()

    if 'keyAvroReader' not in globals():
      topic_name = context.get_current_message_topic_name()
      global keyAvroReader
      global valueAvroReader
      logger.info("Retrieving Schema for: {0}".format(topic_name))
      keyAvroSchema, valueAvroSchema = getSchema(logger)
      keyAvroReader = DatumReader(keyAvroSchema)
      valueAvroReader = DatumReader(valueAvroSchema)

    msgKey = context.get_message_key()
    msgKey_decoded = base64.b64decode(msgKey)
    logger.info("Got Message Key and decoded it: {0}".format(msgKey_decoded))

    messageKey_bytes = io.BytesIO(msgKey_decoded)
    keydecoder = BinaryDecoder(messageKey_bytes)
    msgkeydecoded = keyAvroReader.read(keydecoder)
    logger.info("Decoded Message Key is: {0}".format(msgkeydecoded))
    key_as_json_object=json.dumps(msgkeydecoded)
    logger.info("Message as json is: {0}".format(msgkeydecoded))


    logger.info("Input Message is: {0}".format(input))
    message_bytes = io.BytesIO(str.encode(input))
    decoder = BinaryDecoder(message_bytes)
    msgvalue = valueAvroReader.read(decoder)
    logger.info("Decoded Message is: {0}".format(msgvalue))
    msg_as_json_object  = json.dumps(msgvalue)
    logger.info("Message as json is: {0}".format(msgvalue))


    return msg_as_json_object






