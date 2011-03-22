package uk.co.desirableobjects.imagecropper.exception

class InvalidAttributeException extends RuntimeException {

    InvalidAttributeException(String attribute) {
        super(String.format("attribute %s is not known", attribute))
    }

    InvalidAttributeException(String attribute, List allowedValues) {
        super(String.format("attribute %s has an invalid value, try one of %s", attribute, allowedValues.toListString()))
    }

}
