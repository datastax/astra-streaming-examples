# Enhanced Astra Streaming Metrics Scraping Configuration

This Astra streaming doc ([link](https://docs.datastax.com/en/streaming/astra-streaming/operations/astream-scrape-metrics.html#get-configuration-file-from-astra-streaming)) specifies how to configure Prometheus to scrape the Astra streaming metrics. The scraped metrics is actually at the partition level, not at the parent topic level. See the metrics label of `topic="persistent://msgenrich/testns/raw-partition-0"` in the following example of one scraped `pulsar_msg_backlog` metrics.
```
pulsar_msg_backlog{app="pulsar", cluster="pulsar-gcp-uscentral1", component="broker", controller_revision_hash="pulsar-gcp-uscentral1-broker-787c64647f", exported_instance="10.32.24.4:8080", exported_job="broker", helm_release_name="astraproduction-gcp-pulsar-uscentral1", instance="prometheus-gcp-uscentral1.streaming.datastax.com:443", job="astra-pulsar-metrics-msgenrich", kubernetes_namespace="pulsar", kubernetes_pod_name="pulsar-gcp-uscentral1-broker-3", namespace="msgenrich/testns", prometheus="pulsar/astraproduction-gcp-pulsar-prometheus", prometheus_replica="prometheus-astraproduction-gcp-pulsar-prometheus-0", ptopic="persistent://msgenrich/testns/raw", pulsar_cluster_dns="gcp-uscentral1.streaming.datastax.com", release="astraproduction-gcp-pulsar-uscentral1", statefulset_kubernetes_io_pod_name="pulsar-gcp-uscentral1-broker-3", topic="persistent://msgenrich/testns/raw-partition-0"}
```

The partition level metrics is not really useful for end users who are more interested in the parent topic (e.g. `persistent://msgenrich/testns/raw`). In order to make it easier end users to do for further-down metrics analysis such as metrics aggregation visualization through Grafana dashboards, it is recommended to add a new label to indicate the parent topic, `ptopic` for each scraped metrics. 

This can be done via the `metrics_relabel_configs` setting in the Prometheus scraping job.
```
- job_name: '<job_name>'
  scheme: 'https'
  metrics_path: '<scrape_metrics_path>'
  authorization:
    credentials: <jwt_token_value>
  static_configs:
  - targets: ['<scrape_endpoint>']
  metric_relabel_configs:
  - source_labels: [topic]
    regex: (.*)(-partition.*)
    replacement: $1
    target_label: ptopic
```

Please **NOTE** one of the provided Grafana dashboards, `as-topic.json`, is constructed using this approach.