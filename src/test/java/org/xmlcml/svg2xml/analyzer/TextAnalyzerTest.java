package org.xmlcml.svg2xml.analyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import junit.framework.Assert;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.test.StringTestBase;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.text.SvgPlusCoordinate;
import org.xmlcml.svg2xml.text.TextLine;
import org.xmlcml.svg2xml.text.TextStructurer;
import org.xmlcml.svg2xml.text.ScriptLine;
import org.xmlcml.svg2xml.text.TextLineSet;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

public class TextAnalyzerTest {

	private final static Logger LOG = Logger.getLogger(TextAnalyzerTest.class);

	private final static char MINUS = (char)8722;
	private final static char WHITE_BULLET = (char)9702;
	
	/** checks there are 4 lines in para
	 * 
	 */
	@Test
	public void analyze1ParaTest() {
		List<TextLine> textLineList = TextStructurer.createTextLineList(Fixtures.PARA1_SVG);
		Assert.assertEquals("lines ", 4, textLineList.size());
	}

	/** checks 52 characters in first line */
	@Test
	public void analyzeLine() {
		List<TextLine> textLineList = TextStructurer.createTextLineList(Fixtures.PARA1_SVG);
		TextLine textLine0 = textLineList.get(0);
		List<SVGText> characters = textLine0.getSVGTextCharacters();
		Assert.assertEquals("textLine0", 52, characters.size());
	}

	@Test
	/**checks content of line 0 - note no spaces
	 * 
	 */
	public void getTextLineStringTest() {
		List<TextLine> textLineList = TextStructurer.createTextLineList(Fixtures.PARA1_SVG);
		TextLine textLine0 = textLineList.get(0);
		String lineContent = textLine0.getLineString();
		Assert.assertEquals("text line", "dependentonreactiontimet,whichisafeatureofzero-order", lineContent);
	}

	/** inserts spaces into line 0 */
	@Test
	public void insertSpacesInTextLineStringTest() {
		List<TextLine> textLineList = TextStructurer.createTextLineList(Fixtures.PARA1_SVG);
		TextLine textLine0 = textLineList.get(0);
		textLine0.insertSpaces();
		String lineContent = textLine0.getLineString();
		Assert.assertEquals("text line", "dependent on reaction time t, which is a feature of zero-order", lineContent);
	}

	/** finds mean font sizes in each line (normally only one size per line)
	 */
	@Test
	public void getMeanFontSizeArrayTest() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA1_SVG);
		 RealArray meanFontSizeArray = textContainer.getMeanFontSizeArray();
		 Assert.assertNotNull(meanFontSizeArray);
		 Assert.assertTrue(meanFontSizeArray.equals(new RealArray(new double[] {9.465,9.465,9.465,9.465}), 0.001));
	}
	
	@Test
	/** gets all the lines in a suscripted para.
	 * does not detect spaces
	 * 
	 */
	public void getTextLinesParaSuscriptTest() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		textContainer.getLinesInIncreasingY();
		List<String> textLineContentList = textContainer.getTextLineContentList();
		StringTestBase.assertEquals("unspaced strings", 
		    new String[]{""+MINUS+"1"+MINUS+"1",
			"Therateconstantis0.61795mgLh.",
			"Thetemperaturedependenceoftherateconstantsisdescribed",
			"bytheArrheniusequationk=kexp("+MINUS+"E/RT),whereEisthe",
			"0aa",
			"activationenergy.Takingthenaturallogarithmofthisequa-",
			"tionandcombiningthekvaluesobtainedforthereactionat",
			""+WHITE_BULLET,
			"130and200CyieldstheresultsofkandEathighertem-",
			"0a",
			"peratures.Therefore,thecalculatedactivationenergy(E)is",
			"a",
			"5"+MINUS+"1",
			"1.11×10Jmol.",
			"Theaboveanalysisseemstoindicatethatindifferentreac-",
			"tiontemperaturerangesthesolvothermalreactioninthereverse",
			"micellesolutioniscontrolledbydifferentfactors.Thereactionat"
			},
			textLineContentList.toArray(new String[0]));
	}

	@Test
	@Ignore // until we have the spaces sorted
    /**
	 * It's still not quite right
	 * 
	 */
	public void getTextLinesParaSuscriptWithSpacesTest() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		textContainer.getLinesInIncreasingY();
		textContainer.insertSpaces();
		List<String> textLineContentList = textContainer.getTextLineContentList();
		StringTestBase.assertEquals("spaced strings", 
		    new String[]{""+MINUS+" "+"1"+" "+MINUS+" "+"1",
			"The rate constant is 0.61795mgL h .",
			"Thetemperaturedependenceoftherateconstantsisdescribed",
			"by theArrhenius equation k =k exp(− E /RT), where E is the",
			"0 a a",
			"activation energy. Taking the natural logarithm of this equa-",
			"tion and combining the k values obtained for the reaction at",
			""+WHITE_BULLET,
			"130 and 200 C yields the results of k and E at higher tem-",
			"0 a",
			"peratures. Therefore, the calculated activation energy (E ) is",
			"a",
			"5"+" "+MINUS+" "+"1",
			"1.11×10 Jmol .",
			"The above analysis seems to indicate that in different reac-",
			"tion temperature ranges the solvothermal reaction in the reverse",
			"micellesolutioniscontrolledbydifferentfactors.Thereactionat"
			},
			textLineContentList.toArray(new String[0]));
	}

	@Test
	@Ignore // not sure why now
	/**
	 * It's still not quite right
	 * 
	 */
	public void defaultSpaceTest() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		textContainer.getLinesInIncreasingY();
		textContainer.insertSpaces();
		List<String> textLineContentList = textContainer.getTextLineContentList();
		StringTestBase.assertEquals("spaced strings", 
		    new String[]{""+MINUS+" "+"1"+" "+MINUS+" "+"1",
			"The rate constant is 0.61795 mg L h .",
			"Thetemperaturedependenceoftherateconstantsisdescribed",
			"by theArrhenius equation k =k exp(− E /RT), where E is the",
			"0 a a",
			"activation energy. Taking the natural logarithm of this equa-",
			"tion and combining the k values obtained for the reaction at",
			""+WHITE_BULLET,
			"130 and 200 C yields the results of k and E at higher tem-",
			"0 a",
			"peratures. Therefore, the calculated activation energy (E ) is",
			"a",
			"5"+" "+MINUS+" "+"1",
			"1.11×10 Jmol .",
			"The above analysis seems to indicate that in different reac-",
			"tion temperature ranges the solvothermal reaction in the reverse",
			"micellesolutioniscontrolledbydifferentfactors.Thereactionat"
			},
		textLineContentList.toArray(new String[0]));

	}

	@Test
	/**
	 * It's still not quite right
	 * 
	 */
	public void minSpaceFactorTest() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		textContainer.getLinesInIncreasingY();
		textContainer.insertSpaces(0.05); // this seems to be minimum
//		textContainer.insertSpaces(0.12); // this seems to be maximum
		List<String> textLineContentList = textContainer.getTextLineContentList();
		StringTestBase.assertEquals("spaced strings", 
		    new String[]{""+MINUS+" "+"1"+" "+MINUS+" "+"1",
			"The rate constant is 0.61795 mg L h .",
			"The temperature dependence of the rate constants is described",
			"by the Arrhenius equation k = k exp(− E /RT ), where E is the",
			"0 a a",
			"activation energy. Taking the natural logarithm of this equa-",
			"tion and combining the k values obtained for the reaction at",
			""+WHITE_BULLET,
			"130 and 200 C yields the results of k and E at higher tem-",
			"0 a",
			"peratures. Therefore, the calculated activation energy (E ) is",
			"a",
			"5"+" "+MINUS+" "+"1",
			"1.11 × 10 J mol .",
			"The above analysis seems to indicate that in different reac-",
			"tion temperature ranges the solvothermal reaction in the reverse",
			"micelle solution is controlled by different factors. The reaction at"
			},
		textLineContentList.toArray(new String[0]));

	}

	@Test
	/**
	 * It's still not quite right
	 * 
	 */
	public void maxSpaceFactorTest() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		textContainer.getLinesInIncreasingY();
//		textContainer.insertSpaces(0.05); // this seems to be minimum
		textContainer.insertSpaces(0.12); // this seems to be maximum
		                              // but very critically balanced
		List<String> textLineContentList = textContainer.getTextLineContentList();
		StringTestBase.assertEquals("spaced strings", 
		    new String[]{""+MINUS+" "+"1"+" "+MINUS+" "+"1",
			"The rate constant is 0.61795 mg L h .",
			"The temperature dependence of the rate constants is described",
			"by the Arrhenius equation k = k exp(− E /RT ), where E is the",
			"0 a a",
			"activation energy. Taking the natural logarithm of this equa-",
			"tion and combining the k values obtained for the reaction at",
			""+WHITE_BULLET,
			"130 and 200 C yields the results of k and E at higher tem-",
			"0 a",
			"peratures. Therefore, the calculated activation energy (E ) is",
			"a",
			"5"+" "+MINUS+" "+"1",
			"1.11 × 10 J mol .",
			"The above analysis seems to indicate that in different reac-",
			"tion temperature ranges the solvothermal reaction in the reverse",
			"micelle solution is controlled by different factors. The reaction at"
			},
		textLineContentList.toArray(new String[0]));

	}

	
	@Test
	public void getMeanSpaceSeparationArrayParaSuscriptTest() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		textContainer.insertSpaces();
		List<TextLine> textLineList = textContainer.getLinesInIncreasingY();
		List<String> textLineContentList = textContainer.getTextLineContentList();
		List<Double> meanSpaceWidthList = textContainer.getActualWidthsOfSpaceCharactersList();
		Assert.assertNotNull(meanSpaceWidthList);
	}
	
	@Test
	public void getMeanFontSizeArrayParaSuscriptTest() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		RealArray meanFontSizeArray = textContainer.getMeanFontSizeArray();
		Assert.assertNotNull(meanFontSizeArray);
		Assert.assertTrue("fontSizes "+meanFontSizeArray, meanFontSizeArray.equals(
			 new RealArray(new double[] 
        {
					 7.074, // super0
					 9.465,	// l0
					 9.465,	// l1
					 9.465,	//l2
					 7.074, // sub2
					 9.465, // l3
					 9.465, // l4
					 7.074, // sup5
					 9.465, // l5
					 7.074, // sub5
					 9.465, // l6
					 7.074, // sub6
					 7.074,	// sup7
					 9.465, // l7
					 9.465, // l8
					 9.465, // l9
					 9.465  // l10
					 }), 0.001));
	}

	@Test
	@Ignore // FIXME
	/** not sure what this does
	 * 
	 */
	public void getModalExcessWidthArrayTest() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		textContainer.insertSpaces();
		RealArray modalExcessWidthArray = textContainer.getModalExcessWidthArray();
		Assert.assertNotNull(modalExcessWidthArray);
		Assert.assertTrue("fontSizes "+modalExcessWidthArray, modalExcessWidthArray.equals(
			 new RealArray(new double[] 
        {7.074,9.465,9.465,9.465,7.074,9.465,9.465,7.074,9.465,7.074,9.465,7.074,7.074,9.465,9.465,9.465,9.465}), 0.001));
	}
	
	@Test
	/** test contains normal and superscripts so two fontsizes
	 * 
	 */
	public void getFontSizeSetTest() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		Set<SvgPlusCoordinate> fontSizeSet = textContainer.getFontSizeSet();
		Assert.assertEquals("font sizes", 2, fontSizeSet.size());
		Assert.assertTrue("font large", fontSizeSet.contains(new SvgPlusCoordinate(9.465)));
		Assert.assertTrue("font small", fontSizeSet.contains(new SvgPlusCoordinate(7.07)));
	}

	@Test
	/** 
	 * indexes lines by font sizes
	 */
	public void getTextLinesByFontSizeTest() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		Multimap<SvgPlusCoordinate, TextLine> textLineListByFontSize = textContainer.getTextLineListByFontSize();
		Assert.assertEquals("font sizes", 17, textLineListByFontSize.size());
		List<TextLine> largeLines = (List<TextLine>) textLineListByFontSize.get(new SvgPlusCoordinate(9.465));
		Assert.assertEquals("font large", 11, largeLines.size());
		Assert.assertEquals("font small", 6, ((List<TextLine>) textLineListByFontSize.get(new SvgPlusCoordinate(7.07))).size());
	}

	@Test
	/** 
	 * retrieve by font size
	 */
	public void getTextLineSetByFontSizeTest() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		TextLineSet textLineSetByFontSize = textContainer.getTextLineSetByFontSize(9.465);
		Assert.assertEquals("textLineSet", 11, textLineSetByFontSize.size());
	}

	@Test
	/** 
	 * get Mainlines
	 */
	public void getLargestFontTest() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		SvgPlusCoordinate maxSize = textContainer.getLargestFontSize();
		Assert.assertEquals("largest font", 9.47, maxSize.getDouble(), 0.001);
	}

	@Test
	/** 
	 * get Mainlines
	 */
	public void getLinesWithLargestFontTest() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<TextLine> largestLineList = textContainer.getLinesWithLargestFont();
		Assert.assertEquals("largest", 11, largestLineList.size());
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testAnalyzeSuscripts0() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<TextLine> largestLineList = textContainer.getLinesWithLargestFont();
		TextLine largeLine = largestLineList.get(0);
		TextLine superscript = largeLine.getSuperscript();
		Assert.assertNotNull(superscript);
		Assert.assertEquals("sup", "−1−1", superscript.getLineString());
		TextLine subscript = largeLine.getSubscript();
		Assert.assertNull(subscript);
	}


	@Test
	/** 
	 * suscripts
	 */
	public void testAnalyzeSuscripts1() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<TextLine> largestLineList = textContainer.getLinesWithLargestFont();
		TextLine largeLine = largestLineList.get(1);
		TextLine superscript = largeLine.getSuperscript();
		Assert.assertNull(superscript);
		TextLine subscript = largeLine.getSubscript();
		Assert.assertNull(subscript);
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testAnalyzeSuscripts2() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<TextLine> largestLineList = textContainer.getLinesWithLargestFont();
		TextLine largeLine = largestLineList.get(2);
		TextLine superscript = largeLine.getSuperscript();
		Assert.assertNull(superscript);
		TextLine subscript = largeLine.getSubscript();
		Assert.assertNotNull(subscript);
		Assert.assertEquals("sub", "0aa", subscript.getLineString());
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testAnalyzeSuscripts5() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<TextLine> largestLineList = textContainer.getLinesWithLargestFont();
		TextLine largeLine = largestLineList.get(5);
		TextLine superscript = largeLine.getSuperscript();
		Assert.assertNotNull(superscript);
		String s = superscript.getLineString();
		// this is a WHITE BULLET (should be a degree sign)
		Assert.assertEquals("sup"+(int)s.charAt(0), ""+WHITE_BULLET, superscript.getLineString());
		TextLine subscript = largeLine.getSubscript();
		Assert.assertNotNull(subscript);
		Assert.assertEquals("sub", "0a", subscript.getLineString());
	}

	@Test
	/** 
	 * suscripts - mainly debugging routine 
	 * does not produce html
	 */
	public void testCreateSuscriptLine0() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		TextLine largeLine = textContainer.getLinesWithLargestFont().get(0);
		List<SVGText> largeLineSVG = largeLine.createSuscriptString();
		printLine(largeLineSVG);
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptTextLines0() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		TextLine largeLine = textContainer.getLinesWithLargestFont().get(0);
		List<TextLine> suscriptLines = largeLine.createSuscriptTextLineList();
		printTextLines(suscriptLines);
	}
	
	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptTextLines1() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<TextLine> suscriptLines = textContainer.getLinesWithLargestFont().get(1).createSuscriptTextLineList();
		printTextLines(suscriptLines);
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptTextLines2() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<TextLine> suscriptLines = textContainer.getLinesWithLargestFont().get(2).createSuscriptTextLineList();
		printTextLines(suscriptLines);
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptTextLines5() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<TextLine> suscriptLines = textContainer.getLinesWithLargestFont().get(5).createSuscriptTextLineList();
		printTextLines(suscriptLines);
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptTextLines7() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<TextLine> suscriptLines = textContainer.getLinesWithLargestFont().get(7).createSuscriptTextLineList();
		printTextLines(suscriptLines);
	}
	
	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptWordTextLines0() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		TextLine largeLine = textContainer.getLinesWithLargestFont().get(0);
		List<TextLine> suscriptLines = largeLine.createSuscriptTextLineList();
		for (TextLine textLine : suscriptLines) {
			textLine.insertSpaces();
		}
	}
	
	@Test
	/** 
	 * superscripts
	 */
	public void testCreateHTML0() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		TextLine largeLine = textContainer.getLinesWithLargestFont().get(0);
		HtmlElement p = largeLine.createHtmlLine();
		Element ref = CMLUtil.parseXML(
				"<p xmlns='http://www.w3.org/1999/xhtml'>" +
"<span style='font-size:9.465px;font-family:TimesNewRoman;'>The rate constant is 0.61795 mg L</span>" +
"<sup>" +
"<span style='font-size:7.074px;color:red;font-family:MTSYN;'>− </span>" +
"<span style='font-size:7.074px;font-family:TimesNewRoman;'>1</span>" +
"</sup>" +
"<span style='font-size:9.465px;font-family:TimesNewRoman;'>h</span>" +
"<sup>" +
"<span style='font-size:7.074px;color:red;font-family:MTSYN;'>− </span>" +
"<span style='font-size:7.074px;font-family:TimesNewRoman;'>1</span>" +
"</sup>" +
"<span style='font-size:9.465px;font-family:TimesNewRoman;'>.</span>" +
"</p>" +
"");
		JumboTestUtils.assertEqualsIncludingFloat("ref ", ref, p, true, 0.001);
	}
	

	@Test
	/** 
	 * superscripts
	 */
	public void testCreateHTML1() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		HtmlElement p = textContainer.getLinesWithLargestFont().get(1).createHtmlLine();
		Element ref = CMLUtil.parseXML(
				"<p xmlns='http://www.w3.org/1999/xhtml'>" +
				"<span style='font-size:9.465px;font-family:TimesNewRoman;'>The temperature dependence of the rate constants is described</span>"+
			"</p>" +
			"");
		JumboTestUtils.assertEqualsIncludingFloat("ref ", ref, p, true, 0.001);
	}
	
	@Test
	/** 
	 * superscripts
	 */
	public void testCreateHTML2() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		HtmlElement p = textContainer.getLinesWithLargestFont().get(2).createHtmlLine();
		Element ref = CMLUtil.parseXML(""+
		"<p xmlns='http://www.w3.org/1999/xhtml'>" +
		"<span style='font-size:9.465px;font-family:TimesNewRoman;'>by the Arrhenius equation </span>" +
		"<span style='font-size:9.465px;font-style:italic;font-family:TimesNewRoman;'>k</span>" +
		"<span style='font-size:9.465px;font-family:TimesNewRoman;' />" +
		"<span style='font-size:9.465px;color:red;font-family:MTSYN;'>=</span>" +
		"<span style='font-size:9.465px;font-family:TimesNewRoman;' />" +
		"<span style='font-size:9.465px;font-style:italic;font-family:TimesNewRoman;'>k</span>" +
		"<sub>" +
		"<span style='font-size:7.074px;font-family:TimesNewRoman;'>0</span>" +
		"</sub>" +
		"<span style='font-size:9.465px;font-family:Times-Roman;'>exp</span>" +
		"<span style='font-size:9.465px;font-family:TimesNewRoman;'>(</span>" +
		"<span style='font-size:9.465px;color:red;font-family:MTSYN;'>"+MINUS+"</span>" +
		"<span style='font-size:9.465px;font-family:Times-Roman;' />" +
		"<span style='font-size:9.465px;font-style:italic;font-family:TimesNewRoman;'>E</span>" +
		"<sub>" +
		"<span style='font-size:7.074px;font-family:TimesNewRoman;'>a</span>" +
		"</sub>" +
		"<span style='font-size:9.465px;font-family:TimesNewRoman;'>/</span>" +
		"<span style='font-size:9.465px;font-style:italic;font-family:TimesNewRoman;'>RT</span>" +
		"<span style='font-size:9.465px;font-family:TimesNewRoman;'> ), where </span>" +
		"<span style='font-size:9.465px;font-style:italic;font-family:TimesNewRoman;'>E</span>" +
		"<sub>" +
		"<span style='font-size:7.074px;font-family:TimesNewRoman;'>a</span>" +
		"</sub>" +
		"<span style='font-size:9.465px;font-family:TimesNewRoman;'>is the</span>" +
		"</p>" +
		"");

		JumboTestUtils.assertEqualsIncludingFloat("ref ", ref, p, true, 0.001);
	}
	
	@Test
	/** 
	 * superscripts
	 */
	public void testCreateHTML5() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		HtmlElement p = textContainer.getLinesWithLargestFont().get(5).createHtmlLine();
		Element ref = CMLUtil.parseXML(
				"<p xmlns='http://www.w3.org/1999/xhtml'>" +
				"<span style='font-size:9.465px;font-family:TimesNewRoman;'>130 and 200</span>" +
				"<sup>" +
				"<span style='font-size:7.074px;color:red;font-family:MTSYN;'>"+WHITE_BULLET+"</span>" +
				"</sup>" +
				"<span style='font-size:9.465px;font-family:TimesNewRoman;'>C yields the results of </span>" +
				"<span style='font-size:9.465px;font-style:italic;font-family:TimesNewRoman;'>k</span>" +
				"<sub>" +
				"<span style='font-size:7.074px;font-family:TimesNewRoman;'>0</span>" +
				"</sub>" +
				"<span style='font-size:9.465px;font-family:TimesNewRoman;'>and </span>" +
				"<span style='font-size:9.465px;font-style:italic;font-family:TimesNewRoman;'>E</span>" +
				"<sub>" +
				"<span style='font-size:7.074px;font-family:TimesNewRoman;'>a</span>" +
				"</sub>" +
				"<span style='font-size:9.465px;font-family:TimesNewRoman;'>at higher tem-</span>" +
				"</p>" +
"");
		JumboTestUtils.assertEqualsIncludingFloat("ref ", ref, p, true, 0.001);
	}
	
	@Test
	/** 
	 * superscripts
	 */
	public void testCreateHTMLRawDiv() throws Exception {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		HtmlElement div = textContainer.getTextAnalyzer().createHtmlRawDiv();
		CMLUtil.debug(div, new FileOutputStream("target/div.html"), 0);
	}
	
	@Test
	/** 
	 * superscripts
	 */
	public void testCreateHTMLDivWithParas() throws Exception {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		HtmlElement div = textContainer.getTextAnalyzer().createHtmlDivWithParas();
		CMLUtil.debug(div, new FileOutputStream("target/divParas0.html"), 0);
	}
	
	@Test
	/** 
	 * superscripts
	 */
	public void testCreateHTMLDivWithParasNew() throws Exception {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<ScriptLine> textGroupList = textContainer.getScriptedLineList();
		HtmlElement div = textContainer.createHtmlElementWithParas(textGroupList);
		CMLUtil.debug(div, new FileOutputStream("target/divParasNew.html"), 0);
	}
	
	@Test
	/** 
	 * bold
	 */
	public void testCreateHTMLDivWithParasBold() throws Exception {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PAGE3RESULTS_SVG);
		List<ScriptLine> textGroupList = textContainer.getScriptedLineList();
		HtmlElement div = textContainer.createHtmlElementWithParas(textGroupList);
		CMLUtil.debug(div, new FileOutputStream("target/divBold.html"), 0);
	}
	

	private void printTextLines(List<TextLine> suscriptLines) {
		for (TextLine textLine : suscriptLines){
			LOG.trace(""+textLine.getSuscript()+" ");
			printLine(textLine.getSVGTextCharacters());
		}
		System.out.println();
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptLine4() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<TextLine> largestLineList = textContainer.getLinesWithLargestFont();
		TextLine largeLine = largestLineList.get(4);
		List<SVGText> largeLineSVG = largeLine.createSuscriptString();
		printLine(largeLineSVG);
		System.out.println();
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptLine5() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<TextLine> largestLineList = textContainer.getLinesWithLargestFont();
		TextLine largeLine = largestLineList.get(5);
		List<SVGText> largeLineSVG = largeLine.createSuscriptString();
		printLine(largeLineSVG);
		System.out.println();
	}

	private void printLine(List<SVGText> largeLineSVG) {
//		System.out.print("LINE: ");
//		for (SVGText large : largeLineSVG) {
//			System.out.print(" "+large.getValue());
//		}
	}


	@Test
	/** 
	 * get serial of text
	 */
	public void testgetSerial() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<TextLine> largestLineList = textContainer.getLinesWithLargestFont();
		Assert.assertEquals("super", 1, (int) textContainer.getSerialNumber(largestLineList.get(0)));
	}



	@Test
	/** 
	 * get Interline separation
	 */
	public void getInterTextLineSeparationSetTest() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		Multiset<Double> separationSet = textContainer.createSeparationSet(2);
		Assert.assertEquals("separationSet", 16, separationSet.size());
		Assert.assertEquals("separationSet", 6, separationSet.entrySet().size());
	}

	@Test
	/** 
	 * get Interline separation
	 */
	public void getMainInterTextLineSeparationTest() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		Double sep = textContainer.getMainInterTextLineSeparation(2);
		Assert.assertEquals("sep ", 10.96, sep, 0.001);
	}

	@Test
	/** 
	 * get Interline separation
	 */
	public void getInterTextLineSeparation() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		RealArray interTextLineSeparationArray = textContainer.getInterTextLineSeparationArray();
		Assert.assertNotNull(interTextLineSeparationArray);
		RealArray ref = new RealArray(new double[]{
				3.436,10.959,10.959,1.419,9.54,10.959,7.523,3.436,1.419,9.539,1.42,6.104,3.435,10.959,10.959,10.959});
		Assert.assertTrue("interline separation "+interTextLineSeparationArray, interTextLineSeparationArray.equals(ref, 0.001));
	}

	@Test
	/** 
	 * get merged boxes
	 */
	public void testgetDiscreteBoxes() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<Real2Range> discreteBoxes  = textContainer.getTextLineChunkBoxesAndInitialiScriptLineList();
		Assert.assertNotNull(discreteBoxes);
		// lines 7subscript and 8superscrip overlap 
		Assert.assertEquals("boxes", 10, discreteBoxes.size());
	}

	@Test
	/** 
	 * get lines in merged boxes
	 */
	public void testLinesInDiscreteBoxes() {
		int[] count = {2, 1, 2, 1, 1, 3, 4, 1, 1, 1};
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<ScriptLine> textLineChunkList  = textContainer.getInitialScriptLineList();
		Assert.assertNotNull(textLineChunkList);
		Assert.assertEquals("boxes", 10, textLineChunkList.size());
		int i = 0;
		for (ScriptLine textLineChunk : textLineChunkList) {
			Assert.assertEquals("box"+i, count[i], textLineChunk.size());
			LOG.trace(">>");
			for (TextLine textLine: textLineChunk) {
				LOG.trace(textLine);
			}
			LOG.trace("<<");
			i++;
		}
	}

	@Test
	/** 
	 * get lines in merged boxes
	 */
	public void testGetInitialTextLineChunkList() {
		int[] count = {2, 1, 2, 1, 1, 3, 4, 1, 1, 1};
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<ScriptLine> textLineChunkList  = textContainer.getInitialScriptLineList();
		Assert.assertNotNull(textLineChunkList);
		Assert.assertEquals("boxes", 10, textLineChunkList.size());
	}


	@Test
	/** 
	 * get coordinates of lines
	 */
	public void getTextLineCoordinateArray() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		RealArray textLineCoordinateArray = textContainer.getTextLineCoordinateArray();
		Assert.assertNotNull(textLineCoordinateArray);
		RealArray ref = new RealArray(new double[] 
			        { 343.872,347.308,358.267,369.226,370.645,380.185,391.144,398.667,402.103,403.522,413.061,414.481,420.585,424.02,434.979,445.938,456.897});
		Assert.assertTrue("textline coordinates "+textLineCoordinateArray, textLineCoordinateArray.equals(ref, 0.001));
	}

	@Test
	/**
	 * 
	 */
	public void testGetCommonestFontSizeTextLineList() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<TextLine> isCommonestFontSize = textContainer.getCommonestFontSizeTextLineList();
		Assert.assertEquals("commonestFontSize", 11, isCommonestFontSize.size());
	}
	
	@Test
	/** splits textGroups into lines with sub/superscripts
	 * 
	 */
	public void testGetScriptedLineGroupList() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<ScriptLine> textLineChunkList  = textContainer.getInitialScriptLineList();
		Assert.assertEquals("TextLines ", 10, textLineChunkList.size());
		List<ScriptLine> separated = textContainer.getScriptedLineList();
		Assert.assertEquals("split", 11, separated.size());
		for (ScriptLine group : separated) {
			LOG.trace(group);
		}
	}
	
	@Test
	public void testCreateTextListLines0() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		ScriptLine group0 = textContainer.getScriptedLineList().get(0);
		Assert.assertEquals("group0", 2, group0.size());
		List<TextLine> textLineList = group0.createSuscriptTextLineList();
		Assert.assertEquals("group0", 5, textLineList.size());
	}
	
	@Test
	public void testCreateTextListLinesAll() {
		int[] groupSize = new int[]{5,1,7,1,1,7,3,5,1,1,1};
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<ScriptLine> groupList = textContainer.getScriptedLineList();
		Assert.assertEquals("groups", 11, groupList.size());
		int i = 0;
		for (ScriptLine group : groupList) {
			List<TextLine> textLineList = group.createSuscriptTextLineList();
			Assert.assertEquals("group"+i, groupSize[i], textLineList.size());
			i++;
		}
	}
	
	@Test
	public void testCreateTextListHtml0() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		ScriptLine group0 = textContainer.getScriptedLineList().get(0);
		HtmlElement textLineHtml = group0.createHtml();
		Assert.assertEquals("group0", 
				"<p xmlns=\"http://www.w3.org/1999/xhtml\">" +
				"<span style=\"font-size:9.465px;font-family:TimesNewRoman;\">The rate constant is 0.61795 mg L</span>" +
				"<sup><span style=\"font-size:7.074px;color:red;font-family:MTSYN;\">"+MINUS+" </span>" +
				"<span style=\"font-size:7.074px;font-family:TimesNewRoman;\">1</span></sup>" +
				"<span style=\"font-size:9.465px;font-family:TimesNewRoman;\">h</span>" +
				"<sup><span style=\"font-size:7.074px;color:red;font-family:MTSYN;\">"+MINUS+" </span><span style=\"font-size:7.074px;font-family:TimesNewRoman;\">1</span></sup>" +
				"<span style=\"font-size:9.465px;font-family:TimesNewRoman;\">.</span></p>",
				textLineHtml.toXML());
	}
	
	@Test
	public void testCreateTextListHtmlDiv() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
		List<ScriptLine> textLineGroupList = textContainer.getScriptedLineList();
		HtmlElement divElement = TextStructurer.createHtmlDiv(textLineGroupList);
		Element ref = CMLUtil.parseQuietlyToDocument(new File("src/test/resources/org/xmlcml/svg2xml/analyzer/textLineGroup0.html")).getRootElement();
		JumboTestUtils.assertEqualsCanonically("html", ref, divElement, true);
	}
	

	/** FIXTURES */

	/** attempts to test for Unicode
	 * doesn't seem to work
	 */
	@Test
	@Ignore
	public void unicodeTestNotRelevant() {
		Pattern pattern = Pattern.compile("\\p{Cn}");
		LOG.trace("\\u0020 "+pattern.matcher("\u0020").matches());
		LOG.trace("A "+pattern.matcher("A").matches());
		LOG.trace("\\uf8f8 "+pattern.matcher("\uf8f8").matches());
	}
	
	

}
