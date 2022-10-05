package main

// import (
// 	"context"
// 	"log"
// 	"encoding/base64"
// 	"encoding/json"
// 	"github.com/linkedin/goavro/v2"

// 	"github.com/apache/pulsar-client-go/pulsar"

// )

// type KeyValueSchema struct {
// 	SchemaInfo pulsar.SchemaInfo
// 	KeyCodec pulsar.AvroCodec
// 	KeySchemaInfo pulsar.SchemaInfo
// 	ValueCode pulsar.AvroCodec
// 	ValueSchemaInfo pulsar.SchemaInfo
// }

// func NewKeyValueSchema(keyvalueSchemaDef string, properties map[string]string) *KeyValueSchema {
// 	kvs := new(KeyValueSchema)

// 	kvs.SchemaInfo.Schema = keyvalueSchemaDef
// 	kvs.SchemaInfo.Type = pulsar.KeyValue
// 	kvs.SchemaInfo.Properties = properties
// 	kvs.SchemaInfo.Name = "KeyValue"

// 	return kvs
// }
