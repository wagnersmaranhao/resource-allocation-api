package com.rm.j2crm.domain.util;

public class ConstantsUtil {
    //Constantes
    public static String DATE_FORMAT = "yyyy-MM-dd";
    public static String DATE_REGEX = "^[0-9]{4}-(1[0-2]|0[1-9])-(3[01]|[12][0-9]|0[1-9])$";

    //Messagens Error
    public static String ERROR_INVALID = "Invalid '%s' parameters.";
    public static String ERROR_NOT_FOUND = "Record with id '%s' not found.";
    public static String ERROR_START_DATE = "The 'startDate' parameter cannot be less than the current date.";
    public static String ERROR_END_DATE = "The 'endDate' parameter cannot be less than or equal to the current date.";
    public static String ERROR_PARSE_DATE = "Unable to parse data field.";
    public static String ERROR_END_DATE_GREATER_START_DATE = "The end date must be greater than the start date.";
    public static String ERROR_PROPERTY_TYPE = "Property '%s' was assigned value '%s' which is of an invalid type. Please correct and provide a value compatible with type '%s'.";
    public static String ERROR_PROPERTY_NOT_FOUND = "The property '%s' does not exist. Please correct or remove this property and try again.";
    public static String ERROR_BODY_INVALID = "The request body is invalid. Check for syntax error.";
    public static String ERROR_UNABLE_DOWNLOAD = "Unable to download file, contact your system administrator.";
    public static String ERROR_UNABLE_UPLOAD = "Unable to save file, contact your system administrator.";

    //Field Name
    public static String NAME = "name";
    public static String DESCRIPTION = "description";
    public static String START_DATE = "startDate";
    public static String END_DATE = "endDate";

    public static String SORT_DIRECTION_ASC = "asc";
    public static String FIRST_NAME = "firstName";
    public static String LAST_NAME = "LastName";
    public static String BIRTH_DATE = "BirthDate";
    public static String ROLE = "role";
    public static String AVAILABILITY = "availability";
    public static String CV_URI = "cvUri";
    public static String CV_LAST_UPDATE = "cvLastUpdated";

    public static String TITLE = "title";
    public static String NUMBER_OF_RESOURCES = "numberOfResources";
    public static String STATUS = "status";
}
