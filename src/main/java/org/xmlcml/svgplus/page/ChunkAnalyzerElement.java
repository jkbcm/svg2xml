package org.xmlcml.svgplus.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.xmlcml.svgplus.command.AbstractAction;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.document.DocumentWriterAction;


public class ChunkAnalyzerElement extends AbstractActionElement {

	public final static String TAG ="chunkAnalyzer";
	
	private static final List<String> ATTNAMES = new ArrayList<String>();
	public static final String SUBSUP = "subSup";
	public static final String REMOVE_NUMERIC_TSPANS = "removeNumericTSpans";
	public static final String SPLIT_AT_SPACES = "splitAtSpaces";
	
	/** attribute names
	 * 
	 */

	static {
		ATTNAMES.add(PageActionElement.XPATH);
		ATTNAMES.add(SUBSUP);
		ATTNAMES.add(REMOVE_NUMERIC_TSPANS);
	}

	/** constructor
	 */
	public ChunkAnalyzerElement() {
		super(TAG);
		init();
	}
	
	protected void init() {
	}
	
	/** constructor
	 */
	public ChunkAnalyzerElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new ChunkAnalyzerElement(this);
    }

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	protected List<String> getRequiredAttributeNames() {
		return Arrays.asList(new String[]{
				AbstractActionElement.XPATH,
		});
	}

	@Override
	protected AbstractAction createAction() {
		return new ChunkAnalyzerAction(this);
	}

}