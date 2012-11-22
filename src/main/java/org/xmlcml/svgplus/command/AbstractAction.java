package org.xmlcml.svgplus.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.svgplus.core.SemanticDocumentAction;
import org.xmlcml.svgplus.core.SemanticDocumentElement;
import org.xmlcml.svgplus.page.PageActionElement;

/** generated by an AbstractActionElement
 * contains the attribute logic because some commands have different actions
 *  
 * @author pm286
 *
 */
public abstract class AbstractAction {

	private final static Logger LOG = Logger.getLogger(AbstractAction.class);
	
	public final static Pattern VARIABLE_REF = Pattern.compile("\\$\\{([^\\{]*)\\}");
	private static final String TRUE = "true";

	protected AbstractActionElement actionElement;
	protected SemanticDocumentAction semanticDocumentAction; // so all subclasses can access this

	public AbstractAction(AbstractActionElement actionElement) {
		this.setActionElement(actionElement);
//		if (actionElement instanceof SemanticDocumentElement) {
//			this.semanticDocumentAction = (SemanticDocumentAction) this;
//		} else {
//			SemanticDocumentElement semanticDocumentElement = actionElement.getSemanticDocumentElement();
//			this.semanticDocumentAction = semanticDocumentElement == null ? null : (SemanticDocumentAction) actionElement.getSemanticDocumentElement().getAction();
//		}
	}

	/** only used for constructing objects programmatically
	 * 
	 */
	protected AbstractAction() {
	}

	/** execute the command
	 * 
	 */
	public abstract void run();

	public void setSemanticDocumentAction(SemanticDocumentAction semanticDocumentAction) {
		this.semanticDocumentAction = semanticDocumentAction;
	}
	
	public String getFilename() {
		String filename = getAndExpand(AbstractActionElement.FILENAME);
		if (filename != null) {
			try {
				filename = new File(filename).getCanonicalPath();
			} catch (Exception e) {
				throw new RuntimeException("Bad filename: "+filename);
			}
		}
		return filename;
	}

	public String getName() {
		return getAndExpand(AbstractActionElement.NAME);
	}

	public String getMark() {
		return getAndExpand(AbstractActionElement.MARK);
	}

	protected String getAndExpand(String attName) {
		String value = getActionElement().getAttributeValue(attName);
		return expandVariables(value);
	}

	protected String getAndExpand(String attName, String defaultValue) {
		String value = getValue(attName, defaultValue);
		return expandVariables(value);
	}

	protected String getValue(String attName, String defaultValue) {
		String value = getActionElement().getAttributeValue(attName);
		value = (value == null) ? defaultValue : value;
		return value;
	}

	public String[] getDeleteNamespaces() {
		String s = getAndExpand(AbstractActionElement.DELETE_NAMESPACES);
		String[] ss = null;
		if (s != null) {
			ss = s.split(CMLConstants.S_WHITEREGEX);
		}
		return ss;
	}

	public Boolean getDebug() {
		String  debugString = getAndExpand(AbstractActionElement.DEBUG);
		return debugString == null ? null : TRUE.equalsIgnoreCase(debugString);
	}

	public String getFormat() {
		return getAndExpand(AbstractActionElement.FORMAT);
	}

	public String getFormat(String defaultFormat) {
		String format = getAndExpand(AbstractActionElement.FORMAT);
		return format == null ? defaultFormat : format;
	}

	public String getLog() {
		return getAndExpand(AbstractActionElement.LOGAT);
	}

	protected boolean isTrue(String attName) {
		String val = getAndExpand(attName);
		return new Boolean(val);
	}

	/** gets Integer from attribute
	 * fails silently
	 * 
	 * @param name attributeName
	 * @return
	 */
	protected Integer getInteger(String name) {
		Integer ii = null;
		String s = getAndExpand(name);
		try {
			ii = new Integer(s);
		} catch (Exception e) {
		}
		return ii;
	}

	/** gets Integer from attribute
	 * fails silently
	 * 
	 * @param name attributeName
	 * @param defaultValue
	 * @return
	 */
	protected Integer getInteger(String name, Integer defaultValue) {
		Integer ii = getInteger(name);
		return ii == null ? defaultValue : ii;
	}

	/** gets Long from attribute
	 * fails silently
	 * 
	 * @param name attributeName
	 * @return
	 */
	protected Long getLong(String name) {
		Long ll = null;
		String s = getAndExpand(name);
		try {
			ll = new Long(s);
		} catch (Exception e) {
		}
		return ll;
	}

	/** gets Long from attribute
	 * fails silently
	 * 
	 * @param name attributeName
	 * @param defaultValue
	 * @return
	 */
	protected Long getLong(String name, Long defaultValue) {
		Long ii = getLong(name);
		return ii == null ? defaultValue : ii;
	}

	/** gets Double from attribute
	 * fails silently
	 * 
	 * @param name attributeName
	 * @return
	 */
	protected Double getDouble(String name) {
		Double dd = null;
		String s = getAndExpand(name);
		try {
			dd = new Double(s);
		} catch (Exception e) {
		}
		return dd;
	}

	/** gets Double from attribute
	 * fails silently
	 * 
	 * @param name attributeName
	 * @param defaultValue
	 * @return
	 */
	protected Double getDouble(String name, Double defaultValue) {
		Double dd = getDouble(name);
		return dd == null ? defaultValue : dd;
	}

	public String getActionValue() {
		return getAndExpand(AbstractActionElement.ACTION);
	}

	protected void debug(String string) {
		if (AbstractActionElement.DEBUG.equals(getLog())) {
			LOG.debug(string);
		}
	}

	protected String expandVariables(String value) {
		String val = null;
		if (value != null) {
			Matcher matcher = VARIABLE_REF.matcher(value);
			StringBuilder sb = new StringBuilder();
			int current = 0;
			while (matcher.find()) {
				int start = matcher.start();
				sb.append(value.substring(current, start));
				int end = matcher.end();
				String name = matcher.group(1);
				Object newValue = semanticDocumentAction.getVariable(name);
				if (newValue == null) {
					throw new RuntimeException("Cannot find variable: "+name+" in "+value);
				}
				if (newValue instanceof String) {
					sb.append(newValue.toString());
				}
				current = end;
			}
			sb.append(value.substring(current));
			val = sb.toString();
		}
		return val;
	}
	
	public void setActionElement(AbstractActionElement actionElement) {
		this.actionElement = actionElement;
	}

	public String getCount() {
		return getAndExpand(AbstractActionElement.COUNT);
	}

	public String getMessage() {
		// don't expand this
		return getActionElement().getAttributeValue(AbstractActionElement.MESSAGE);
	}

	public String getRegex() {
		// don't expand this
		return getActionElement().getAttributeValue(AbstractActionElement.REGEX);
	}

	/** gets skip attribute value or null
	 * 
	 * @return
	 */
	public String getSkip() {
		String skip = getAndExpand(AbstractActionElement.SKIP_IF_EXISTS);
		return skip;
	}

	/** splits the skip string by whitespace
	 * 
	 * @return empty list if missing
	 */
	public List<String> getSkipList() {
		List<String> skipList = new ArrayList<String>();
		String skip = getSkip();
		if (skip != null) {
			String[] skips = skip.split(CMLConstants.S_WHITEREGEX);
			skipList = Arrays.asList(skips);
		}
		return skipList;
	}

	public Long getTimeout(long defaultTimeout) {
		return getLong(AbstractActionElement.TIMEOUT);
	}

	public String getTitle() {
		// don't expand this
		return getActionElement().getAttributeValue(AbstractActionElement.TITLE);
	}

	public String getValue() {
		return getAndExpand(PageActionElement.VALUE);
	}

	public String getXPath() {
		return getAndExpand(AbstractActionElement.XPATH);
	}

	/**
	 * get the commandElement that generated the action
	 * @return
	 */
	public AbstractActionElement getActionElement() {
		return this.actionElement;
	}

	protected List<AbstractAction> getChildActionList() {
		Elements childElements = actionElement.getChildElements();
		List<AbstractAction> childActionList = new ArrayList<AbstractAction>();
		for (int i = 0; i < childElements.size(); i++) {
			Element childActionElement = childElements.get(i);
			if (!(childActionElement instanceof AbstractActionElement)) {
				throw new RuntimeException("Element not allowed: "+childActionElement.toXML());
			}
			AbstractActionElement actionElement = (AbstractActionElement) childActionElement;
			AbstractAction abstractAction = actionElement.getAction();
			childActionList.add(abstractAction);
		}
		return childActionList;
	}

	protected void runChildActionList() {
		List<AbstractAction> childActionList = getChildActionList();
		for (AbstractAction abstractAction : childActionList) {
			LOG.debug("running: "+abstractAction);
			// maybe put filter in here
			abstractAction.run();
		}
	}

	/** the variable is also available as protected
	 * 
	 * @return
	 */
	public SemanticDocumentAction getSemanticDocumentAction() {
		return semanticDocumentAction;
	}


}
