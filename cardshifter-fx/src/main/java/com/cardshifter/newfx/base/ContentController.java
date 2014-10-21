
package com.cardshifter.newfx.base;


import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Frank van Heeswijk
 */
public abstract class ContentController extends BasicController implements ContentCallback {
	private final Deque<Node> contentHistory = new LinkedList<>();
	
	@FXML protected Pane contentPane;
	
	protected ContentController(final Stage stage) {
		super(stage);
	}
	
	@Override
	public void setContent(final Node content) {
		Objects.requireNonNull(content, "content");
		contentHistory.addFirst(content);
		setContentImpl(content);
	}
	
	
	private void setContentImpl(final Node content) {
		if (contentPane instanceof BorderPane) {
			((BorderPane)contentPane).setCenter(content);
		}
		else {
			contentPane.getChildren().add(content);
		}
	}
	
	@Override
	public void showPrevious() {
		contentHistory.removeFirst();
		setContent(contentHistory.removeFirst());
		//TODO if no more elements, show error somewhere
	}
}
