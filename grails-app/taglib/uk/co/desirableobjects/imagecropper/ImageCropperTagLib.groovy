package uk.co.desirableobjects.imagecropper

import uk.co.desirableobjects.imagecropper.exception.MissingRequiredAttributeException
import java.util.Map.Entry
import uk.co.desirableobjects.imagecropper.exception.InvalidAttributeException

class ImageCropperTagLib {

    static def namespace = "cropper"

    String currentImageId = null

    static final Map<String, List<String>> REQUIRED_ATTRIBUTES = [
        imageId: []
    ]

    static final Map<String, List<String>> CONFIGURATION_ATTRIBUTES = [
        minWidth: [],
        maxWidth: [],
        minHeight: [],
        maxHeight: [],
        displayOnInit: ['true', 'false'],
        ratioDim: [],
        captureKeys: [],
        onLoadCoords: []
    ]

    def head = { attrs, body ->
        out << g.javascript([library:'cropper', plugin:'image-cropper'], "")
        out << pluginContextPath
        String cssPath = attrs.css ?: resource(dir:"${pluginContextPath}/css", file:'cropper.css')
        out << '<style type="text/css" media="screen">'
        out << "   @import url( ${cssPath} );"
        out << "</style>"
    }

    def crop = { attrs, body ->

        if (!attrs.imageId) {
            throw new MissingRequiredAttributeException("imageId")
        }

        currentImageId = attrs.imageId

        out << g.javascript([:], """
            Event.observe(document, 'dom:loaded', function() {
                new Cropper.Img('${currentImageId}',
                    { ${doParameters(attrs)}onEndCrop: function(coords, dimensions) { ${body()} } }
                );
            } );
        """)

        currentImageId = null
    }

    private String doParameters(attrs) {

        List<String> parameters = ['autoIncludeCSS: false']
        attrs.each { Entry attribute ->
          if (CONFIGURATION_ATTRIBUTES.keySet().contains(attribute.key)) {
           validateAttribute(attribute)
           parameters << "${attribute.key}: ${attribute.value}"
          } else {
            validateRequiredAttribute(attribute)
          }
        }

        String configuration = parameters.join(",")
        return configuration ? "${configuration}, " : ''

    }

  private def validateRequiredAttribute(Entry attribute) {
    if (!REQUIRED_ATTRIBUTES.keySet().contains(attribute.key)) {
      throw new InvalidAttributeException(attribute.key as String)
    }
  }

  private def validateAttribute(Entry attribute) {
    List allowedValues = CONFIGURATION_ATTRIBUTES[attribute.key]
    if (!allowedValues.isEmpty()) {
      if (!allowedValues.contains(attribute.value)) {
        throw new InvalidAttributeException(attribute.key, allowedValues)
      }
    }
  }

  def onEndCrop = { attrs, body ->

        if (currentImageId) {
            body()
        } else {
            throw new IllegalStateException(":onEndCrop tags can only be used inside an enclosing :cropper tag.")
        }

    }

}
