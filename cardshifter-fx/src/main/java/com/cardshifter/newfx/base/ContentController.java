
package com.cardshifter.newfx.base;


import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.controlsfx.control.NotificationPane;

/**
 *
 * @author Frank van Heeswijk
 */
public abstract class ContentController extends BasicController implements Initializable, ContentCallback {
	private final Deque<Node> contentHistory = new LinkedList<>();
	
	@FXML protected Pane contentPane;
	
	private AnchorPane glassPane;
	private BorderPane internalContentPane;
	private BorderPane internalDialogPane;
	
	private NotificationPane notificationPane;
	
	protected ContentController(final Stage stage) {
		super(stage);
	}
	
	@Override
	public void initialize(final URL url, final ResourceBundle resourceBundle) {
		internalContentPane = new BorderPane();
		internalContentPane.disabledProperty().addListener((observableValue, oldValue, newValue) -> {
			ColorAdjust dimmedColorAdjust = new ColorAdjust();
			dimmedColorAdjust.setBrightness(0.5d);
			internalContentPane.setEffect((newValue) ? dimmedColorAdjust : null);
		});
		
		glassPane = new AnchorPane(internalContentPane);
		AnchorPane.setTopAnchor(internalContentPane, 0d);
		AnchorPane.setBottomAnchor(internalContentPane, 0d);
		AnchorPane.setRightAnchor(internalContentPane, 0d);
		AnchorPane.setLeftAnchor(internalContentPane, 0d);
		
		internalDialogPane = new BorderPane();
		internalDialogPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
		internalDialogPane.setMouseTransparent(true);
		glassPane.getChildren().add(internalDialogPane);
		AnchorPane.setTopAnchor(internalDialogPane, 0d);
		AnchorPane.setBottomAnchor(internalDialogPane, 0d);
		AnchorPane.setRightAnchor(internalDialogPane, 0d);
		AnchorPane.setLeftAnchor(internalDialogPane, 0d);
		
		placeGlassPaneInsideContentPane();
	}
	
	private void placeGlassPaneInsideContentPane() {
		if (contentPane instanceof BorderPane) {
			((BorderPane)contentPane).setCenter(glassPane);
		}
		else {
			contentPane.getChildren().add(glassPane);
		}
	}
	
	@Override
	public void setContent(final Node content, final Consumer<NotificationPane> notificationSetupConsumer) {
		Objects.requireNonNull(content, "content");
		Objects.requireNonNull(notificationSetupConsumer, "notificationSetupConsumer");
		contentHistory.addFirst(content);
		setContentImpl(content, notificationSetupConsumer);
	}
	
	
	
	private void setContentImpl(final Node content, final Consumer<NotificationPane> notificationSetupConsumer) {
		Node notificationAwareContent = createNotificationAwareNode(content, notificationSetupConsumer);
		internalContentPane.setCenter(notificationAwareContent);
	}
	
	private Node createNotificationAwareNode(final Node content, final Consumer<NotificationPane> notificationSetupConsumer) {
		notificationPane = new NotificationPane(content);
		notificationPane.setStyle("-fx-border-color: red; -fx-border-with: 2;");
		notificationSetupConsumer.accept(notificationPane);
		return notificationPane;
	}
	
	@Override
	public void showPrevious() {
		contentHistory.removeFirst();
		setContent(contentHistory.removeFirst());
		//TODO if no more elements, show error somewhere
	}

	@Override
	public void useNotificationPane(final Consumer<NotificationPane> notificationActionConsumer) {
		notificationActionConsumer.accept(notificationPane);
	}
	
	@Override
	public void showDialog(final Node node) {
		Objects.requireNonNull(node, "node");
		internalContentPane.setDisable(true);
		internalDialogPane.setMouseTransparent(false);
		internalDialogPane.setCenter(node);
	}
	
	@Override
	public void closeDialog() {
		internalDialogPane.setCenter(null);
		internalDialogPane.setMouseTransparent(true);
		internalContentPane.setDisable(false);
	}
}
