package com.datastax.astrastreaming.admin;

import org.apache.pulsar.client.api.*;
import java.io.IOException;

public class Namespace {
    private readonly String _tenant;

    public Namespace(String tenant){
        _tenant = tenant;
    }

    public void Create(String namespace){
        admin.namespaces().createNamespace(namespace);
    }
}