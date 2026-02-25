package com.log10x.decode.timestamp;

import java.io.Writer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Calendar;

import com.log10x.decode.util.EpochUtil;

/**
 * 
 * {@link Timestamp} class for describing an date time format based timestamp.
 * 
 * @author Dor Levi
 *
 */
public class FormatterTimestamp extends Timestamp {

	private transient final Calendar calender;

	private transient final ZoneId zoneId;

	private transient DateTimeFormatter formatter;

	/**
	 * Creates a new instance from provided date time format, calendar and zone
	 * 
	 * @param pattern  Date time format pattern to use, such as "MMM dd HH:mm:ss"
	 * @param calender {@link Calendar} instance for this {@link Timestamp}
	 * @param zoneId   {@link ZoneId} instance for this {@link Timestamp}
	 */
	public FormatterTimestamp(String pattern, Calendar calender, ZoneId zoneId) {

		super(pattern);

		this.calender = calender;
		this.zoneId = zoneId;
	}

	private DateTimeFormatter formatter() {

		if (this.formatter != null) {
			return this.formatter;
		}

		synchronized (this) {

			if (this.formatter != null) {
				return this.formatter;
			}

			DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder().appendPattern(pattern());

			if (this.calender != null) {

				builder.parseDefaulting(ChronoField.YEAR_OF_ERA, calender.get(Calendar.YEAR))
						.parseDefaulting(ChronoField.MONTH_OF_YEAR, calender.get(Calendar.MONTH))
						.parseDefaulting(ChronoField.DAY_OF_MONTH, calender.get(Calendar.DAY_OF_MONTH))
						.parseDefaulting(ChronoField.HOUR_OF_DAY, 0).parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
						.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0);
			}

			this.formatter = builder.toFormatter().withZone(zoneId);
		}

		return this.formatter;
	}

	@Override
	public void format(long epoch, Writer writer) {

		Instant instant = EpochUtil.fromEpoch(epoch);

		formatter().formatTo(instant, writer);
	}
}
