
package com.cardshifter.newfx.base;


import javafx.scene.Node;

/**
 *
 * @author Frank van Heeswijk
 */
public interface ContentCallback {
	void setContent(final Node node);
	
	void showPrevious();
}
