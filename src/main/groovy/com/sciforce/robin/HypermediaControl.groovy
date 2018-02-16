package com.sciforce.robin

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.Canonical
import org.hibernate.validator.constraints.NotEmpty

/**
 * Represent input hypermedia control.
 */
@Canonical
class HypermediaControl {

    @JsonProperty( 'xml' )
    @NotEmpty
    String xml

    @JsonProperty( 'filename' )
    String filename

    @JsonProperty( 'w' )
    int w

    @JsonProperty( 'h' )
    int h
}
