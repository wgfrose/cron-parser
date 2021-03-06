package com.wgfrose.cron.parser;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CronParser {

    // Allows for at least one space between fields
    private static final String CRON_DELIMITER_REGEX = "\\s+";
    private static final String FIELD_DELIMITER = " ";
    public static final String INVALID_CRON_STRING = "Invalid cron pattern";

    private final Map<String, String> regexMap;

    private static final int MINUTE_INDEX = 0;
    private static final int HOUR_INDEX = 1;
    private static final int DAY_OF_MONTH_INDEX = 2;
    private static final int MONTH_INDEX = 3;
    private static final int DAY_OF_WEEK_INDEX = 4;
    private static final int COMMAND_INDEX = 5;

    public CronParser() {
        this.regexMap = this.buildCronRegex();
    }

    public static void main(String[] args) {
        CronParser parser = new CronParser();
        try {
            System.out.println(parser.parse(args[0]));
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("No cron pattern argument supplied");
        }
    }

    /**
     * Method to parse the cron pattern provided by the user into a readable expanded format.
     *
     * @param argument - The argument passed in by the user, e.g. "23 0-20/2 1,15 * 1-5 /usr/bin/find".
     * @return - The formatted expansion of a cron pattern.
     */
    public String parse(final String argument) {
        String[] cronFields = argument.split(CRON_DELIMITER_REGEX);
        if (validateAllFields(cronFields)) {
            return printExpanded(cronFields);
        } else {
            return INVALID_CRON_STRING;
        }
    }

    private boolean validateAllFields(String[] fields) {
        return isNumOfFieldsValid(fields) &&
                isMinuteFieldValid(fields[MINUTE_INDEX]) &&
                isHourFieldValid(fields[HOUR_INDEX]) &&
                isDayOfMonthFieldValid(fields[DAY_OF_MONTH_INDEX]) &&
                isMonthFieldValid(fields[MONTH_INDEX]) &&
                isDayOfWeekFieldValid(fields[DAY_OF_WEEK_INDEX]) &&
                isCommandFieldValid(fields[COMMAND_INDEX]);
    }

    private boolean isNumOfFieldsValid(String[] fields) {
        return fields.length == 6;
    }

    private boolean isMinuteFieldValid(String minute) {
        return Pattern.matches(regexMap.get("minute"), minute);
    }

    private boolean isHourFieldValid(String hour) {
        return Pattern.matches(regexMap.get("hour"), hour);
    }

    private boolean isDayOfMonthFieldValid(String dayOfMonth) {
        return Pattern.matches(regexMap.get("dayOfMonth"), dayOfMonth);
    }

    private boolean isMonthFieldValid(String month) {
        return Pattern.matches(regexMap.get("month"), month);
    }

    private boolean isDayOfWeekFieldValid(String dayOfWeek) {
        return Pattern.matches(regexMap.get("dayOfWeek"), dayOfWeek);
    }

    private boolean isCommandFieldValid(String command) {
        try {
            Paths.get(command);
        } catch (InvalidPathException | NullPointerException e) {
            return false;
        }
        return true;
    }

    private String printExpanded(String[] fields) {
        StringBuilder stringbuilder = new StringBuilder();
        String expandedMinute = expandField(fields[MINUTE_INDEX], 0, 59, true);
        String expandedHour = expandField(fields[HOUR_INDEX], 0, 23, true);
        String expandedDayOfMonth = expandField(fields[DAY_OF_MONTH_INDEX], 1, 31, false);
        String expandedMonth = expandField(fields[MONTH_INDEX], 1, 12, false);
        String expandedDayOfWeek = expandField(fields[DAY_OF_WEEK_INDEX], 0, 6, true);

        stringbuilder.append(padRightSpaces("minute", 14)).append(expandedMinute.trim()).append("\n");
        stringbuilder.append(padRightSpaces("hour", 14)).append(expandedHour.trim()).append("\n");
        stringbuilder.append(padRightSpaces("day of month", 14)).append(expandedDayOfMonth.trim()).append("\n");
        stringbuilder.append(padRightSpaces("month", 14)).append(expandedMonth.trim()).append("\n");
        stringbuilder.append(padRightSpaces("day of week", 14)).append(expandedDayOfWeek.trim()).append("\n");
        stringbuilder.append(padRightSpaces("command", 14)).append(fields[COMMAND_INDEX].trim());

        return stringbuilder.toString();
    }

    // Expand a time field by providing the min and max possible for that time unit an if the field is zero indexed
    private String expandField(String field, int min, int max, boolean zeroIndexed) {
        String expandedField = "";
        field = swapNamesForNumbers(field);
        try {
            // If we only see an integer, then set that to be the expanded field
            Integer.parseInt(field);
            expandedField = field;
        } catch (NumberFormatException e) {
            if (field.contains(",")) {
                String[] splits = field.split(",");
                for (String split : splits) {
                    expandedField += split + FIELD_DELIMITER;
                }
            } else if (field.contains("-") && !field.contains("/")) {
                int start = Integer.parseInt(field.split("-")[0]);
                int end = Integer.parseInt(field.split("-")[1]);
                if (zeroIndexed) {
                    if (start > end) end = (max + end) + 1;
                    for (int i = start; i <= end; i++) {
                        if (i > max) {
                            expandedField += (i - max - 1) + FIELD_DELIMITER;
                        } else {
                            expandedField += i + FIELD_DELIMITER;
                        }
                    }
                } else {
                    if (start > end) end = (max + end);
                    for (int i = start; i <= end; i++) {
                        if (i > max) {
                            expandedField += (i - max) + FIELD_DELIMITER;
                        } else {
                            expandedField += i + FIELD_DELIMITER;
                        }
                    }
                }
            } else if (!field.contains("-") & field.contains("/")) {
                int step = Integer.parseInt(field.split("/")[1]);
                for (int i = min; i <= max; i++) {
                    if (i % step == 0) {
                        expandedField += i + FIELD_DELIMITER;
                    }
                }
            } else if (field.contains("-") & field.contains("/")) {
                int step = Integer.parseInt(field.split("/")[1]);
                int start = Integer.parseInt(field.split("/")[0].split("-")[0]);
                int end = Integer.parseInt(field.split("/")[0].split("-")[1]);
                if (zeroIndexed) {
                    if (start > end) end = (max + end) + 1;
                    for (int i = start; i <= end; i++) {
                        if ((i > max) && ((i - max) % step == 0)) {
                            expandedField += (i - max - 1) + FIELD_DELIMITER;
                        } else {
                            if (i % step == 0) {
                                expandedField += i + FIELD_DELIMITER;
                            }
                        }
                    }
                } else {
                    if (start > end) end = (max + end);
                    for (int i = start; i <= end; i++) {
                        if ((i > max) && ((i - max) % step == 0)) {
                            expandedField += (i - max) + FIELD_DELIMITER;
                        } else {
                            if (i % step == 0) {
                                expandedField += i + FIELD_DELIMITER;
                            }
                        }
                    }
                }
            } else if (field.contains("*")) {
                for (int i = min; i <= max; i++) {
                    expandedField += i + FIELD_DELIMITER;
                }
            }
        }
        return expandedField;
    }

    // Pad a string with n number of spaces on the right
    private String padRightSpaces(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(inputString);
        while (sb.length() < length) {
            sb.append(' ');
        }
        return sb.toString();
    }

    // Generates regex patterns to validate each time field
    // Not allowing for '?', 'L', 'W' or '#' special characters
    private Map<String, String> buildCronRegex() {
        // Singular allowed numbers regex
        Map<String, String> numbersRegex = new HashMap<>();
        numbersRegex.put("minute", "[0-5]?\\d");
        numbersRegex.put("hour", "[01]?\\d|2[0-3]");
        numbersRegex.put("dayOfMonth", "0?[1-9]|[12]\\d|3[01]");
        numbersRegex.put("month", "[1-9]|1[012]");
        numbersRegex.put("dayOfWeek", "[0-6]");

        // Add in strings for day/month names
        String monthRegex = numbersRegex.get("month");
        monthRegex = monthRegex + "|jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC";
        numbersRegex.put("month", monthRegex);

        String dayOfWeekRegex = numbersRegex.get("dayOfWeek");
        dayOfWeekRegex = dayOfWeekRegex + "|mon|tue|wed|thu|fri|sat|sun|MON|TUE|WED|THU|FRI|SAT|SUN";
        numbersRegex.put("dayOfWeek", dayOfWeekRegex);

        Map<String, String> fieldRegex = new HashMap<>();

        // Add allowed ranges of numbers and names for each field
        for (String field : numbersRegex.keySet()) {
            String number = numbersRegex.get(field);
            String range = "(?:" + number + ")(?:-(?:" + number + ")(?:\\/\\d+)?)?";
            fieldRegex.put(field, "\\*(?:\\/\\d+)?|" + range + "(?:," + range + ")*");
        }

        return fieldRegex;
    }

    private String swapNamesForNumbers(String field) {
        field = field.toUpperCase();
        if (field.contains("MON")) {
            field = field.replaceAll("MON", "1");
        }
        if (field.contains("TUE")) {
            field = field.replaceAll("TUE", "2");
        }
        if (field.contains("WED")) {
            field = field.replaceAll("WED", "3");
        }
        if (field.contains("THU")) {
            field = field.replaceAll("THU", "4");
        }
        if (field.contains("FRI")) {
            field = field.replaceAll("FRI", "5");
        }
        if (field.contains("SAT")) {
            field = field.replaceAll("SAT", "6");
        }
        if (field.contains("SUN")) {
            field = field.replaceAll("SUN", "0");
        }
        if (field.contains("JAN")) {
            field = field.replaceAll("JAN", String.valueOf(Month.JANUARY.getValue()));
        }
        if (field.contains("FEB")) {
            field = field.replaceAll("FEB", String.valueOf(Month.FEBRUARY.getValue()));
        }
        if (field.contains("MAR")) {
            field = field.replaceAll("MAR", String.valueOf(Month.MARCH.getValue()));
        }
        if (field.contains("APR")) {
            field = field.replaceAll("APR", String.valueOf(Month.APRIL.getValue()));
        }
        if (field.contains("MAY")) {
            field = field.replaceAll("MAY", String.valueOf(Month.MAY.getValue()));
        }
        if (field.contains("JUN")) {
            field = field.replaceAll("JUN", String.valueOf(Month.JUNE.getValue()));
        }
        if (field.contains("JUL")) {
            field = field.replaceAll("JUL", String.valueOf(Month.JULY.getValue()));
        }
        if (field.contains("AUG")) {
            field = field.replaceAll("AUG", String.valueOf(Month.AUGUST.getValue()));
        }
        if (field.contains("SEP")) {
            field = field.replaceAll("SEP", String.valueOf(Month.SEPTEMBER.getValue()));
        }
        if (field.contains("OCT")) {
            field = field.replaceAll("OCT", String.valueOf(Month.OCTOBER.getValue()));
        }
        if (field.contains("NOV")) {
            field = field.replaceAll("NOV", String.valueOf(Month.NOVEMBER.getValue()));
        }
        if (field.contains("DEC")) {
            field = field.replaceAll("DEC", String.valueOf(Month.DECEMBER.getValue()));
        }
        return field;
    }

}