package com.sciforce.robin

import spock.lang.Specification

/**
 * Base class for testing.
 */
abstract class BaseTest extends Specification {

    boolean outputPdf( byte[] byteArray ) {
        File file = File.createTempFile( UUID.randomUUID().toString(), '.pdf' )
        new FileOutputStream( file ).withStream { it.write( byteArray ) }
        println "Just created PDF: $file.path"
        true
    }

    protected static final String XMLTemplate = '''<?xml version="1.0" encoding="UTF-8" ?> \
<note> \
<to>Tove</to> \
<from>Jani</from> \
<heading>Reminder</heading> \
<body>Don't forget me this weekend!</body> \
</note> \
'''
}
