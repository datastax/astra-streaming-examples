import base64
import json
import io
import avro.schema
from avro.io import BinaryDecoder, DatumReader
#
from pulsar import Function


## helpers
keySchemaDict = {
    "type": "record",
    "name": "reviews",
    "fields": [
        {
            "name": "hotel",
            "type": "string",
        },
        {
            "name": "id",
            "type": [
                "null",
                {
                    "type": "string",
                    "logicalType": "uuid",
                },
            ],
            "default": None,
        },
    ],
}

valueSchemaDict = {
    "type": "record",
    "name": "reviews",
    "fields": [
        {
            "name": "body",
            "type": [
                "null",
                "string"
            ],
            "default": None
        },
        {
            "name": "reviewer",
            "type": [
                "null",
                "string"
            ],
            "default": None
        },
        {
            "name": "is_valid",
            "type": [
                "null",
                "boolean"
            ],
            "default": None
        },
        {
            "name": "score",
            "type": [
                "null",
                "int"
            ],
            "default": None
        }
    ]
}


def createAvroReader(schemaDict):
    return DatumReader(avro.schema.make_avsc_object(schemaDict))


def bytesToReadDict(by, avroReader):
    binDecoded = BinaryDecoder(io.BytesIO(by))
    return avroReader.read(binDecoded)


def b64ToReadDict(b64string, avroReader):
    b64Decoded = base64.b64decode(b64string)
    return bytesToReadDict(b64Decoded, avroReader)


def cdcMessageToDictPF(pk, body, keyReader, valueReader):
    # Body can be a 'str' or 'bytes' already depending on the length
    # of the input fields in the table insert.
    #       ¯\_(ツ)_/¯
    # Take care of this:
    encodedBody = body if isinstance(body, bytes) else body.encode()
    #
    return {
        **bytesToReadDict(base64.b64decode(pk), keyReader),
        **bytesToReadDict(encodedBody, valueReader),
    }


class Deschemaer(Function):

    def __init__(self):
        self.keyReader = createAvroReader(keySchemaDict)
        self.valueReader = createAvroReader(valueSchemaDict)

    def process(self, msgBody, context):
        msgPK = context.get_partition_key()
        msgDict = cdcMessageToDictPF(msgPK, msgBody, self.keyReader, self.valueReader)
        return json.dumps(msgDict)
