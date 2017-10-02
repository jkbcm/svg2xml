package org.xmlcml.svg2xml.page;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.Univariate;
import org.xmlcml.graphics.svg.GraphicsElement;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.cache.ComponentCache;
import org.xmlcml.graphics.svg.cache.TextChunkCache;
import org.xmlcml.graphics.svg.rule.horizontal.HorizontalElementNew;
import org.xmlcml.graphics.svg.rule.horizontal.HorizontalRuleNew;
import org.xmlcml.graphics.svg.text.build.PhraseChunk;
import org.xmlcml.graphics.svg.text.build.TextChunk;
import org.xmlcml.graphics.svg.text.build.TextChunkList;
import org.xmlcml.graphics.svg.text.structure.TextStructurer;
import org.xmlcml.svg2xml.table.TableGrid;
import org.xmlcml.svg2xml.table.TableStructurer;
import org.xmlcml.xml.XMLUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/** a new approach (2017) to analyzing the structure of pages.
 * uses PhraseList extents and HorizontalRulers to estimate widths
 * 
 * Now (2017) uses Caches to process the SVG input in TableContentCreator.CreateContent()
 * 
 * @author pm286
 *
 */
public class PageLayoutAnalyzer {

	private static final char CHAR_P = 'P';
	private static final char CHAR_L = 'L';
	private static final String LP = "LP";
	private static final String LP_1 = LP+"{1}";
	private static final String X = "X";
	private static final String T = "T";

	private static final Logger LOG = Logger.getLogger(PageLayoutAnalyzer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static Pattern TABLE_N = Pattern.compile("T[Aa][Bb][Ll][Ee]\\s+(\\d+)\\s+(\\(cont(inued)?\\.?\\))?(.{0,500})");


	protected TextStructurer textStructurer;
	protected TextChunk textChunk;
	protected TableStructurer tableStructurer;
	protected List<HorizontalRuleNew> horizontalRuleList;
	protected List<HorizontalElementNew> horizontalList;
	
	private Multiset<IntRange> xRangeSet = HashMultiset.create();
	private Multiset<Integer> xRangeStartSet;
	private Multiset<Integer> xRangeEndSet;
	private boolean includeRulers;
	private boolean includePhrases;
	private int xRangeRangeMin; // to exclude ticks on diagrams
	private File inputFile;
	private boolean rotatable = false;
	private boolean omitWhitespace = true;
	protected SVGElement svgChunk;

	public PageLayoutAnalyzer() {
		setDefaults();
	}

	private void setDefaults() {
		xRangeRangeMin = 50;
		ensureXRangeSets();
		omitWhitespace = true;
	}

	private void ensureXRangeSets() {
		xRangeSet = HashMultiset.create();
		xRangeStartSet = HashMultiset.create();
		xRangeEndSet = HashMultiset.create();
	}
	
	public void createContent(File inputFile) {
		this.inputFile = inputFile;
		svgChunk = SVGElement.readAndCreateSVG(inputFile);
		createContent(svgChunk);
	}

	/** uses the SVG classes and methods
	 * 
	 * @param svgElement
	 */
	public void createContent(SVGElement svgElement) {
		ComponentCache componentCache = new ComponentCache();
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		TextChunkCache textChunkCache = componentCache.getOrCreateTextChunkCache();
		TextStructurer textStructurer = textChunkCache.getOrCreateTextStructurer();
		TextChunkList textChunkList = textChunkCache.getOrCreateTextChunkList();
		LOG.debug("components: "+componentCache.toString());
		// should probably move TextStructure to TextChunkCache
		textStructurer = TextStructurer.createTextStructurerWithSortedLines(svgElement);
		SVGElement inputSVGChunk = textStructurer.getSVGChunk();
		cleanChunk(inputSVGChunk);
		if (rotatable  && textStructurer.hasAntiClockwiseCharacters()) {
			throw new RuntimeException("refactored rot90");
//			inputSVGChunk = rotateClockwise(textStructurer);
//			TextStructurer textStructurer1 = TextStructurer.createTextStructurerWithSortedLines(inputSVGChunk);
//			textStructurer = textStructurer1;
		}

		textChunk = textStructurer.getTextChunkList().getLastTextChunk();
//		LOG.trace(">pll>"+phraseListList.size()+" ... "+phraseListList.toXML());
		textStructurer.condenseSuscripts();
//		phraseListList.format(3);
		tableStructurer = PageLayoutAnalyzer.createTableStructurer(textStructurer); // this deletes outer rect
		TableGrid tableGrid = tableStructurer.createGrid();
			
		if (tableGrid == null) {
			createOrderedHorizontalList();
			LOG.trace("hlist: " + PageLayoutAnalyzer.createSig(horizontalList));
		}
		return;
	}
	
	/** rotates text clockwise to create new tables.
	 * probable obsolete here.
	 * static because of refactoring
	 * 
	 * @param textStructurer
	 * @return
	 */
	public static SVGElement rotateClockwise(TextStructurer textStructurer) {
		SVGG rotatedVerticalText = textStructurer.createChunkFromVerticalText(new Angle(-1.0 * Math.PI / 2));
		TableStructurer tableStructurer = TableStructurer.createTableStructurer(textStructurer);
		SVGElement chunk = textStructurer.getSVGChunk();
		Angle angle = new Angle(-1.0 * Math.PI / 2);
		List<SVGShape> shapeList = tableStructurer.getOrCreateShapeList();
		SVGElement.rotateAndAlsoUpdateTransforms(shapeList, chunk.getCentreForClockwise90Rotation(), angle);
		chunk.removeChildren();
		XMLUtil.transferChildren(rotatedVerticalText, chunk);
		for (SVGShape shape : shapeList) {
			shape.detach();
			chunk.appendChild(shape);
		}
		return chunk;
	}


	private static TableStructurer createTableStructurer(TextStructurer textStructurer) {
		TextChunkList textChunkList = textStructurer.getOrCreateTextChunkListFromWords();
		TableStructurer tableStructurer = new TableStructurer(textChunkList.getLastTextChunk());
		tableStructurer.setTextStructurer(textStructurer);
		tableStructurer.analyzeShapeList();
		return tableStructurer;
	}

	public static String createSig(List<HorizontalElementNew> horizontalList) {
		StringBuilder sb;
		String sig = createLPList(horizontalList);
		LOG.trace(">>"+sig);
		String lpc = createLPCountList(sig);
		LOG.trace(">>>"+lpc);
		String lpccond = contractLP1(lpc);
		LOG.trace(">>>>"+lpccond);
		return lpccond;
	}

	private static String createLPList(List<HorizontalElementNew> horizontalList) {
		StringBuilder sb = new StringBuilder();
		for (HorizontalElementNew helem :horizontalList) {
			if (helem instanceof HorizontalRuleNew) {
				sb.append("L");
			} else if (helem instanceof PhraseChunk) {
				sb.append("P");
			} else {
				sb.append(helem.getClass().getSimpleName());
			}
		}
		String sig = sb.toString();
		return sig;
	}

	private static String createLPCountList(String sig) {
		StringBuilder sb;
		sb = new StringBuilder();
		int pcount = 0;
		for (int i = 0; i < sig.length(); i++) {
			char c = sig.charAt(i);
			if (c == CHAR_P) {
				pcount++;
			} else if (c == CHAR_L) {
				if (pcount > 0) {
					sb.append(CHAR_P+"{"+pcount+"}");
				}
				pcount = 0;
				sb.append("L");
			}
		}
		if (pcount > 0) {
			sb.append(CHAR_P+"{"+pcount+"}");
		}
		return sb.toString();
	}

	private static String contractLP1(String sig) {
		String cond = sig.replace(LP_1, X);
		StringBuilder sb = new StringBuilder();
		int xcount = 0;
		int i = 0;
		while (i < cond.length()) {
			String s = cond.substring(i, i+1);
			if (X.equals(s)) {
				if (xcount == 1) {
					sb.append(X);
				}
				xcount++;
			} else {
				if (xcount > 1) {
					sb.append(T+"{"+(xcount - 1) +"}");
					xcount = 0;
				}
				sb.append(s);
			}
			i++;
		}
		if (xcount > 0) {
			sb.append(T+"{"+xcount+"}");
		}
		String s = sb.toString();
		s = s.replace(X, LP);
		s = s.replace(T+"{1}", LP);
		return s;
	}


	private void cleanChunk(GraphicsElement chunk) {
		if (omitWhitespace) {
			detachWhitespaceTexts(chunk);
		}
	}

	private void detachWhitespaceTexts(GraphicsElement chunk) {
		List<SVGText> spaceList = SVGText.extractSelfAndDescendantTexts(chunk);
		for (SVGText text : spaceList) {
			String textS = text.getText();
			if (textS == null || textS.trim().length() == 0) {
				text.detach();
			}
		}
	}

	private List<HorizontalElementNew> createOrderedHorizontalList() {
		Stack<PhraseChunk> phraseListStack = new Stack<PhraseChunk>();
		for (PhraseChunk phraseList : textChunk) {
			phraseListStack.push(phraseList);
		}
		Stack<HorizontalRuleNew> horizontalRulerListStack = new Stack<HorizontalRuleNew>();
		horizontalRuleList = tableStructurer.getHorizontalRulerList(true, 1.0);
		for (HorizontalRuleNew ruler : horizontalRuleList) {
			horizontalRulerListStack.push(ruler);
		}
		addStacksToHorizontalListInYOrder(phraseListStack, horizontalRulerListStack);
		return horizontalList;
	}

	private void addStacksToHorizontalListInYOrder(Stack<PhraseChunk> phraseListStack, Stack<HorizontalRuleNew> horizontalRulerListStack) {
		horizontalList = new ArrayList<HorizontalElementNew>();
		PhraseChunk currentPhraseList = null;
		HorizontalRuleNew currentRuler = null;
		while (!phraseListStack.isEmpty() || !horizontalRulerListStack.isEmpty() ||
				currentPhraseList != null || currentRuler != null) {
			if (!phraseListStack.isEmpty() && currentPhraseList == null) {
				currentPhraseList = phraseListStack.pop();
			}
			if (!horizontalRulerListStack.isEmpty() && currentRuler == null) {
				currentRuler = horizontalRulerListStack.pop();
			}
			if (currentRuler != null && currentPhraseList != null) {
				Double rulerY = currentRuler.getY();
				Double phraseListY = currentPhraseList.getXY().getY();
				if (rulerY < phraseListY) {
					addPhraseList(currentPhraseList);
					currentPhraseList = null;
				} else {
					addRuler(currentRuler);
					currentRuler = null;
				}
			} else if (currentPhraseList != null) {
				addPhraseList(currentPhraseList);
				currentPhraseList = null;
			} else if (currentRuler != null) {
				addRuler(currentRuler);
				currentRuler = null;
			} else {
				LOG.trace("stacks empty");
			}
		}
		Collections.reverse(horizontalList);
		for (HorizontalElementNew horizontalElement : horizontalList) {
			LOG.trace("============"+horizontalElement.getClass()+"\n"+horizontalElement.toString());
		}
	}

	private void addRuler(HorizontalRuleNew currentRuler) {
		horizontalList.add((HorizontalElementNew)currentRuler);
		LOG.trace("phrase: "+currentRuler.getStringValue()+"/"+currentRuler.getY());
	}

	private void addPhraseList(PhraseChunk currentPhraseList) {
		horizontalList.add(currentPhraseList);
		LOG.trace("phrase: "+currentPhraseList.getStringValue()+"/"+currentPhraseList.getY());
	}

	public List<HorizontalElementNew> getHorizontalList() {
		return horizontalList;
	}


	public void analyzeXRangeExtents(File inputFile) {
		createContent(inputFile);
		List<HorizontalElementNew> horizontalElementList = getHorizontalList();
		
		for (HorizontalElementNew horizontalElement : horizontalElementList) {
			IntRange xRange = new IntRange(((SVGElement)horizontalElement).getBoundingBox().getXRange().format(0));
			int round = 1;
			xRangeStartSet.add(xRange.getMin() / round * round);
			xRangeEndSet.add(xRange.getMax() / round * round);
			if (includeRulers && horizontalElement instanceof HorizontalRuleNew ||
				includePhrases && horizontalElement instanceof PhraseChunk) {
				if (xRange.getRange() >= xRangeRangeMin) {
					xRangeSet.add(xRange);
				}
			}
		}
	}

	public List<HorizontalRuleNew> getHorizontalRulerList() {
		return horizontalRuleList;
	}


	public void setIncludeRulers(boolean b) {
		this.includeRulers = b;
	}

	public void setIncludePhrases(boolean b) {
		this.includePhrases = b;
	}

	public Multiset<IntRange> getXRangeSet() {
		return xRangeSet;
	}

	public Multiset<Integer> getXRangeEndSet() {
		return xRangeEndSet;
	}

	public Multiset<Integer> getXRangeStartSet() {
		return xRangeStartSet;
	}

	public void setXRangeRangeMin(int rangeMin) {
		this.xRangeRangeMin = rangeMin;
	}

	public RealArray getXRangeStartArray() {
		return getRealArray(xRangeStartSet);
	}
	
	public Univariate getXStartUnivariate() {
		return new Univariate(getXRangeStartArray());
	}

	public RealArray getXRangeEndArray() {
		return getRealArray(xRangeEndSet);
	}

	public Univariate getXEndUnivariate() {
		return new Univariate(getXRangeEndArray());
	}


	private RealArray getRealArray(Multiset<Integer> xSet) {
		RealArray xArray = new RealArray();
		for (Multiset.Entry<Integer> entry : xSet.entrySet()) {
			for (int i = 0; i < entry.getCount(); i++) {
				xArray.addElement((double)entry.getElement());
			}
		}
		return xArray;
	}

	public TextStructurer getTextStructurer() {
		return textStructurer;
	}

	public TableStructurer getTableStructurer() {
		return tableStructurer;
	}
	
	public boolean isRotatable() {
		return rotatable;
	}

	public void setRotatable(boolean rotatable) {
		this.rotatable = rotatable;
	}



}
