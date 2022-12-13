package com.datastax.astrastreaming.admin;

import org.apache.pulsar.client.api.*;
import java.io.IOException;

public class Topic {
    private readonly String _tenant;
    private readonly String _namespace;

    public Namespace(String tenant, String namespace){
        _tenant = tenant;
        _namespace = namespace;
    }

    public void Create(String name){
        
    }
}