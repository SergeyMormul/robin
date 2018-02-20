package com.sciforce.robin

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.util.UriComponentsBuilder

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

        given: 'a valid url'
        def url = buildUri()

        when: 'the service is called'
        def result = template.exchange( new RequestEntity<Object>(HttpMethod.POST, url ), byte[] )

        then: 'the result is returned'
        result

        and: 'pdf is generated'
        // need to inspect by hand
        outputPdf( result.body )
    }

    URI buildUri() {
        UriComponentsBuilder.newInstance()
                .scheme('http')
                .host('localhost')
                .port(port)
                .path('/export')
                .queryParam('w', '1')
                .queryParam('h', '1')
                .queryParam('bg', '000000')
                .queryParam('filename', 'fuckoff')
                .queryParam('format', 'pdf')
                .queryParam('xml', XMLTemplate)
                .build().toUri()
    }
}
