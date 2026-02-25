package com.log10x.decode.main.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class representing an event template in the Log10x system.
 * 
 * @author Dor Levi
 *
 */
public class TemplateEntry {

	private static final Logger logger = LoggerFactory.getLogger(TemplateEntry.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * A unique hash id in the Log10x system for identifying this template.
	 */
	public final String templateHash;
	
	/**
	 * The actual template.
	 */
	public final String template;

	TemplateEntry() {
		this(null, null);
	}

	/**
	 * @param templateHash hash id of this template
	 * @param template     actual template
	 */
	public TemplateEntry(String templateHash, String template) {
		this.templateHash = templateHash;
		this.template = template;
	}

	/**
	 * Constructs a {@link TemplateEntry} from a json representation
	 * 
	 * @param s a json representation of a {@link TemplateEntry}
	 * @return a new {@link TemplateEntry}
	 */
	public static TemplateEntry fromJson(String s) {
		try {
			return mapper.readValue(s, TemplateEntry.class);
		} catch (JsonProcessingException e) {
			logger.warn("Failed parsing TemplateEntry - {}", s, e);

			return null;
		}
	}
}