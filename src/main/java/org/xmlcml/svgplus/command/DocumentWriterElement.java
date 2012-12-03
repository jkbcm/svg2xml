package org.xmlcml.svgplus.command;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;

public class DocumentWriterElement extends AbstractActionElement {
	
	private final static Logger LOG = Logger.getLogger(DocumentWriterElement.class);

	public final static String TAG ="documentWriter";

	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
//		ATTNAMES.add(PageActionElement.ACTION);
		ATTNAMES.add(PageActionElement.FILENAME);
		ATTNAMES.add(PageActionElement.FORMAT);
		ATTNAMES.add(PageActionElement.REGEX);
		ATTNAMES.add(PageActionElement.XPATH);
	}

	/** constructor
	 */
	public DocumentWriterElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public DocumentWriterElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new DocumentWriterElement(this);
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
				AbstractActionElement.FILENAME,
		});
	}

	@Override
	protected AbstractAction createAction() {
		return new DocumentWriterAction(this);
	}
}