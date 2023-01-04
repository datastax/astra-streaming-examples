# Create a new namespace in an existing pulsar tenant
curl -sS --fail --location \
    --request PUT \
    --header "Authorization: Bearer $PULSAR_TOKEN" \
    "$WEB_SERVICE_URL/admin/v3/namespaces/$TENANT/$NAMESPACE"