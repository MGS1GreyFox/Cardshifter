
package com.cardshifter.newfx.base;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Objects;

import javafx.fxml.FXMLLoader;

/**
 *
 * @author Frank van Heeswijk
 */
public final class Views {
	private Views() {
		throw new UnsupportedOperationException();
	}
	
	public static <T> T load(final URL location, final BasicController controller) {
		Objects.requireNonNull(location, "location");
		Objects.requireNonNull(controller, "controller");
		try {
			FXMLLoader loader = new FXMLLoader(location);
			loader.setController(controller);
			return loader.load();
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}
}
