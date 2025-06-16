package com.rm.j2crm.domain.util;

import com.rm.j2crm.domain.exception.InputDataException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;
import org.apache.logging.log4j.util.Strings;

public class FunctionsUtil {

  private static final DateFormat dateFormat = new SimpleDateFormat(ConstantsUtil.DATE_FORMAT);

  public static void isValid(String value, String field) {
    if (Strings.isEmpty(value)) {
      throw new InputDataException(ConstantsUtil.ERROR_INVALID.formatted(field));
    }
  }

  public static void isValidDate(String date, String field) {
    isValid(date, field);

    if (!isMatch(date, ConstantsUtil.DATE_REGEX)) {
      throw new InputDataException(ConstantsUtil.ERROR_INVALID.formatted(field));
    }
  }

  public static boolean isMatch(String target, String regex) {
    return Pattern.compile(regex).matcher(target).matches();
  }

  public static Date dateNow() {
    return formatDate(new Date());
  }

  public static Date formatDate(Date date) {
    try {
      return dateFormat.parse(dateFormat.format(date));
    }
    catch (ParseException e) {
      throw new InputDataException(ConstantsUtil.ERROR_PARSE_DATE);
    }
  }

  public static String dateToString(Date date) {
    return dateFormat.format(date);
  }

  public static Date stringToDate(String value) {
    try {
      return dateFormat.parse(value);
    }
    catch (ParseException e) {
      throw new InputDataException(ConstantsUtil.ERROR_PARSE_DATE);
    }
  }

  public static Boolean strToBool(String value) {
    return (Strings.isNotEmpty(value) && value.equals("true"));
  }

  public static String getDate(Date date, int days) {
    return dateToString(addDays(date, days));
  }

  public static Date addDays(Date date, int days) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DATE, days); //minus number would decrement the days
    return cal.getTime();
  }
}
