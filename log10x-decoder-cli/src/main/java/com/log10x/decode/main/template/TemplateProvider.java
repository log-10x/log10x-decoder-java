package com.log10x.decode.main.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.log10x.decode.lexer.TokenizeContext;
import com.log10x.decode.template.Template;
import com.log10x.decode.template.TemplateLexer;
import com.log10x.decode.util.StringUtil;

/**
 * Class for providing a cached access to {@link Template} objects.
 * 
 * Each instance is initialized with plain {@code String} templates, and each is
 * only tokenized into a {@link Template} object on demand
 * 
 * @author Dor Levi
 *
 */
public class TemplateProvider {
	private static final Logger logger = LoggerFactory.getLogger(TemplateProvider.class);

	private final TokenizeContext context;
	private final Map<String, String> rawTemplates;
	private final Map<String, Template> templates;

	/**
	 * @param context      {@link TokenizeContext} for this {@link TemplateProvider}
	 * @param rawTemplates a {@code Map<String, String>} from hashes to a simple
	 *                     {@code String} representation of templates.
	 */
	public TemplateProvider(TokenizeContext context, Map<String, String> rawTemplates) {
		this.context = context;
		this.rawTemplates = rawTemplates;
		this.templates = new ConcurrentHashMap<>();
	}

	/**
	 * Returns the {@link Template} matching to the input {@code hash}
	 * 
	 * If the result {@link Template} was already initialized in the past, returns
	 * it from cache. Otherwise, will first tokenize the raw {@code String} matching
	 * this template into a {@link Template} object, cache that, and return it.
	 * 
	 * @param hash The hash representing the requested {@link Template}
	 * @return the matching {@link Template}
	 */
	public Template getTemplate(String hash) {
		Template result = this.templates.get(hash);

		if (result != null) {
			return result;
		}

		return addTemplate(hash);
	}

	private Template addTemplate(String hash) {
		Template result;

		synchronized (this.templates) {

			result = templates.get(hash);

			if (result == null) {
				result = createTemplate(hash);

				if (result != null) {
					this.templates.put(hash, result);
				}
			}
		}

		return result;
	}

	private Template createTemplate(String hash) {
		String rawTemplate = rawTemplates.get(hash);

		if (StringUtil.isNullOrEmpty(rawTemplate)) {
			logger.warn("No raw template found for {}.", hash);

			return null;
		}

		try {
			TemplateLexer lexer = new TemplateLexer(context, hash);
			return lexer.tokenize(rawTemplate);
		} catch (Exception e) {
			logger.error("Failed to build template {} - {}", hash, rawTemplate, e);

			return null;
		}
	}

	/**
	 * {@code warnDuplicates} defaults to {@code true}
	 * 
	 * @see #fromFile(TokenizeContext, String, boolean)
	 * 
	 * @param context  {@link TokenizeContext} for the created templates.
	 * @param filename name of the file to read templates from.
	 * @return resulting {@link TemplateProvider}
	 */
	public static TemplateProvider fromFile(TokenizeContext context, String filename) {
		return fromFile(context, filename, true);
	}

	/**
	 * Creates a new {@link TemplateProvider} from the provided {@code context} and
	 * {@code filename}
	 * 
	 * If {@code warnDuplicates} is {@code true}, will emit a log warning whenever
	 * encountering a duplicate template hash in the file.
	 * 
	 * @param context        {@link TokenizeContext} for the created templates.
	 * @param filename       name of the file to read templates from.
	 * @param warnDuplicates if set to {@code true}, will emit a log warning
	 *                       whenever encountering a duplicate template hash in the
	 *                       file.
	 * @return resulting {@link TemplateProvider}
	 */
	public static TemplateProvider fromFile(TokenizeContext context, String filename, boolean warnDuplicates) {
		return new TemplateProvider(context, getEntriesMap(filename, warnDuplicates));
	}

	private static Map<String, String> getEntriesMap(String filename, boolean warnDuplicates) {
		Map<String, String> result = new HashMap<>();

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			for (String line; (line = br.readLine()) != null;) {
				TemplateEntry entry;

				try {
					entry = TemplateEntry.fromJson(line);
				} catch (Exception e) {
					logger.warn("Non matching template entry line - {}.", line, e);
					continue;
				}

				if ((warnDuplicates) && (result.containsKey(entry.templateHash))) {
					logger.warn("Encountered duplicate template hash {} in file {}.", entry.templateHash, filename);
					continue;
				}

				result.put(entry.templateHash, entry.template);
			}
		} catch (Exception e) {
			logger.warn("Error while reading template entries from file {}.", filename, e);
		}

		return result;
	}
}
