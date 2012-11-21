package org.xmlcml.svgplus.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.xmlcml.svgplus.command.AbstractAction;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.document.DocumentWriterAction;


public class PageWriterElement extends AbstractActionElement {

	public final static String TAG ="pageWriter";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	public static String MAKE_DISPLAY = "makeDisplay";

	static {
		ATTNAMES.add(PageActionElement.ACTION);
		ATTNAMES.add(PageActionElement.DELETE_XPATHS);
		ATTNAMES.add(PageActionElement.DELETE_NAMESPACES);
		ATTNAMES.add(PageActionElement.FILENAME);
		ATTNAMES.add(PageActionElement.FORMAT);
		ATTNAMES.add(PageActionElement.NAME);
		ATTNAMES.add(PageActionElement.XPATH);
		ATTNAMES.add(MAKE_DISPLAY);
	}

	/** constructor
	 */
	public PageWriterElement() {
		super(TAG);
		init();
	}
	
	protected void init() {
	}
	
	/** constructor
	 */
	public PageWriterElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new PageWriterElement(this);
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
		return new PageWriterAction(this);
	}

}