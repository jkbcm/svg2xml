package org.xmlcml.svgplus.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.xmlcml.svgplus.command.AbstractAction;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.document.DocumentIteratorElement;
import org.xmlcml.svgplus.page.PageActionElement;

/*
 * rootElement
 */
public class SemanticDocumentElement extends AbstractActionElement {

	public final static String TAG ="semanticDocument";

	private static final String COMMAND_DIRECTORY = "src/main/resources/org/xmlcml/graphics/styles";
	private static final String DEFAULT_COMMAND_FILENAME = COMMAND_DIRECTORY+"/"+"basic.xml";
	
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
		ATTNAMES.add(PageActionElement.DEBUG);
	}

	public static String getDefaultCommandFilename() { 
		return DEFAULT_COMMAND_FILENAME;
	}
	
	private DocumentIteratorElement documentIteratorElement;
	private AbstractAction semanticDocumentAction;

	/** constructor
	 */
	public SemanticDocumentElement() {
		super(TAG);
		semanticDocumentAction = new SemanticDocumentAction(this);
	}
	
	/** constructor
	 */
	public SemanticDocumentElement(AbstractActionElement element) {
        super(element);
        init();
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SemanticDocumentElement(this);
    }

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	public DocumentIteratorElement getDocumentIteratorElement() {
		if (documentIteratorElement == null) {
			Nodes nodes = this.query(DocumentIteratorElement.TAG);
			documentIteratorElement =  (nodes.size() == 1) ? (DocumentIteratorElement) nodes.get(0) : null;
		}
		return documentIteratorElement;
	}

	public static SemanticDocumentElement createSemanticDocument(Element element) {
		SemanticDocumentElement semanticDocumentElement = null;
		AbstractActionElement actionElement = AbstractActionElement.createActionElement(element);
		if (actionElement != null && actionElement instanceof SemanticDocumentElement) {
			semanticDocumentElement = (SemanticDocumentElement) actionElement;
			SemanticDocumentAction semanticDocumentAction = (SemanticDocumentAction) actionElement.getAction();
			semanticDocumentElement.setAllDescendants(semanticDocumentAction);
		}
		return semanticDocumentElement;
	}

	private void setAllDescendants(SemanticDocumentAction semanticDocumentAction) {
		Nodes elements = this.query("//*");
		for (int i = 0; i <elements.size(); i++) {
			Element element = (Element) elements.get(i);
			if (element instanceof AbstractActionElement) {
				AbstractActionElement actionElement = (AbstractActionElement) element; 
				AbstractAction action = actionElement.getAction();
				action.setSemanticDocumentAction(semanticDocumentAction);
			}
		}
	}

	/** factory
	 * 
	 * @param file
	 * @return
	 */
	public static SemanticDocumentElement createSemanticDocument(File file) {
		AbstractActionElement commandElement = AbstractActionElement.createActionElement(file);
		if (!(commandElement instanceof SemanticDocumentElement)) {
			throw new RuntimeException("commandFile must have root Element: "+SemanticDocumentElement.TAG);
		}
		
		SemanticDocumentElement semanticDocumentElement = (SemanticDocumentElement) commandElement;
		semanticDocumentElement.setFilename(file.getAbsolutePath());
		return semanticDocumentElement;
	}

	private void setFilename(String filename) {
		this.addAttribute(new Attribute(FILENAME, filename));
	}
	

	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	protected List<String> getRequiredAttributeNames() {
		return Arrays.asList(new String[]{
		});
	}

	@Override
	protected AbstractAction createAction() {
		return new SemanticDocumentAction(this);
	}
	
}