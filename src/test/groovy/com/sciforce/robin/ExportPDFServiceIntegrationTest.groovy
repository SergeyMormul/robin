package com.sciforce.robin

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.ContextConfiguration

/**
 * Integration test for {@link ExportPDFService}.
 */
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
@ContextConfiguration( classes = Application, loader = SpringBootContextLoader )
class ExportPDFServiceIntegrationTest extends BaseTest {

    /**
     * The port where application is started.
     */
    @Value( '${local.server.port}' )
    protected int port

    @Autowired
    TestRestTemplate template

    def 'verify pdf generation'() {

        given: 'a valid hypermedia control'
        def control = new HypermediaControl( w: 1, h: 1, filename: 'file', xml: XMLTemplate )

        when: 'the service is called'
        def result = template.postForObject("http://localhost:${port}/export", control, byte[] )

        then: 'the result is returned'
        result

        and: 'pdf is generated'
        // need to inspect by hand
        outputPdf( result )
    }
}
