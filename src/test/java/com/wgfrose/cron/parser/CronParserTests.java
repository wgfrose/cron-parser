package com.wgfrose.cron.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CronParserTests {

    private CronParser cronParser = new CronParser();

    @Test
    public void testValidCronPatterns() {
        System.out.println(cronParser.parse("23 0-20/2 1,15 * 1-5 /usr/bin/find"));
//        Assert.assertEquals("Valid pattern!", cronParser.parse("23 0-20/2 1,15 * 1-5 /usr/bin/find"));
//        Assert.assertEquals("Valid pattern!", cronParser.parse("5 4 * * SUN /usr/bin/find"));
//        Assert.assertEquals("Valid pattern!", cronParser.parse("10,44 14 * 3 WED /usr/bin/find"));
//        Assert.assertEquals("Valid pattern!", cronParser.parse("15 10 * * MON-FRI /usr/bin/find"));
    }

    @Test
    public void testInvalidCronPatterns() {
        //Assert.assertEquals("Invalid cron pattern", cronParser.parse("*/15 0 1,15 * 1-5 7 /usr/bin/find"));
    }

}
