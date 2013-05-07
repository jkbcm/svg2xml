package org.xmlcml.svg2xml.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.RealRangeArray;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlTr;

public class TableRow extends AbstractTableChunk {

	private final static Logger LOG = Logger.getLogger(TableRow.class);
	private List<TableCell> cellList;
	
	public TableRow(RealRangeArray horizontalMask, RealRangeArray verticalMask) {
		super(horizontalMask, null);
	}
	
	public TableRow() {
		super();
	}

	public List<TableCell> createCells() {
		setCellList(new ArrayList<TableCell>());
		return getCellList();
	}

	public void createAndAnalyzeCells(RealRangeArray horizontalMask) {
		createCells();
		for (RealRange range : horizontalMask) {
			TableCell cell = new TableCell();
			getCellList().add(cell);
			for (SVGElement element : elementList) {
				RealRange elemRange = element.getBoundingBox().getXRange();
				if (range.includes(elemRange)) {
					cell.add(element);
				}
			}
		}
	}

	public List<TableCell> getCellList() {
		return cellList;
	}

	public void setCellList(List<TableCell> cellList) {
		this.cellList = cellList;
	}

	public HtmlElement getHtml() {
		HtmlTr tr = new HtmlTr();
		for (TableCell cell : cellList) {
			tr.appendChild(cell.getHtml());
		}
		return tr;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append("{");
		for (TableCell cell : cellList) {
			sb.append("{"+cell.toString()+"}");
		}
		sb.append("}");
		return sb.toString();
	}
}
