
package com.cardshifter.newfx;

import com.cardshifter.newfx.base.ContentController;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import org.controlsfx.control.action.Action;

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
		super.initialize(url, resourceBundle);
		
		Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
			useNotificationPane(notificationPane -> {
				Action action = new Action("Details", actionEvent -> {
//					//TODO showDialog
				});
				notificationPane.show(ex.getClass().getSimpleName() + ": " + ex.getMessage() + " on " + thread, new ImageView(new Image(getClass().getResourceAsStream("dialog-error.png"))), action);
			});
			ex.printStackTrace();
		});
		
		buildLabel.setText("Build: " + getClass().getPackage().getImplementationVersion());
		setContent(Views.load(getClass().getResource("Homepage.fxml"), new HomepageController(stage, this)));
	}
	
	@FXML
	private void handleExitButtonAction(final ActionEvent actionEvent) {
		Platform.exit();	//TODO replace with graceful exit
	}
}
