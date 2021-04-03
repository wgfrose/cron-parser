package com.wgfrose.cron.parser;

import org.junit.Assert;
import org.junit.Test;

import static com.wgfrose.cron.parser.CronParser.INVALID_CRON_STRING;

public class CronParserTests {

    private final CronParser cronParser = new CronParser();

    @Test
    public void testValidCronPatterns() {
        // Checking that the cron patterns return a valid response
        Assert.assertNotEquals(INVALID_CRON_STRING, cronParser.parse("23 0-20/2 1,15 * 1-5 /usr/bin/find"));
        Assert.assertNotEquals(INVALID_CRON_STRING, cronParser.parse("5 4 * * SUN /usr/bin/find"));
        Assert.assertNotEquals(INVALID_CRON_STRING, cronParser.parse("10,44 14 * 3 SUN-SAT /usr/bin/find"));
        Assert.assertNotEquals(INVALID_CRON_STRING, cronParser.parse("10,44 14 * 3 SUN-FRI/2 /usr/bin/find"));
        Assert.assertNotEquals(INVALID_CRON_STRING, cronParser.parse("10,44 14 * 3 */2 /usr/bin/find"));
        Assert.assertNotEquals(INVALID_CRON_STRING, cronParser.parse("15 10 * * MON-FRI /usr/bin/find"));
        Assert.assertNotEquals(INVALID_CRON_STRING, cronParser.parse("*/15 0 1,15 * 1-5 /usr/bin/find"));

        // Checking that the valid cron patterns are formatted correctly
        Assert.assertEquals("""
                minute        0 15 30 45
                hour          0
                day of month  1 15
                month         1 2 3 4 5 6 7 8 9 10 11 12
                day of week   1 2 3 4 5
                command       /usr/bin/find""", cronParser.parse("*/15 0 1,15 * 1-5 /usr/bin/find"));

        Assert.assertEquals("""
                minute        23
                hour          0 2 4 6 8 10 12 14 16 18 20
                day of month  1 15
                month         1 2 3 4 5 6 7 8 9 10 11 12
                day of week   1 2 3 4 5
                command       /usr/bin/find""", cronParser.parse("23 0-20/2 1,15 * 1-5 /usr/bin/find"));

        Assert.assertEquals("""
                minute        5
                hour          4
                day of month  1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31
                month         1 2 3 4 5 6 7 8 9 10 11 12
                day of week   0
                command       /usr/bin/find""", cronParser.parse("5 4 * * SUN /usr/bin/find"));

        Assert.assertEquals("""
                minute        10 44
                hour          14
                day of month  1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31
                month         3
                day of week   0 1 2 3 4 5 6
                command       /usr/bin/find""", cronParser.parse("10,44 14 * 3 SUN-SAT /usr/bin/find"));

        Assert.assertEquals("""
                minute        10 44
                hour          14
                day of month  1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31
                month         3
                day of week   0 2 4
                command       /usr/bin/find""", cronParser.parse("10,44 14 * 3 SUN-FRI/2 /usr/bin/find"));

        Assert.assertEquals("""
                minute        15
                hour          10
                day of month  1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31
                month         1 2 3 4 5 6 7 8 9 10 11 12
                day of week   1 2 3 4 5
                command       /usr/bin/find""", cronParser.parse("15 10 * * MON-FRI /usr/bin/find"));

        Assert.assertEquals("""
                minute        10 12 14 16 18 20 22 24 26 28 30 32 34 36 38 40 42 44
                hour          14
                day of month  4 5 6 7 8 9 10 11 12 13 14 15 16 17
                month         11 12 1 2 3 4
                day of week   5 6 0 1 2 3 4
                command       /usr/bin/find""", cronParser.parse("10-44/2 14 4-17 NOV-APR FRI-THU /usr/bin/find"));
    }

    @Test
    public void testInvalidCronPatterns() {
        Assert.assertEquals(INVALID_CRON_STRING, cronParser.parse("*/15 0 1,15 * 1-5 7 /usr/bin/find"));
        Assert.assertEquals(INVALID_CRON_STRING, cronParser.parse("100/15 0 1,15 * 1-5 /usr/bin/find"));
        Assert.assertEquals(INVALID_CRON_STRING, cronParser.parse("*/15 0 1,32 * 1-5 /usr/bin/find"));
        Assert.assertEquals(INVALID_CRON_STRING, cronParser.parse("*/15 0 1,15 YTR-FEB 1-5 /usr/bin/find"));
        Assert.assertEquals(INVALID_CRON_STRING, cronParser.parse("*/15 0 1*15 * 1-5 /usr/bin/find"));
        Assert.assertEquals(INVALID_CRON_STRING, cronParser.parse("*/15 0 1,15 ? 1-5 /usr/bin/find"));
    }

}
