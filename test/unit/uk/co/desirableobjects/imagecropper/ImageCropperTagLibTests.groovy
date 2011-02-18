package uk.co.desirableobjects.imagecropper

import grails.test.*

class ImageCropperTagLibTests extends TagLibUnitTestCase {

    static final Map EXAMPLE_PARAMETERS = [myKey: 'myValue', myOtherKey: 5]
    static final String DUMMY_PLUGIN_CONTEXT_PATH = 'plugins/my-plugin-1.0'
    static final String DUMMY_CALLBACK = "alert(filename+' yadda yadda')"
    static final Closure BLANK_TAG_BODY = { return "" }

    protected void setUp() {
        super.setUp()
        //AjaxUploaderTagLib.metaClass.createLink = { attrs -> return "/file/upload" }

        ImageCropperTagLib.metaClass.javascript = { attrs, body ->
            if (attrs.library) {
                return """<script type="text/javascript" src="${attrs.plugin}/${attrs.library}"></script>"""
            }
            return """<script type="text/javascript">${body}</script>"""
        }
        ImageCropperTagLib.metaClass.resource = { attrs -> return "/${attrs.dir}/${attrs.file}" }
        ImageCropperTagLib.metaClass.pluginContextPath = DUMMY_PLUGIN_CONTEXT_PATH

    }

    protected void tearDown() {
        super.tearDown()
    }

    void testHeadTagIncludesJs() {

        tagLib.head([:], BLANK_TAG_BODY)

        assertContains """<script type="text/javascript" src="/plugins/my-plugin-1.0/my-plugin"></script>"""

    }

    private assertContains(String expected) {
        assertTrue tagLib.out.toString().contains(expected)
    }

    private assertDoesNotContain(String unexpected) {
        assertFalse tagLib.out.toString().contains(unexpected)
    }
}
