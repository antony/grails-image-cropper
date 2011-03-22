package uk.co.desirableobjects.imagecropper.exception

class MissingRequiredAttributeException extends RuntimeException {

    MissingRequiredAttributeException(String attribute) {
        super(String.format("Required attribute %s is missing.", attribute))
    }

}
