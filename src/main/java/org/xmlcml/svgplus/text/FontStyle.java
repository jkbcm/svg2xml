package org.xmlcml.svgplus.text;

import org.xmlcml.graphics.svg.SVGText;

/** manages the simple (Java-like) fontStyles
 * intially for analyzing text in TextAnalyzer
 * @author pm286
 *
 */
public class FontStyle {
	
	public enum Style {
		BOLD,
		BOLD_ITALIC,
		ITALIC,
		NORMAL
	}

	Style style;
	
	public FontStyle(String sty) {
		this.style = null;
		if (sty.equalsIgnoreCase(Style.BOLD.toString())) {
			style = Style.BOLD;
		} else if (sty.equalsIgnoreCase(Style.ITALIC.toString())) {
			style = Style.ITALIC;
		} else if (sty.equalsIgnoreCase(Style.NORMAL.toString())) {
			style = Style.NORMAL;
		} else if (sty.toUpperCase().contains(Style.BOLD.toString()) &&
	               sty.toUpperCase().contains(Style.ITALIC.toString())) {
			style = Style.BOLD_ITALIC;
		} else {
			style = Style.NORMAL; // default; is this a good idea?
		}
	}
	
	public FontStyle(Style style) {
		this.style = style;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equals =false;
		if (obj != null && obj instanceof FontStyle) {
			FontStyle fontStyle = (FontStyle)obj;
			equals = fontStyle.style.equals(style);
		}
		return equals;
	}
	
	@Override
	public int hashCode() {
		return style == null ? 37 : style.toString().hashCode();
	}

	public static FontStyle getFontStyle(SVGText text) {
		Style style = null;
		if (text != null) {
			String fontWeight = text.getFontWeight();
			System.out.println(text.toXML());
			boolean bold = Style.BOLD.toString().equalsIgnoreCase(fontWeight);
			String fontStyle = text.getFontStyle();
			boolean italic = Style.ITALIC.toString().equalsIgnoreCase(fontStyle);
			if (bold) {
				style = (italic) ? Style.BOLD_ITALIC : Style.BOLD;
			} else {
				style = (italic) ? Style.ITALIC : Style.NORMAL;
			}
		}
		return style == null ? null : new FontStyle(style);
	}
	
	public String toString() {
		return style.toString();
	}
}
