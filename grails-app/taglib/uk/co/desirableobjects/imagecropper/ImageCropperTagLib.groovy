package uk.co.desirableobjects.imagecropper

import uk.co.desirableobjects.imagecropper.exception.MissingRequiredAttributeException

class ImageCropperTagLib {

    def namespace = "cropper"

    String currentImageId = null

    def head = { attrs, body ->
        out << g.javascript([library:'cropper', plugin:'image-cropper'], "")
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
                    { onEndCrop: function(coords, dimensions) { ${body()} } }
                );
            } );
        """)

        currentImageId = null
    }

    def onEndCrop = { attrs, body ->

        if (currentImageId) {
            body()
        } else {
            throw new IllegalStateException(":onEndCrop tags can only be used inside an enclosing :cropper tag.")
        }

    }

}
