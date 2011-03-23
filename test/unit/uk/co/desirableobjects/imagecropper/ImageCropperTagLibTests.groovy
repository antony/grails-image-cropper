package uk.co.desirableobjects.imagecropper

import grails.test.*
import uk.co.desirableobjects.imagecropper.exception.MissingRequiredAttributeException
import uk.co.desirableobjects.imagecropper.exception.InvalidAttributeException

class ImageCropperTagLibTests extends TagLibUnitTestCase {

    static final Map EXAMPLE_PARAMETERS = [myKey: 'myValue', myOtherKey: 5]
    static final String DUMMY_PLUGIN_CONTEXT_PATH = 'plugins/image-cropper-1.0'
    static final String DUMMY_CALLBACK = "alert(filename+' yadda yadda')"
    static final Closure BLANK_TAG_BODY = { return "" }
    static final String CROPPABLE_IMAGE_ID = 'myImageId'

    protected void setUp() {
        super.setUp()
        
        ImageCropperTagLib.metaClass.javascript = { attrs, body ->
            if (attrs.library) {
                return """<script type="text/javascript" src="${attrs.plugin}/${attrs.library}.js"></script>"""
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

        assertContains """<script type="text/javascript" src="image-cropper/cropper.js"></script>"""

    }

    void testHeadTagIncludesCss() {

        tagLib.head([:], BLANK_TAG_BODY)

        assertContains '<style type="text/css" media="screen">'
        assertContains "@import url( /${DUMMY_PLUGIN_CONTEXT_PATH}/css/cropper.css );"
        assertContains '</style>'

    }

    void testExcludeCss() {

        tagLib.head([css:'/myapp/mycss.css'], BLANK_TAG_BODY)

        assertDoesNotContain '@import url( /css/cropper.css )'
        assertContains '@import url( /myapp/mycss.css )'

    }

    void testAttachBasicCropper() {

        tagLib.crop([imageId:CROPPABLE_IMAGE_ID], BLANK_TAG_BODY)

        assertContains "Event.observe(window, 'load', function() {"
        assertContains "new Cropper.Img('${CROPPABLE_IMAGE_ID}',"
        assertContains "{ autoIncludeCSS: false, onEndCrop: function(coords, dimensions) {  } }"

    }

    void testBasicCropperWithCustomCallback() {

        String onEndCropFunction = "alert('Ended crop, selected area was '+coords+' new image size is '+dimensions)"

        tagLib.crop([imageId:CROPPABLE_IMAGE_ID], {
            return tagLib.onEndCrop([:], { onEndCropFunction })
        })

        assertContains("{ autoIncludeCSS: false, onEndCrop: function(coords, dimensions) { alert('Ended crop, selected area was '+coords+' new image size is '+dimensions) } }")

    }

    void testAttachCropperWithoutProvidingImageId() {

        shouldFail(MissingRequiredAttributeException.class) {
            tagLib.crop([:], BLANK_TAG_BODY)
        }

    }

    void testOnEndCropTagCanOnlyBeCalledWithinCropperTag() {

        shouldFail(IllegalStateException.class) {
            tagLib.onEndCrop([:], BLANK_TAG_BODY)
        }

    }

    void testEnsureStateIsResetAfterTag() {

        testBasicCropperWithCustomCallback()
        testOnEndCropTagCanOnlyBeCalledWithinCropperTag()

    }

    void testNoAutoIncludeCSS() {

        tagLib.crop([imageId:CROPPABLE_IMAGE_ID], BLANK_TAG_BODY)
        assertContains('autoIncludeCSS: false')

    }

    void testAllowedAttribute() {
        tagLib.crop([imageId:CROPPABLE_IMAGE_ID, minHeight:'640'], BLANK_TAG_BODY)
        assertContains('minHeight: 640')
    }

    void testInvalidAttributeValue() {
        shouldFail(InvalidAttributeException.class) {
            tagLib.crop([imageId:CROPPABLE_IMAGE_ID, captureKeys:'biahh'], BLANK_TAG_BODY)
        }
    }

    void testUnallowedAttribute() {
        shouldFail(InvalidAttributeException.class) {
          tagLib.crop([imageId:CROPPABLE_IMAGE_ID, carnegie:'mellon'], BLANK_TAG_BODY)
        }
    }

    void testDisplayOnInitExcludedWhenNotUsed() {
        shouldFail(InvalidAttributeException.class) {
            tagLib.crop([imageId:CROPPABLE_IMAGE_ID, displayOnInit:'true'], BLANK_TAG_BODY)
        }
    }

    void testDisplayOnInitAllowedWhenDependenciesMet() {
        tagLib.crop([imageId:CROPPABLE_IMAGE_ID, displayOnInit:'true', minWidth:'32', minHeight:'32'], BLANK_TAG_BODY)
    }

    private assertContains(String expected) {
        assert tagLib.out.toString().contains(expected)
    }

    private assertDoesNotContain(String unexpected) {
        assert !tagLib.out.toString().contains(unexpected)
    }
}
