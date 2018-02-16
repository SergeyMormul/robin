package com.sciforce.robin

import javax.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.xhtmlrenderer.pdf.ITextRenderer

/**
 * Inbound HTTP gateway for supporting generation PDF from XML.
 */
@RestController
class ExportPDFService {

    @PostMapping( path = '/export' )
    ResponseEntity<byte[]> generatePDF( @RequestBody @Valid HypermediaControl control, Errors errors ) {
        errors.hasErrors() ? new ResponseEntity<byte[]>( [] as byte[], HttpStatus.BAD_REQUEST ) : constructPDF( control )
    }

    private static ResponseEntity<byte[]> constructPDF( final HypermediaControl control ) {
        if ( control.xml ) {
            try {
                def pdfBytes = new ByteArrayOutputStream().withStream { ByteArrayOutputStream stream ->
                    def renderer = new ITextRenderer()
                    renderer.setDocumentFromString(control.xml)
                    renderer.layout()
                    renderer.createPDF(stream)
                    stream.toByteArray()
                }
                new ResponseEntity<byte[]>(pdfBytes, HttpStatus.ACCEPTED)
            }
            catch ( Exception e ) {
                new ResponseEntity<byte[]>( [] as byte[], HttpStatus.BAD_REQUEST )
            }
        }
        else { new ResponseEntity<byte[]>( [] as byte[], HttpStatus.BAD_REQUEST ) }
    }
}
