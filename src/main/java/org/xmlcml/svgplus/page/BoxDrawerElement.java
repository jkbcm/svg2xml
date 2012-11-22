package org.xmlcml.svgplus.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.command.AbstractAction;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.document.DocumentWriterAction;


public class BoxDrawerElement extends AbstractActionElement {

	private final static Logger LOG = Logger.getLogger(BoxDrawerElement.class);
	
	public final static String TAG ="boxDrawer";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	/** attribute names
	 * 
	 */

	static {
//		ATTNAMES.add(PageActionElement.ACTION);
		ATTNAMES.add(PageActionElement.FILL);
		ATTNAMES.add(PageActionElement.OPACITY);
		ATTNAMES.add(PageActionElement.STROKE_WIDTH);
		ATTNAMES.add(PageActionElement.STROKE);
		ATTNAMES.add(PageActionElement.TITLE);
		ATTNAMES.add(PageActionElement.XPATH);
	}

	/** constructor
	 */
	public BoxDrawerElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public BoxDrawerElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new BoxDrawerElement(this);
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
		return new BoxDrawerAction(this);
	}

}
