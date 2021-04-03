package com.wgfrose.cron.parser;

import org.junit.Assert;
import org.junit.Test;

import static com.wgfrose.cron.parser.CronParser.INVALID_CRON_STRING;

public class CronParserTests {

    private CronParser cronParser = new CronParser();

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
        Assert.assertEquals("minute        0 15 30 45\n" +
                                    "hour          0\n" +
                                    "day of month  1 15\n" +
                                    "month         1 2 3 4 5 6 7 8 9 10 11 12\n" +
                                    "day of week   1 2 3 4 5\n" +
                                    "command       /usr/bin/find\n", cronParser.parse("*/15 0 1,15 * 1-5 /usr/bin/find"));

        Assert.assertEquals("minute        23\n" +
                                    "hour          0 2 4 6 8 10 12 14 16 18 20\n" +
                                    "day of month  1 15\n" +
                                    "month         1 2 3 4 5 6 7 8 9 10 11 12\n" +
                                    "day of week   1 2 3 4 5\n" +
                                    "command       /usr/bin/find\n", cronParser.parse("23 0-20/2 1,15 * 1-5 /usr/bin/find"));

        Assert.assertEquals("minute        5\n" +
                                    "hour          4\n" +
                                    "day of month  1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31\n" +
                                    "month         1 2 3 4 5 6 7 8 9 10 11 12\n" +
                                    "day of week   0\n" +
                                    "command       /usr/bin/find\n", cronParser.parse("5 4 * * SUN /usr/bin/find"));

        Assert.assertEquals("minute        10 44\n" +
                                    "hour          14\n" +
                                    "day of month  1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31\n" +
                                    "month         3\n" +
                                    "day of week   0 1 2 3 4 5 6\n" +
                                    "command       /usr/bin/find\n", cronParser.parse("10,44 14 * 3 SUN-SAT /usr/bin/find"));

        Assert.assertEquals("minute        10 44\n" +
                                    "hour          14\n" +
                                    "day of month  1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31\n" +
                                    "month         3\n" +
                                    "day of week   0 2 4\n" +
                                    "command       /usr/bin/find\n", cronParser.parse("10,44 14 * 3 SUN-FRI/2 /usr/bin/find"));

        Assert.assertEquals("minute        15\n" +
                                    "hour          10\n" +
                                    "day of month  1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31\n" +
                                    "month         1 2 3 4 5 6 7 8 9 10 11 12\n" +
                                    "day of week   1 2 3 4 5\n" +
                                    "command       /usr/bin/find\n", cronParser.parse("15 10 * * MON-FRI /usr/bin/find"));

        Assert.assertEquals("minute        10 12 14 16 18 20 22 24 26 28 30 32 34 36 38 40 42 44\n" +
                                    "hour          14\n" +
                                    "day of month  4 5 6 7 8 9 10 11 12 13 14 15 16 17\n" +
                                    "month         11 12 1 2 3 4\n" +
                                    "day of week   5 6 0 1 2 3 4\n" +
                                    "command       /usr/bin/find\n", cronParser.parse("10-44/2 14 4-17 NOV-APR FRI-THU /usr/bin/find"));
    }

    @Test
    public void testInvalidCronPatterns() {
        Assert.assertEquals(INVALID_CRON_STRING, cronParser.parse("*/15 0 1,15 * 1-5 7 /usr/bin/find"));
        Assert.assertEquals(INVALID_CRON_STRING, cronParser.parse("100/15 0 1,15 * 1-5 /usr/bin/find"));
    }

}
