package com.cardshifter.newfx;

import com.cardshifter.newfx.base.BasicController;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.cardshifter.newfx.base.ContentCallback;

/**
 * FXML Controller class
 *
 * @author Frank van Heeswijk
 */
public class PlayOnlineController extends BasicController implements Initializable {
	private final ContentCallback contentCallback;
	
	@FXML private TextField ipAddressTextField;
	@FXML private TextField portTextField;
	@FXML private TextField usernameTextField;
	
	public PlayOnlineController(final Stage stage, final ContentCallback contentCallback) {
		super(stage);
		this.contentCallback = Objects.requireNonNull(contentCallback, "contentCallback");
	}
	
	@Override
	public void initialize(final URL url, final ResourceBundle resourceBundle) {
		
	}
	
	@FXML
	private void handleConnectButtonAction(final ActionEvent actionEvent) {
		Label label = new Label("This feature is not available yet.");
		label.setOnMouseClicked(mouseEvent -> contentCallback.closeDialog());
		contentCallback.showDialog(label);
	}
	
	@FXML
	private void handleCancelButtonAction(final ActionEvent actionEvent) {
		contentCallback.showPrevious();
	}
}
