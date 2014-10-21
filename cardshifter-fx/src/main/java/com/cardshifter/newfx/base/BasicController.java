
package com.cardshifter.newfx.base;


import java.util.Objects;

import javafx.stage.Stage;

/**
 *
 * @author Frank van Heeswijk
 */
public abstract class BasicController {
	protected final Stage stage;
	
	protected BasicController(final Stage stage) {
		this.stage = Objects.requireNonNull(stage, "stage");
	}
	
//	public void setStage(final Stage stage) {
//		this.stage = Objects.requireNonNull(stage, "stage");
//	}
//	
//	abstract protected void secondInitialization();
}
