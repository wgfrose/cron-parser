package com.wgfrose.cron.parser;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CronParser {

    // Allows for at least one space between fields
    private static final String CRON_DELIMITER_REGEX = "\\s+";
    private static final String FIELD_DELIMITER = " ";

    private Map<String, String> regexMap;

    private final int minuteIndex = 0;
    private final int hourIndex = 1;
    private final int dayOfMonthIndex = 2;
    private final int monthIndex = 3;
    private final int dayOfWeekIndex = 4;
    private final int commandIndex = 5;

    private final String minuteLabel = "minute";
    private final String hourLabel = "hour";
    private final String dayOfMonthLabel = "day of month";
    private final String monthLabel = "month";
    private final String dayOfWeekLabel = "day of week";
    private final String commandLabel = "command";

    private String expandedMinute;
    private String expandedHour;
    private String expandedDayOfMonth;
    private String expandedMonth;
    private String expandedDayOfWeek;

    public CronParser() {
        this.regexMap = this.buildCronRegex();
    }

    public static void main(String[] args) {
        CronParser parser = new CronParser();
        System.out.println(parser.parse(args[0]));
    }

    public String parse(final String argument) {
        String[] cronFields = argument.split(CRON_DELIMITER_REGEX);
        if (validateAllFields(cronFields)) {
            return printExpanded(cronFields);
        } else {
            return "Invalid cron pattern";
        }
    }

    private boolean validateAllFields(String[] fields) {
        return isNumOfFieldsValid(fields) &&
                isMinuteFieldValid(fields[this.minuteIndex]) &&
                isHourFieldValid(fields[this.hourIndex]) &&
                isDayOfMonthFieldValid(fields[this.dayOfMonthIndex]) &&
                isMonthFieldValid(fields[this.monthIndex]) &&
                isDayOfWeekFieldValid(fields[this.dayOfWeekIndex]) &&
                isCommandFieldValid(fields[this.commandIndex]);
    }

    private boolean isNumOfFieldsValid(String[] fields) {
        return (fields.length == 6);
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
        this.expandedMinute = expandField(fields[minuteIndex], 0, 59);
        this.expandedHour = expandField(fields[hourIndex], 0, 23);
        this.expandedDayOfMonth = expandField(fields[dayOfMonthIndex], 1, 31);
        this.expandedMonth = expandField(fields[monthIndex], 1, 12);
        this.expandedDayOfWeek = expandField(fields[dayOfWeekIndex], 0, 6);

        stringbuilder.append(padRightSpaces(minuteLabel, 14)).append(expandedMinute).append("\n");
        stringbuilder.append(padRightSpaces(hourLabel, 14)).append(expandedHour).append("\n");
        stringbuilder.append(padRightSpaces(dayOfMonthLabel, 14)).append(expandedDayOfMonth).append("\n");
        stringbuilder.append(padRightSpaces(monthLabel, 14)).append(expandedMonth).append("\n");
        stringbuilder.append(padRightSpaces(dayOfWeekLabel, 14)).append(expandedDayOfWeek).append("\n");
        stringbuilder.append(padRightSpaces(commandLabel, 14)).append(fields[commandIndex]).append("\n");

        return stringbuilder.toString();
    }

    private String expandField(String field, int min, int max) {
        String expandedField = "";
        field = swapNamesForNumbers(field);
        try {
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
                if (start > end) end = (max * 2);
                for (int i = start; i <= end; i++) {
                    if (i > max) {
                        expandedField += (i - max - 1) + FIELD_DELIMITER;
                    } else {
                        expandedField += i + FIELD_DELIMITER;
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
                if (start > end) end = (max * 2);
                for (int i = start; i <= end; i++) {
                    if ((i > max) && ((i - max) % step == 0)) {
                        expandedField += (i - max - 1) + FIELD_DELIMITER;
                    } else {
                        if (i % step == 0) {
                            expandedField += i + FIELD_DELIMITER;
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
        if (field.contains("mon") || field.contains("MON")) {
            field = field.replaceAll("(?i)mon", "1");
        }
        if (field.contains("tue") || field.contains("TUE")) {
            field = field.replaceAll("(?i)tue", "2");
        }
        if (field.contains("wed") || field.contains("WED")) {
            field = field.replaceAll("(?i)wed", "3");
        }
        if (field.contains("thu") || field.contains("THU")) {
            field = field.replaceAll("(?i)thu", "4");
        }
        if (field.contains("fri") || field.contains("FRI")) {
            field = field.replaceAll("(?i)fri", "5");
        }
        if (field.contains("sat") || field.contains("SAT")) {
            field = field.replaceAll("(?i)sat", "6");
        }
        if (field.contains("sun") || field.contains("SUN")) {
            field = field.replaceAll("(?i)sun", "0");
        }
        if (field.contains("jan") || field.contains("JAN")) {
            field = field.replaceAll("(?i)jan", String.valueOf(Month.JANUARY.getValue()));
        }
        if (field.contains("feb") || field.contains("FEB")) {
            field = field.replaceAll("(?i)feb", String.valueOf(Month.FEBRUARY.getValue()));
        }
        if (field.contains("mar") || field.contains("MAR")) {
            field = field.replaceAll("(?i)mar", String.valueOf(Month.MARCH.getValue()));
        }
        if (field.contains("apr") || field.contains("APR")) {
            field = field.replaceAll("(?i)apr", String.valueOf(Month.APRIL.getValue()));
        }
        if (field.contains("may") || field.contains("MAY")) {
            field = field.replaceAll("(?i)may", String.valueOf(Month.MAY.getValue()));
        }
        if (field.contains("jun") || field.contains("JUN")) {
            field = field.replaceAll("(?i)jun", String.valueOf(Month.JUNE.getValue()));
        }
        if (field.contains("jul") || field.contains("JUL")) {
            field = field.replaceAll("(?i)jul", String.valueOf(Month.JULY.getValue()));
        }
        if (field.contains("aug") || field.contains("AUG")) {
            field = field.replaceAll("(?i)aug", String.valueOf(Month.AUGUST.getValue()));
        }
        if (field.contains("sep") || field.contains("SEP")) {
            field = field.replaceAll("(?i)sep", String.valueOf(Month.SEPTEMBER.getValue()));
        }
        if (field.contains("oct") || field.contains("OCT")) {
            field = field.replaceAll("(?i)oct", String.valueOf(Month.OCTOBER.getValue()));
        }
        if (field.contains("nov") || field.contains("NOV")) {
            field = field.replaceAll("(?i)nov", String.valueOf(Month.NOVEMBER.getValue()));
        }
        if (field.contains("dec") || field.contains("DEC")) {
            field = field.replaceAll("(?i)dec", String.valueOf(Month.DECEMBER.getValue()));
        }
        return field;
    }

}
