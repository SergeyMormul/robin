#Overview

Simple service to retrieve PDF based on XML via REST.

#Retrieval API

The service expects a `POST` request to ```/export``` with XML in the payload and returns a byte array of pdf.

Example payload
```javascript
{
    w = 1, // not currently supported, can be empty
    h = 1, // not currently supported, can be empty
    filename = 'File.pdf', // not currently supported, can be empty
    xml='<?xml version="1.0" encoding="UTF-8" ?> <note> <to>Tove</to> <from>Jani</from> <heading>Reminder</heading> <body>Do not forget me this weekend!</body> </note>'
}
```

If the `xml` field is empty or malformed the service returns a `BAD_REQUEST` exception.

# Example conversation

CURL to check service work
```
curl -XPOST 'http://localhost:8080/export' -d '{"xml":"<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <note> <to>Tove</to> <from>Jani</from> <heading>Reminder</heading> <body>Do not forget me this weekend!</body> </note>"}' -H 'Content-type:application/json'
```
