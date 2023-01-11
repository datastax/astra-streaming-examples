package com.datastax.astrastreaming.javaexamples.functions;


import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.pulsar.client.api.schema.GenericRecord;
import org.apache.pulsar.client.api.schema.GenericObject;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;
import org.apache.pulsar.common.schema.KeyValue;
import com.datastax.astrastreaming.javaexamples.utils.JsonConverter;;


/**
 * Function that Consumes a CDC data topic and ouputs it as a json string
 */
public class CDCFunction implements Function<GenericObject, String> {

    

    @Override
    public String process(GenericObject input, Context context) {
        try {
            KeyValue<GenericRecord, GenericRecord> keyValue = (KeyValue<GenericRecord, GenericRecord>) input.getNativeObject();

            GenericRecord keyGenObject = keyValue.getKey();
            GenericRecord valGenObject = keyValue.getValue();

           
            ObjectNode keyNode = JsonConverter.toJson((org.apache.avro.generic.GenericRecord) keyGenObject.getNativeObject());
            ObjectNode valNode = JsonConverter.toJson((org.apache.avro.generic.GenericRecord) valGenObject.getNativeObject());

            keyNode.setAll(valNode);

            String json = keyNode.toPrettyString();
            return json;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    

  
}
    

