package org.fordes.subtitles.view.utils.submerge.constant;

/**
 * Enum all the supported font names of the application
 *
 */
public enum FontName {
	
	Arial("Arial"), 
	CourierNew("Courier New"),
	Times("Times"),
	Helvetica("Helvetica"),
	DroidSans("Droid Sans"),
	Cursive("cursive"),
	Monospace("monospace"),
	Serif("serif"),
	SansSerif("sans-serif"),
	Fantasy("fantasy"),
	Courier("Courier"),
	Georgia("Georgia"),
	LucidaConsole("Lucida Console"),
	Papyrus("Papyrus"),
	Tahoma("Tahoma"),
	TeX("TeX"),
	Verdana("Verdana"),
	Verona("Verona"),
	SimSun("SimSun"),
	Ubuntu("Ubuntu"),
	UbuntuMono("Ubuntu Mono"),
	FreeMono("FreeMono"),
	LiberationSerif("Liberation Serif"),
	Purisa("Purisa"),
	TimesNewRoman("Times New Roman");

	private String name;

	FontName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.name;
	}	

}
