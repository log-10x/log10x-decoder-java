package com.log10x.decode.options;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.log10x.decode.timestamp.FormatterTimestamp;
import com.log10x.decode.timestamp.Timestamp;
import com.log10x.decode.timestamp.epoch.EpochMilliTimestamp;
import com.log10x.decode.timestamp.epoch.EpochNanoTimestamp;
import com.log10x.decode.util.ToString;
import com.log10x.decode.util.chars.CharArraySequence;

/**
 * 
 * Options used for containing and getting timestamp patterns.
 * 
 * For proper decoding, those options need to match the ones used for encoding
 * events.
 * 
 * @author Dor Levi
 *
 */
public class TimestampOptions {

	private final Set<String> patterns;
	private final String zone;
	private final Map<CharArraySequence, FormatterTimestamp> patternsMap;

	private transient Calendar contextCalender;
	private transient ZoneId zoneId;

	/**
	 * Creates an instance with default timestamp patterns.
	 */
	public TimestampOptions() {

		this(TimestampConsts.DEFAULT_TIMESTAMP_PATTERNS, null);
	}

	/**
	 * Creates an instance with custom timestamp patterns and zone.
	 * 
	 * @param patterns A {@link Set} containing all timestamp patterns.
	 * @param zone     A {@link ZoneId} as a {@link String}
	 */
	public TimestampOptions(Set<String> patterns, String zone) {

		this.patterns = patterns;
		this.zone = zone;

		this.patternsMap = new HashMap<>(patterns.size());

		initializePatterns();
	}

	private void initializePatterns() {

		for (String pattern : patterns()) {

			addPattern(pattern);
		}
	}

	/**
	 * @return A {@link Set} of all patterns defined.
	 */
	public Set<String> patterns() {

		return (this.patterns != null) ? this.patterns : Collections.emptySet();
	}

	private ZoneId zoneId() {

		if (this.zoneId != null) {
			return this.zoneId;
		}

		this.zoneId = (this.zone != null) ? ZoneId.of(this.zone) : ZoneId.systemDefault();

		return this.zoneId;
	}

	/**
	 * Get's a {@link Timestamp} matching the provided timestamp format (i.e
	 * something like "MMM d HH:mm:ss")
	 * 
	 * @param pattern Timestamp format pattern
	 * @return Matching {@link Timestamp}, cached if possible, a new instance if
	 *         it's the first time {@code pattern} is requested.
	 */
	public Timestamp getPattern(CharArraySequence pattern) {

		if (pattern.contentEquals(EpochNanoTimestamp.EPOCH_NANO)) {
			return EpochNanoTimestamp.Instance;
		}

		if (pattern.contentEquals(EpochMilliTimestamp.EPOCH)) {
			return EpochMilliTimestamp.Instance;
		}

		FormatterTimestamp existing = patternsMap.get(pattern);

		if (existing != null) {
			return existing;
		}

		return addPattern(pattern.asString());
	}

	private FormatterTimestamp addPattern(String pattern) {

		FormatterTimestamp result = new FormatterTimestamp(pattern, contextCalender, zoneId());

		patternsMap.put(new CharArraySequence(pattern), result);

		return result;
	}

	@Override
	public String toString() {
		return ToString.format("lexerPatterns", patterns.size());
	}
}