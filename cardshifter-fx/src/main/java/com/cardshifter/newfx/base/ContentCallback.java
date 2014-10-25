
package com.cardshifter.newfx.base;


import java.util.function.Consumer;

import javafx.scene.Node;

import org.controlsfx.control.NotificationPane;

/**
 *
 * @author Frank van Heeswijk
 */
public interface ContentCallback {
	default void setContent(final Node node) {
		setContent(node, notificationPane -> { });
	}
	
	void setContent(final Node node, final Consumer<NotificationPane> notificationSetupConsumer);
	
	void showPrevious();
	
	void useNotificationPane(final Consumer<NotificationPane> notificationActionConsumer);
	
	void showDialog(final Node node);
	
	void closeDialog();
}
