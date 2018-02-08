package com.sciforce.robin

import java.awt.image.BufferedImage

/**
 * Created by vagrant on 2/8/18.
 */
class Constants {

    /**
     * Contains an empty image.
     */
    public static BufferedImage EMPTY_IMAGE

    /**
     * Initializes the empty image.
     */
    static {
        try {
            EMPTY_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
        }
        catch (final Exception e) {
            // ignore
        }
    }

    /**
     * Maximum size (in bytes) for request payloads. Default is 10485760 (10MB).
     */
    public static final int MAX_REQUEST_SIZE = 10485760

    /**
     * Maximum area for exports. Default is 10000x10000px.
     */
    public static final int MAX_AREA = 10000 * 10000
}
