
package com.cardshifter.newfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import com.cardshifter.newfx.base.Views;

/**
 *
 * @author Frank van Heeswijk
 */
public final class Client extends Application {
	@Override
	public void start(final Stage stage) throws Exception {
		Scene scene = new Scene(Views.load(getClass().getResource("Client.fxml"), new ClientController(stage)));
		
		stage.setScene(scene);
		stage.setFullScreen(true);
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
