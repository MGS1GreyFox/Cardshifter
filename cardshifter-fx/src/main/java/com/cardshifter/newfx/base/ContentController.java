
package com.cardshifter.newfx.base;


import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.controlsfx.control.NotificationPane;

/**
 *
 * @author Frank van Heeswijk
 */
public abstract class ContentController extends BasicController implements ContentCallback {
	private final Deque<Node> contentHistory = new LinkedList<>();
	
	@FXML protected Pane contentPane;
	
	private NotificationPane notificationPane;
	
	protected ContentController(final Stage stage) {
		super(stage);
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
		if (contentPane instanceof BorderPane) {
			((BorderPane)contentPane).setCenter(notificationAwareContent);
		}
		else {
			contentPane.getChildren().add(notificationAwareContent);
		}
	}
	
	private Node createNotificationAwareNode(final Node content, final Consumer<NotificationPane> notificationSetupConsumer) {
		notificationPane = new NotificationPane(content);
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
}
