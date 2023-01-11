# Create a new topic in an existing pulsar tenant
curl -sS --fail --location \
    --request PUT \
    --header "Authorization: Bearer $PULSAR_TOKEN" \
    "$WEB_SERVICE_URL/admin/v3/topics/$TENANT/$NAMESPACE/$TOPIC"