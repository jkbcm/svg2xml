package org.xmlcml.svgplus;

import java.io.File;

import nu.xom.Builder;
import nu.xom.Element;

import org.junit.Assert;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.core.SemanticDocumentAction;
import org.xmlcml.svgplus.core.SemanticDocumentElement;

public class Fixtures {

	public static final String SVGPLUS_DIR = "src/test/resources/org/xmlcml/svgplus/";
	public static final String COMMAND_DIR= SVGPLUS_DIR+"command/";
	public static final String CORE_DIR = SVGPLUS_DIR+"core/";
	public static final String AJC_PAGE6_PDF = CORE_DIR+"ajc-page6.pdf";
	public final static File NOOP_FILE = new File(CORE_DIR+"noopTst.xml");
	public final static File BASIC_FILE = new File(CORE_DIR+"basicTst.xml");
	public static final File INCLUDE_TEST_FILE = new File(CORE_DIR+"includeTst.xml");
	public static final File INFILE_TEST = new File(CORE_DIR+"infileTst.xml");
	public static final File ASSERT_TST = new File(COMMAND_DIR+"assertTst.xml");
	public static final File NO_ASSERT_TST = new File(COMMAND_DIR+"noAssertTst.xml");
	public static final File VARIABLE_TST = new File(COMMAND_DIR+"variableTst.xml");
	public static final File WHITESPACE_CHUNKER_TST = new File(Fixtures.COMMAND_DIR+"whitespaceChunkerTst.xml");
	
	public static SemanticDocumentAction getSemanticDocumentAction(File file) {
		SemanticDocumentAction semanticDocumentAction = null;
		try {
			Element element = new Builder().build(file).getRootElement();
			SemanticDocumentElement semanticDocumentElement = SemanticDocumentElement.createSemanticDocument(element);
			semanticDocumentAction = semanticDocumentElement.getSemanticDocumentAction();
		} catch (Exception e) {
			throw new RuntimeException("Cannot create semanticDocumentAction ", e);
		}
		return semanticDocumentAction;
	}
}
