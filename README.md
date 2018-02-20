#Overview

Simple service to retrieve PDF based on XML via REST.

#Retrieval API

The service expects a `POST` request to ```/export``` with XML in the payload and returns a byte array of pdf.

Example payload
```javascript
{
    'w' : '1' // greater than 0, required
    'h' : '1' // greater than 0, required
    'filename' : 'NameOfFile' // required
    'format' : 'pdf' // required
    'bg': 'ffffff' // background colour, optional for pdf
    'xml': '<something>...</something>' // XML string to transfer into pdf
}
```
