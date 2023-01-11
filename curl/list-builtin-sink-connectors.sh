curl -sS --fail --location --request GET \
  -H "Authorization: $PULSAR_TOKEN" \
  "$WEB_SERVICE_URL/admin/v3/sinks/builtinsinks"