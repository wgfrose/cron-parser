# Cron Parser Utility
A basic CLI cron pattern parser to expand the time intervals provided in a given pattern.

### Usage on macOS / Linux
1. Download the `cron-parser.jar` file from the repo
2. Ensure you have Java installed, this was built on Java 15.0.2
3. Run `java -jar /path/to/cron-parser.jar` in the terminal  followed by your cron pattern argument - e.g. `java -jar /path/to/cron-parser.jar "10,44 14 * 3 SUN-SAT /usr/bin/find"`

This parser supports the standard 5-field cron format: minutes, hours, days of the month, month and days of the week. It does not support the special characters `?`, `W`, `L` or `#`. The `@yearly` non-standard values are also not supported.

It does support `JAN-DEC` and `SUN-SAT` name values for months and weekdays. 

Also note the macOS / Linux command location at the end that needs to be provided.

### Acceptable cron pattern format examples:
- `4 5 * * MON-FRI /usr/bin/find`
- `23 0-20/2 1,15 * 1-5 /usr/bin/find`
- `5 4 * * SUN /usr/bin/find`
- `10,44 14 * 3 SUN-SAT /usr/bin/find`
- `10-44 14 * 3 SUN-FRI/2 /usr/bin/find`
- `2,58 14 * 3 */2 /usr/bin/find`

### Notes on the cron standard:
- `6 8 31 FEB *` - 31st Feb is not a real date, yet according to the standard, is still a valid cron pattern along with other invalid dates.
- The working week starts on a Sunday not a Monday as I assumed, so that is why I interpreted `SUN-SAT` to mean `6-5` and not `0-6`. This is why the utility here supports going into the next week or month when specifying ranges in those fields. This means when specifying ranges of days of the week or months, you can specify ranges within the same week/year such as `JAN-MAR` or `TUE-SAT`, but you can also span to the next period and use `FRI-MON` or `OCT-APR`. Was not sure if the spanning the next period was part of the standard but included it just in case.

### Notes on validation:
- I used regex to ignore if the argument supplied has more than one space between fields.
- If a pattern is invalid it returns just a basic string, but this can be expanded to include the reasons why a pattern is invalid.