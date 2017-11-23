package org.xmlcml.svg2xml.table;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.graphics.svg.SVGTitle;
import org.xmlcml.svg2xml.util.GraphPlot;

/** manages the table header, including trying to sort out the column spanning
 * 
 * @author pm286
 *
 */
public class TableTitleSection extends TableSection {
	static final String TITLE_TITLE = "title.title";
	static final Logger LOG = Logger.getLogger(TableTitleSection.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public TableTitleSection() {
		super(TableSectionType.TITLE);
	}
	
	public TableTitleSection(TableSection tableSection) {
		super(tableSection);
	}
	
	public SVGElement createMarkedContent(
			SVGElement svgChunk,
			String[] colors,
			double[] opacity) {
			SVGElement g = createBoxAndShiftToOrigin(svgChunk, colors, opacity);
			svgChunk.appendChild(g);
			return svgChunk;
	}
	
	private SVGElement createBoxAndShiftToOrigin(SVGElement svgChunk, String[] colors, double[] opacity) {
		SVGG g = new SVGG();
		g.setClassName(TITLE_TITLE);
		if (boundingBox == null) {
			LOG.trace("no bounding box");
		} else {
			String title = "TITLE: "+this.getFontInfo()+" //" +this.getStringValue();
			SVGTitle svgTitle = new SVGTitle(title);
			SVGShape plotBox = GraphPlot.createBoxWithFillOpacity(boundingBox, colors[0], opacity[0]);
			plotBox.appendChild(svgTitle);
			g.appendChild(plotBox);
			TableContentCreator.shiftToOrigin(svgChunk, g);
		}
		return g;
	}
}
