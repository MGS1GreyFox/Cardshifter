
package com.cardshifter.newfx;

import com.cardshifter.newfx.base.ContentController;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import com.cardshifter.newfx.base.Views;

/**
 *
 * @author Frank van Heeswijk
 */
public final class ClientController extends ContentController implements Initializable {
	@FXML private Label buildLabel;
	
	public ClientController(final Stage stage) {
		super(stage);
	}
	
	@Override
	public void initialize(final URL url, final ResourceBundle resourceBundle) {
		buildLabel.setText("Build: " + getClass().getPackage().getImplementationVersion());
		setContent(Views.load(getClass().getResource("Homepage.fxml"), new HomepageController(stage, this)));
	}
	
	@FXML
	private void handleExitButtonAction(final ActionEvent actionEvent) {
		Platform.exit();	//TODO replace with graceful exit
	}
}
