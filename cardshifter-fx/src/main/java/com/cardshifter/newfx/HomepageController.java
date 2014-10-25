package com.cardshifter.newfx;

import com.cardshifter.newfx.base.BasicController;
import com.cardshifter.newfx.base.ContentCallback;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import com.cardshifter.newfx.base.Views;

/**
 * FXML Controller class
 *
 * @author Frank van Heeswijk
 */
public final class HomepageController extends BasicController implements Initializable {
	private final ContentCallback contentCallback;
	
	@FXML private WebView releasesWebView;
	private WebEngine releasesWebEngine;
	
	public HomepageController(final Stage stage, final ContentCallback contentCallback) {
		super(stage);
		this.contentCallback = Objects.requireNonNull(contentCallback, "contentCallback");
	}
	
	@Override
	public void initialize(final URL url, final ResourceBundle resourceBundle) {
		releasesWebEngine = releasesWebView.getEngine();
		releasesWebEngine.load("https://github.com/Cardshifter/Cardshifter/wiki/Releases-&-Features#releases");
	}
	
	@FXML
	private void handlePlayLocalButtonAction(final ActionEvent actionEvent) {
		contentCallback.useNotificationPane(notificationPane -> notificationPane.show("This feature is not available yet."));
	}
	
	@FXML
	private void handlePlayOnlineButtonAction(final ActionEvent actionEvent) {
		contentCallback.setContent(Views.load(getClass().getResource("PlayOnline.fxml"), new PlayOnlineController(stage, contentCallback)));
	}
}
