package org.xmlcml.svg2xml.semantic;

import java.util.Set;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.svg2xml.analyzer.AbstractAnalyzer;
import org.xmlcml.svg2xml.analyzer.ChunkId;
import org.xmlcml.svg2xml.analyzer.PDFIndex;

/**
 * @author pm286
 *
 */
public class LicenceAnalyzer extends AbstractAnalyzer {
	private static final Logger LOG = Logger.getLogger(LicenceAnalyzer.class);
	public static final Pattern PATTERN = Pattern.compile(".*(" +
			"([Ll][Ii][Cc][Ee][Nn][CcSs][Ee])|" +
			"(\u00A9)|" +
			"([Cc][Oo][Pp][Yy][Rr][Ii][Gg][Hh][Tt])|" +
			"([Cc][Rr][Ee][Aa][Tt][Ii][Vv][Ee]\\s*[Cc][Oo][Mm][Mm][Oo][Nn][Ss])|" +
			"([Aa][Ll][Ll]\\s*[Rr][Ii][Gg][Hh][Tt][Ss]\\s*[Rr][Ee][Ss][Ee][Rr][Vv][Ee][Dd])" +
			").*", Pattern.DOTALL);
	public final static String TITLE = "LICENCE";
	
	public LicenceAnalyzer(PDFIndex pdfIndex) {
		super(pdfIndex);
	}
	
	public void analyze() {
	}
	
	@Override
	public SVGG oldAnnotateChunk() {
		throw new RuntimeException("annotate NYI");
	}
	
	public Integer indexAndLabelChunk(String content, ChunkId id) {
		if (content.contains("eserved")) {
			LOG.trace("LIC: "+content);
		}
		Integer serial = super.indexAndLabelChunk(content, id);
		if (serial != null) {
			LOG.trace("LICENCE "+content);
		}
		// index...
		return serial;
	}
	
	/** Pattern for the content for this analyzer
	 * 
	 * @return pattern (default null)
	 */
	@Override
	protected Pattern getPattern() {
		return PATTERN;
	}

	/** (constant) title for this analyzer
	 * 
	 * @return title (default null)
	 */
	@Override
	public String getTitle() {
		return TITLE;
	}

}