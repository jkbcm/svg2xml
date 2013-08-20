package org.xmlcml.svg2xml.analyzer;

import java.io.File;

import org.junit.Test;
import org.xmlcml.svg2xml.Fixtures;

public class PDFAnalyzerTest {

	@Test
	public void Dummy() {
	}

	@Test
	// maven fails on memory but Eclipse runs
//	@Ignore
	public void testPDFAnalyzerPDF() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFFile(Fixtures.MULTIPLE312_PDF);
	}


	@Test
	// maven fails on memory
//	@Ignore
	public void testPDFAnalyzerSVG() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.setRawSvgDirectory(Fixtures.SVG_MULTIPLE312_DIR);
		analyzer.analyzeRawSVGPagesWithPageAnalyzers();
	}
	
	@Test
	public void testKevinACS() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFFile(new File("../pdfs/acs/nn400656n.pdf"));
	}
	
}
