# Cron Parser Utility
A basic CLI cron pattern parser to expand the time intervals provided in a given pattern.

###Usage
1. Download the `cron-parser.jar` file from the repo
2. Ensure you have Java installed, this was built on version 15.0.2
3. Run `java -jar /path/to/cron-parser.jar` in the terminal  followed by your cron pattern argument - e.g. `java -jar /path/to/cron-parser.jar "10,44 14 * 3 SUN-SAT /usr/bin/find"`

This parser supports the standard 5-field cron format: minutes, hours, days of the month, month and days of the week. It does not support the special characters `?`, `W`, `L` or `#`.

It does support `JAN-DEC` and `SUN-SAT` name values for months and weekdays.

Also note the Linux / macOS command location at the end that needs to be provided.

Acceptable cron pattern format examples:
- `4 5 * * MON-FRI /usr/bin/find`
- `23 0-20/2 1,15 * 1-5 /usr/bin/find`
- `5 4 * * SUN /usr/bin/find`
- `10,44 14 * 3 SUN-SAT /usr/bin/find`
- `10-44 14 * 3 SUN-FRI/2 /usr/bin/find`
- `2,58 14 * 3 */2 /usr/bin/find`

