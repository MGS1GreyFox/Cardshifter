package com.cardshifter.client.views;

import com.cardshifter.api.outgoing.CardInfoMessage;
import com.cardshifter.api.outgoing.UpdateMessage;
import com.cardshifter.api.outgoing.UsableActionMessage;
import com.cardshifter.client.GameClientController;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public final class CardBattlefieldDocumentController extends CardView implements Initializable {
    
    @FXML private Label strength;
    @FXML private Label health;
	@FXML private Label cardId;
	@FXML private Label scrapValue;
	@FXML private Label nameText;
	@FXML private Label abilityText;
    @FXML private Label creatureType;
	@FXML private Rectangle background;
	@FXML private Circle sicknessCircle;
	@FXML private AnchorPane anchorPane;
	@FXML private Button scrapButton;
    
	private boolean isActive;
    private final CardInfoMessage card;
	private final GameClientController controller;
	private UsableActionMessage message;
	
	private Map<String, Integer> cardValues = new HashMap<>();
	
    public CardBattlefieldDocumentController(CardInfoMessage message, GameClientController controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CardBattlefieldDocument.fxml"));
            loader.setController(this);
			loader.load();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        } 
        this.card = message;
		this.controller = controller;
        this.setCardId();
        this.setCardLabels();
		
		scrapButton.setOnMouseClicked(this::scrapButtonAction);
    }
	
	private void setCardId() {
        int newId = card.getId();
        cardId.setText(String.format("CardId = %d", newId));
    }

    private void setCardLabels() {
		for (Entry<String, Object> entry : this.card.getProperties().entrySet()) {
			Object value = entry.getValue();
			String stringValue = String.valueOf(entry.getValue());
			String key = entry.getKey();
			
			try {
				this.cardValues.put(key, Integer.parseInt(stringValue));
			} catch (NumberFormatException e) {
				System.out.println("Not a number");
			}
						
			switch (entry.getKey()) {
				case "SICKNESS":
					int sicknessValue = (Integer) value;
					if (sicknessValue == 1) {
						this.setSickness();
					}	
					break;
				case "ATTACK":
					strength.setText(stringValue);
					break;
				case "name":
					nameText.setText(stringValue);
					break;
				case "effect":
					abilityText.setText(stringValue);
					break;
				case "HEALTH":
					health.setText(stringValue);
					break;
				case "ATTACK_AVAILABLE":
					break;
				case "creatureType":
					creatureType.setText(stringValue);
					break;
				case "SCRAP":
					scrapValue.setText(String.format("Scrap = %s", stringValue));
					break;
				default:
					break;
			}
		}
		abilityText.setText(abilityText.getText() + CardHelper.stringResources(this.card));
    }
    
	@Override
    public AnchorPane getRootPane() {
		return this.anchorPane;
    }
	
	public boolean isCardActive() {
		return this.isActive;
	}
	
	public void setCardAttackActive(UsableActionMessage message) {
		this.isActive = true;
		this.message = message;
		this.anchorPane.setOnMouseClicked(this::actionOnClick);
        background.setFill(Color.DARKGREEN);
	}
	
	public void setCardIsAttacking() {
		this.background.setFill(Color.MAGENTA);
	}
	
	private void setSickness() {
		sicknessCircle.setVisible(true);
	}
	
	@Override
	public void setCardScrappable(UsableActionMessage message) {
		this.message = message;
		background.setFill(Color.GRAY);
		this.scrapButton.setVisible(true);
	}

	@Override
    public void setCardActive(UsableActionMessage message) {
		this.isActive = true;
		this.message = message;
        background.setFill(Color.YELLOW);
		//this.anchorPane.setOnMouseClicked(this::actionOnClick);
    }
	
	@Override
	public void setCardTargetable() {
		this.anchorPane.setOnMouseClicked(this::actionOnTarget);
		background.setFill(Color.BLUE);
	}

	public void removeSickness() {
		sicknessCircle.setVisible(false);
	}
	
	@Override
	public void removeCardScrappable() {
		this.message = null;
		this.background.setFill(Color.BLACK);
		this.scrapButton.setVisible(false);
	}
	
	@Override
	public void removeCardActive() {
		this.isActive = false;
		this.message = null;
		this.anchorPane.setOnMouseClicked(e -> {});
		background.setFill(Color.BLACK);
	}
	
	private void scrapButtonAction(MouseEvent event) {
		scrapButton.setVisible(false);
		UsableActionMessage scrapMessage = new UsableActionMessage(this.message.getId(), "Scrap", false, 0);
		this.controller.createAndSendMessage(scrapMessage);
	}
	
	private void actionOnClick(MouseEvent event) {
		this.controller.createAndSendMessage(this.message);
	}

	@Override
	public void updateFields(UpdateMessage message) {
		if (message.getKey().equals("ATTACK")) {
			strength.setText(String.format("%d", message.getValue()));
			if ((int)message.getValue() < this.cardValues.get(message.getKey().toString())) {
				strength.setTextFill(Color.ORANGE);
			} else if ((int)message.getValue() > this.cardValues.get(message.getKey().toString())) {
				strength.setTextFill(Color.GREENYELLOW);
			} else {
				strength.setTextFill(Color.WHITE);
			}
		} else if (message.getKey().equals("HEALTH")) {
			health.setText(String.format("%d", message.getValue()));
			if ((int)message.getValue() < this.cardValues.get("MAX_HEALTH")) {
				health.setTextFill(Color.ORANGE);
			} else if ((int)message.getValue() > this.cardValues.get(message.getKey().toString())) {
				health.setTextFill(Color.GREENYELLOW);
			} else {
				health.setTextFill(Color.WHITE);
			}
		} else if (message.getKey().equals("creatureType")) {
			creatureType.setText(String.valueOf(message.getValue()));
		} else if (message.getKey().equals("SICKNESS")) {
			if ((int)message.getValue() == 0) {
				this.removeSickness();
			}
		}

	}

	private void actionOnTarget(MouseEvent event) {
		boolean isChosenTarget = controller.addTarget(card.getId());
		background.setFill(isChosenTarget ? Color.VIOLET : Color.BLUE);
	}
	
	@Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
