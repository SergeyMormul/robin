package com.sciforce.robin

import org.springframework.http.HttpStatus
import org.springframework.validation.Errors

/**
 * Unit-level test of {@link ExportPDFService}.
 */
class ExportPDFServiceUnitTest extends BaseTest {

    def 'verify pdf generation based on xml'() {

        given: 'a valid subject under test'
        def errors = Mock( Errors )
        def sut = new ExportPDFService()

        and: 'a valid hypermedia control'
        def control = new HypermediaControl( w: 1, h: 1, filename: 'file', xml: XMLTemplate )

        when: 'the service is called'
        def result = sut.generatePDF( control, errors )

        then: 'result generated'
        result
        result.statusCode == HttpStatus.ACCEPTED

        and: 'pdf is generated'
        // need to inspect by hand
        outputPdf( result.body )
    }
}
