package com.sciforce.robin

import com.itextpdf.text.Document
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.PdfWriter
import java.awt.Image
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParserFactory
import org.xhtmlrenderer.pdf.ITextRenderer
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.XMLReader

/**
 * Created by vagrant on 2/8/18.
 */
@WebServlet(value = '/export')
class ExportService extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = -5040708166131034515L

    /**
     *
     */
    private transient SAXParserFactory parserFactory = SAXParserFactory.newInstance()

    /**
     * Cache for all images.
     */
    protected transient Hashtable<String, Image> imageCache = new Hashtable<String, Image>()

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        println 'You did it!'
        resp.setStatus(HttpServletResponse.SC_ACCEPTED)
    }

    /**
     * Handles exceptions and the output stream buffer.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            if (request.getContentLength() < Constants.MAX_REQUEST_SIZE) {
                long t0 = System.currentTimeMillis()

                handleRequest(request, response)

                long mem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
                long dt = System.currentTimeMillis() - t0

                request.with { println "export: ip= ${remoteAddr} ref='${getHeader('Referer')}' length=${contentLength} mem=${mem} dt=${dt}" }
            } else {
                response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE)
            }
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace()
            final Runtime r = Runtime.getRuntime()
            System.out.println("r.freeMemory() = " + r.freeMemory() / 1024.0 / 1024)
            System.out.println("r.totalMemory() = " + r.totalMemory() / 1024.0 / 1024)
            System.out.println("r.maxMemory() = " + r.maxMemory() / 1024.0 / 1024)
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
        }
        catch (Exception e) {
            e.printStackTrace()
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
        }
        finally {
            response.getOutputStream().flush()
            response.getOutputStream().close()
        }
    }

    /**
     * Gets the parameters and logs the request.
     *
     * @throws ParserConfigurationException
     * @throws SAXException
     //     * @throws DocumentException
     */
    protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Parses parameters
        String format = request.getParameter('format')
        String fileName = request.getParameter('filename')
        int w = Integer.parseInt(request.getParameter('w'))
        int h = Integer.parseInt(request.getParameter('h'))
        String xml = getRequestXml(request)

        if ( w > 0 && h > 0 && w * h < Constants.MAX_AREA && format != null && xml != null && xml.length() > 0 ) {
            def renderer = new ITextRenderer()
            renderer.setDocumentFromString(xml)
            renderer.layout()
            renderer.createPDF(response.outputStream)

            // Checks parameters
            if (fileName != null && fileName.toLowerCase().endsWith('.xml')) {
                fileName = fileName.substring(0, fileName.length() - 4) + format
            }

            response.setContentType('application/pdf')

            if (fileName != null) {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + fileName)
            }
            response.setStatus(HttpServletResponse.SC_OK)
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
        }
    }

    /**
     * Gets the XML request parameter.
     */
    protected static String getRequestXml(HttpServletRequest request) throws IOException, UnsupportedEncodingException {
        def xml = request.getParameter('xml')

        // Decoding is optional (no plain text values allowed)
        if (xml != null && xml.startsWith('%3C')) {
            xml = URLDecoder.decode(xml, 'UTF-8')
        }

        xml
    }

    protected void writePdf(String url, String fname, int w, int h, String xml, HttpServletResponse response)
    /*throws DocumentException, IOException, SAXException, ParserConfigurationException*/ {

        /*response.setContentType('application/pdf')

        if (fname != null) {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fname + "\"; filename*=UTF-8''" + fname)
        }

        // Fixes PDF offset
        w += 1
        h += 1

        Document document = new Document(new com.mxpdf.text.Rectangle(w, h))
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream())
        document.open();

        mxGraphicsCanvas2D gc = createCanvas(url, writer.getDirectContent().createGraphics(w, h))

        // Fixes PDF offset
        gc.translate(1, 1)

        renderXml(xml, gc)
        gc.getGraphics().dispose()
        document.close()
        writer.flush()
        writer.close()*/
    }

    /**
     * Renders the XML to the given canvas.
     */
    protected void renderXml(String xml) throws SAXException, ParserConfigurationException, IOException {
        XMLReader reader = parserFactory.newSAXParser().getXMLReader()
        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        reader.setFeature("http://xml.org/sax/features/external-general-entities", false)
        reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
        reader.parse(new InputSource(new StringReader(xml)))
    }
}
