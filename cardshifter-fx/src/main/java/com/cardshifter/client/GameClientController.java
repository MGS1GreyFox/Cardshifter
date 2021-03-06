package com.cardshifter.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.cardshifter.api.both.ChatMessage;
import com.cardshifter.api.incoming.RequestTargetsMessage;
import com.cardshifter.api.incoming.UseAbilityMessage;
import com.cardshifter.api.messages.Message;
import com.cardshifter.api.outgoing.AvailableTargetsMessage;
import com.cardshifter.api.outgoing.CardInfoMessage;
import com.cardshifter.api.outgoing.ClientDisconnectedMessage;
import com.cardshifter.api.outgoing.EntityRemoveMessage;
import com.cardshifter.api.outgoing.GameOverMessage;
import com.cardshifter.api.outgoing.NewGameMessage;
import com.cardshifter.api.outgoing.PlayerMessage;
import com.cardshifter.api.outgoing.ResetAvailableActionsMessage;
import com.cardshifter.api.outgoing.UpdateMessage;
import com.cardshifter.api.outgoing.UsableActionMessage;
import com.cardshifter.api.outgoing.WaitMessage;
import com.cardshifter.api.outgoing.WelcomeMessage;
import com.cardshifter.api.outgoing.ZoneChangeMessage;
import com.cardshifter.api.outgoing.ZoneMessage;
import com.cardshifter.client.views.ActionButton;
import com.cardshifter.client.views.BattlefieldZoneView;
import com.cardshifter.client.views.CardBattlefieldDocumentController;
import com.cardshifter.client.views.CardHandDocumentController;
import com.cardshifter.client.views.CardView;
import com.cardshifter.client.views.PlayerHandZoneView;
import com.cardshifter.client.views.ZoneView;
import com.cardshifter.core.messages.IncomingHandler;
import javafx.scene.input.MouseEvent;

public class GameClientController {
	
	private static final Logger logger = LogManager.getLogger(GameClientController.class);
	
	@FXML private AnchorPane rootPane;
	@FXML private Label loginMessage;
	@FXML private ListView<String> serverMessages;
	@FXML private VBox opponentStatBox;
	@FXML private VBox playerStatBox;
	@FXML private HBox actionBox;
	
	@FXML private HBox opponentHandPane;
	@FXML private Rectangle opponentTargetRectangle;
	@FXML private HBox opponentBattlefieldPane;
	@FXML private Pane opponentDeckPane;
	@FXML private Label opponentDeckLabel;
	@FXML private HBox playerHandPane;
	@FXML private Rectangle playerTargetRectangle;
	@FXML private HBox playerBattlefieldPane;
	@FXML private Pane playerDeckPane;
	@FXML private Label playerDeckLabel;
	
	@FXML private Label playerName;
	@FXML private Label opponentName;
	
	private int gameId;
	private int playerIndex;
	
	private int opponentId;
	private int opponentHandId;
	private int opponentBattlefieldId;
	private int opponentDeckId;
	private final Map<Integer, Set<Integer>> deckEntityIds = new HashMap<>();
	private int playerId;
	private int playerHandId;
	private int playerBattlefieldId;
	private int playerDeckId;
	
	private final Map<String, Integer> playerStatBoxMap = new HashMap<>();
	private final Map<String, Integer> opponentStatBoxMap = new HashMap<>();
	private final Map<Integer, ZoneView<?>> zoneViewMap = new HashMap<>();
	private final List<UsableActionMessage> savedMessages = new ArrayList<>();
	private final Set<Integer> chosenTargets = new HashSet<>();
	private AvailableTargetsMessage targetInfo;
	private Consumer<Message> sender;
	
	private IncomingHandler incomingHandler;
	
	public void acceptConnectionSettings(NewGameMessage message, Consumer<Message> sender) {
		// this is passed into this object after it is automatically created by the FXML document
		this.playerIndex = message.getPlayerIndex();
		this.gameId = message.getGameId();
		logger.info(String.format("You are player: %d", this.playerIndex));
		this.sender = sender;
		this.setUpMessageHandling();
	}
	
	private void setUpMessageHandling() {
		this.incomingHandler = new IncomingHandler();
		
		//incomingHandler.addHandler("login", LoginMessage.class, handlers::loginMessage);
		//incomingHandler.addHandler("chat", ChatMessage.class, handlers::chat);
		//incomingHandler.addHandler("startgame", StartGameRequest.class, handlers::play);
	}
	
	/**
	 * 
	 * @return Returns the IncomingHandler for use
	 */
	public IncomingHandler getIncomingHandler() {
		return incomingHandler;
	}
	public void performIncoming(Message message) {
		//functionality not yet complete
		getIncomingHandler().perform(message, null);
	}

	public void createAndSendMessage(UsableActionMessage action) {
		if (action.isTargetRequired()) {
			this.send(new RequestTargetsMessage(gameId, action.getId(), action.getAction()));
		} else {
			this.send(new UseAbilityMessage(gameId, action.getId(), action.getAction(), action.getTargetId()));
		}
		
		//A new list of actions will be sent back from the server, so it is okay to clear them
		this.actionBox.getChildren().clear();
		this.clearActiveFromAllCards();
	}
	
	private void send(Message message) {
		this.sender.accept(message);
	}
	
	public void processMessageFromServer(Message message) {
		//for diagnostics in client
		serverMessages.getItems().add(message.toString());
		//for diagnostics in console
		System.out.println(message.toString());

		//this.performIncoming(message);
		
		if (message instanceof WelcomeMessage) {
			Platform.runLater(() -> loginMessage.setText(message.toString()));
		} else if (message instanceof WaitMessage) {
			Platform.runLater(() -> loginMessage.setText(message.toString()));
		} else if (message instanceof PlayerMessage) {
			this.processPlayerMessage((PlayerMessage)message);
		} else if (message instanceof ZoneMessage) {
			this.assignZoneIdForZoneMessage((ZoneMessage)message);
		} else if (message instanceof CardInfoMessage) {
			this.processCardInfoMessage((CardInfoMessage)message);
		} else if (message instanceof UsableActionMessage) {
			this.savedMessages.add((UsableActionMessage)message);
			this.processUseableActionMessage((UsableActionMessage)message);
		} else if (message instanceof UpdateMessage) {
			this.processUpdateMessage((UpdateMessage)message);
		} else if (message instanceof ZoneChangeMessage) {
			this.processZoneChangeMessage((ZoneChangeMessage)message);
		} else if (message instanceof EntityRemoveMessage) {
			this.processEntityRemoveMessage((EntityRemoveMessage)message);
		} else if (message instanceof AvailableTargetsMessage) {
			this.processAvailableTargetsMessage((AvailableTargetsMessage)message);
		} else if (message instanceof ResetAvailableActionsMessage) {
			//this.processResetAvailableActionsMessage((ResetAvailableActionsMessage)message);
			this.clearSavedActions();
			this.unhighlightPlayerTargetRectangles();
		} else if (message instanceof ClientDisconnectedMessage) {
			this.processClientDisconnectedMessage((ClientDisconnectedMessage)message);
		} else if (message instanceof GameOverMessage) {
			this.processGameOverMessage((GameOverMessage)message);
		}
	}
	
	private void processPlayerMessage(PlayerMessage message) {
		if (message.getIndex() == this.playerIndex) {
			this.playerId = message.getId();
			Platform.runLater(() -> this.playerName.setText(message.getName()));
			this.processPlayerMessageForPlayer(message, playerStatBox, playerStatBoxMap);
		} else {
			this.opponentId = message.getId();
			Platform.runLater(() -> this.opponentName.setText(message.getName()));
			this.processPlayerMessageForPlayer(message, opponentStatBox, opponentStatBoxMap);
			Platform.runLater(() -> this.loginMessage.setText("Opponent Connected"));
		}
	}
	private void processPlayerMessageForPlayer(PlayerMessage message, Pane statBox, Map<String, Integer> playerMap) {
		statBox.getChildren().clear();
		Map<String, Integer> sortedMap = new TreeMap<>(message.getProperties());
		playerMap.putAll(sortedMap);
		for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
			String key = entry.getKey();
			int value = entry.getValue();
			Platform.runLater(() -> {
				statBox.getChildren().add(new Label(key));
				statBox.getChildren().add(new Label(String.format("%d",value)));
			});
		}
	}
	
	private void assignZoneIdForZoneMessage(ZoneMessage message) {
		if (!this.zoneViewMap.containsKey(message.getId())) {
			if (message.getName().equals("Battlefield")) {
				if(message.getOwner() == this.playerId) {
					this.playerBattlefieldId = message.getId();
					this.zoneViewMap.put(message.getId(), new BattlefieldZoneView(message.getId(), playerBattlefieldPane));
					
				} else {
					this.opponentBattlefieldId = message.getId();
					this.zoneViewMap.put(message.getId(), new BattlefieldZoneView(message.getId(), opponentBattlefieldPane));
				}
			} else if (message.getName().equals("Hand")) {
				if (message.getOwner() == this.playerId) {
					this.playerHandId = message.getId();
					this.zoneViewMap.put(message.getId(), new PlayerHandZoneView(message.getId(), playerHandPane));
				} else {
					this.opponentHandId = message.getId();
					this.zoneViewMap.put(this.opponentHandId, new ZoneView<CardView>(message.getId(), opponentHandPane));
					this.createOpponentHand(message);
				}
			} else if (message.getName().equals("Deck")) {
				if (message.getOwner() == this.playerId) {
					this.playerDeckId = message.getId();
					this.deckEntityIds.put(message.getId(), new HashSet<>());
					for (int entity : message.getEntities()) {
						this.addCardToDeck(playerDeckId, entity);
					}
					this.repaintDeckLabels();
					this.zoneViewMap.put(message.getId(), new ZoneView<CardView>(message.getId(), playerDeckPane));
				} else {
					this.opponentDeckId = message.getId();
					this.deckEntityIds.put(message.getId(), new HashSet<>());
					for (int entity : message.getEntities()) {
						this.addCardToDeck(opponentDeckId, entity);
					}
					this.repaintDeckLabels();
					this.zoneViewMap.put(message.getId(), new ZoneView<CardView>(message.getId(), opponentDeckPane));
				}
			}
		}
	}

	private void processCardInfoMessage(CardInfoMessage message) {
		int targetZone = message.getZone();
		
		if (targetZone == opponentBattlefieldId) {
			this.addCardToOpponentBattlefieldPane(message);
		} else if (targetZone == opponentHandId) {
			this.addCardToOpponentHandPane(message);
		} else if (targetZone == playerBattlefieldId) {
			this.addCardToPlayerBattlefieldPane(message);
		} else if (targetZone == playerHandId) {
			this.addCardToPlayerHandPane(message);
		} else {
			logger.warn("No known target Zone for " + message);
		}
	}	
	private void addCardToOpponentBattlefieldPane(CardInfoMessage message) {
		BattlefieldZoneView opponentBattlefield = getZoneView(opponentBattlefieldId);
		CardBattlefieldDocumentController card = new CardBattlefieldDocumentController(message, this);
		opponentBattlefield.addPane(message.getId(), card);
	}
	private void addCardToOpponentHandPane(CardInfoMessage message) {
		// this is unused because *KNOWN* cards don't pop up in opponent hand without reason (at least not now)
	}
	private void addCardToPlayerBattlefieldPane(CardInfoMessage message) {
		BattlefieldZoneView playerBattlefield = getZoneView(playerBattlefieldId);
		CardBattlefieldDocumentController card = new CardBattlefieldDocumentController(message, this);
		playerBattlefield.addPane(message.getId(), card);
	}
	private void addCardToPlayerHandPane(CardInfoMessage message) {
		PlayerHandZoneView playerHand = getZoneView(playerHandId);
		CardHandDocumentController card = new CardHandDocumentController(message, this);
		playerHand.addPane(message.getId(), card);
	}
	
	private void processUseableActionMessage(UsableActionMessage message) {
		ZoneView<?> zoneView = getZoneViewForCard(message.getId());
		logger.info("Usable message: " + message + " inform zone " + zoneView);
		if (zoneView == null) {
			this.createActionButton(message);
			return;
		}
		if (message.getAction().equals("Attack")) {
			((BattlefieldZoneView)zoneView).setCardCanAttack(message.getId(),message);
		} else if (message.getAction().equals("Scrap")) {
			zoneView.setCardScrappable(message.getId(), message);
		} else {
			zoneView.setCardActive(message.getId(), message);
		}
		
	}
	
	private void processUpdateMessage(UpdateMessage message) {
		if (message.getId() == this.playerId) {
			this.processUpdateMessageForPlayer(playerStatBox, message, playerStatBoxMap);
		} else if (message.getId() == this.opponentId) {
			this.processUpdateMessageForPlayer(opponentStatBox, message, opponentStatBoxMap);
		} else {
			this.processUpdateMessageForCard(message);
		}
	}
	
	private void processUpdateMessageForPlayer(Pane statBox, UpdateMessage message, Map<String, Integer> playerMap) {
		String key = (String)message.getKey();
		Integer value = (Integer)message.getValue();
		playerMap.put(key, value);
	
		this.repaintStatBox(statBox, playerMap);
	}
	
	private void processUpdateMessageForCard(UpdateMessage message) {
		ZoneView<?> zoneView = getZoneViewForCard(message.getId());
		if (zoneView != null) {
			zoneView.updateCard(message.getId(), message);
		}
	}
	
	private void processZoneChangeMessage(ZoneChangeMessage message) {
		int sourceZoneId = message.getSourceZone();
		int destinationZoneId = message.getDestinationZone();
		int cardId = message.getEntity();
		
		if (sourceZoneId == opponentDeckId) {
			this.removeCardFromDeck(sourceZoneId, cardId);
		} else if (sourceZoneId == playerDeckId) {
			this.removeCardFromDeck(sourceZoneId, cardId);
		}
		
		if (destinationZoneId == opponentHandId) {
			this.addCardToOpponentHand(cardId);
		}
		if (destinationZoneId == opponentDeckId || destinationZoneId == playerDeckId) {
			this.addCardToDeck(destinationZoneId, cardId);
		}
		if (this.zoneViewMap.containsKey(sourceZoneId) && this.zoneViewMap.containsKey(destinationZoneId)) {
			if (sourceZoneId == playerHandId) {
				PlayerHandZoneView sourceZone = getZoneView(sourceZoneId);
				CardHandDocumentController card = sourceZone.getCard(cardId);
				
				CardBattlefieldDocumentController newCard = new CardBattlefieldDocumentController(card.getCard(), this);
				
				ZoneView<?> zoneView = getZoneView(destinationZoneId);
				if (zoneView instanceof BattlefieldZoneView) {
					BattlefieldZoneView destinationZone = getZoneView(destinationZoneId);
					destinationZone.addPane(cardId, newCard);
				} else if (zoneView instanceof PlayerHandZoneView) {
					// TODO: Card moving from battlefield to hand for example, doesn't happen yet
				} else {
					// Card moving to deck is handled above
				}
			}
		}
		
		if (zoneViewMap.containsKey(sourceZoneId)) {
			ZoneView<?> view = zoneViewMap.get(sourceZoneId);
			view.removePane(cardId);
		}
	}
	
	private void addCardToDeck(int zoneId, int cardId) {
		logger.info("Add card to deck " + zoneId + " card " + cardId);
		Set<Integer> set = this.deckEntityIds.get(zoneId);
		set.add(cardId);
		this.repaintDeckLabels();
	}
	
	private void processEntityRemoveMessage(EntityRemoveMessage message) {
		int entityId = message.getEntity();
		for (Entry<Integer, Set<Integer>> deckIdsEntry : this.deckEntityIds.entrySet()) {
			int deckId = deckIdsEntry.getKey();
			Set<Integer> deckIds = deckIdsEntry.getValue();
			if (deckIds.contains(entityId)) {
				this.removeCardFromDeck(deckId, message.getEntity());
			}
		}
		
		ZoneView<?> zoneView = getZoneViewForCard(message.getEntity());
		if (zoneView != null) {
			zoneView.removePane(message.getEntity());
		}
	}
	
	private void processAvailableTargetsMessage(AvailableTargetsMessage message) {
		this.chosenTargets.clear();
		this.targetInfo = message;
		
		if (message.getAction().equals("Attack")) {
			ZoneView<?> attackerZoneView = getZoneViewForCard(message.getEntity());
			if (attackerZoneView != null) {
				((BattlefieldZoneView)attackerZoneView).setCardIsAttacking(message.getEntity());
			}
		}
		
		for (int i = 0; i < message.getTargets().length; i++) {
			int target = message.getTargets()[i];
			if (target != this.opponentId) {
				ZoneView<?> zoneView = getZoneViewForCard(target);
				if (zoneView != null) {
					zoneView.setCardTargetable(target);
				}
			} else {
				//Create the target box and action for self/opponent targeting
				UsableActionMessage newMessage = new UsableActionMessage(message.getEntity(), message.getAction(), false, target);
				this.highlightPlayerTargetRectangle(target, newMessage);
			}
		}
		this.createCancelActionsButton();
		if (message.getMax() != message.getMin()) {
			// This is an action that can have a variant amount of targets. We need a "Done" button to use it for fewer targets
			createActionButton("Done", () -> sendActionWithCurrentTargets(message));
		}
	}
	
	public boolean addTarget(int id) {
		if (chosenTargets.isEmpty() && targetInfo.getMax() == 1) {
			// Only one target, perform that action with target now
			this.createAndSendMessage(new UsableActionMessage(targetInfo.getEntity(), targetInfo.getAction(), false, id));
			return false; // Card should not be selected, because we are sending the action directly
		}
		
		if (chosenTargets.size() >= targetInfo.getMax()) {
			logger.info("Cannot add more targets");
			return false;
		}
		
		if (chosenTargets.add(id)) {
			return true;
		} else {
			chosenTargets.remove(id);
			return false;
		}
	}
	
	private void sendActionWithCurrentTargets(AvailableTargetsMessage message) {
		this.send(new UseAbilityMessage(gameId, message.getEntity(), message.getAction(), chosenTargets.stream().mapToInt(i -> i).toArray()));
		this.actionBox.getChildren().clear();
		this.clearActiveFromAllCards();
	}
	
	private void processClientDisconnectedMessage(ClientDisconnectedMessage message) {
		Platform.runLater(() -> this.loginMessage.setText("Opponent Left"));
	}
	
	private void processGameOverMessage(GameOverMessage message) {
		Platform.runLater(() -> this.loginMessage.setText("Game Over!"));
	}
	
	private void removeCardFromDeck(int zoneId, int cardId) {
		Set<Integer> set = this.deckEntityIds.get(zoneId);
		set.remove(cardId);
		this.repaintDeckLabels();
	}
	
	private void createActionButton(UsableActionMessage message) {
		createActionButton(message.getAction(), () -> createAndSendMessage(message));
	}
	
	private void createCancelActionsButton() {
		createActionButton("Cancel", () -> cancelAction());
	}
	
	private void createActionButton(String label, Runnable action) {
		double paneHeight = actionBox.getHeight();
		double paneWidth = actionBox.getWidth();
		
		int maxActions = 8;
		double actionWidth = paneWidth / maxActions;
		
		ActionButton actionButton = new ActionButton(label, actionWidth, paneHeight, action);
		actionBox.getChildren().add(actionButton);
	}
	
	private void clearSavedActions() {
		this.savedMessages.clear();
		this.actionBox.getChildren().clear();
		this.zoneViewMap.get(this.playerBattlefieldId).removeActiveAllCards();
	}
	
	public void cancelAction() {
		this.clearActiveFromAllCards();
		this.unhighlightPlayerTargetRectangles();
		this.actionBox.getChildren().clear();
		
		for (UsableActionMessage message : this.savedMessages) {
			this.processUseableActionMessage(message);
		}
	}
	
	private void clearActiveFromAllCards() {
		for (ZoneView<?> zoneView : this.zoneViewMap.values()) {
			zoneView.removeActiveAllCards();
			zoneView.removeScrappableAllCards();
		}
	}
	
	private void repaintStatBox(Pane statBox, Map<String, Integer> playerMap) {
		statBox.getChildren().clear();
		for (Map.Entry<String, Integer> entry : playerMap.entrySet()) {
			String key = entry.getKey();
			statBox.getChildren().add(new Label(key));
			int value = entry.getValue();
			statBox.getChildren().add(new Label(String.format("%d",value)));
		}
	}
	
	private void repaintDeckLabels() {
		if (this.deckEntityIds.containsKey(opponentDeckId)) {
			this.opponentDeckLabel.setText(String.format("%d", this.deckEntityIds.get(opponentDeckId).size()));
		}
		if (this.deckEntityIds.containsKey(playerDeckId)) {
			this.playerDeckLabel.setText(String.format("%d", this.deckEntityIds.get(playerDeckId).size()));
		}
	}
	
	private void highlightPlayerTargetRectangle(int playerId, UsableActionMessage message) {
		if (playerId == this.playerId) {
			this.playerTargetRectangle.setVisible(true);
			this.playerTargetRectangle.setOnMouseClicked(e -> this.clickToTargetPlayer(e, message));
		} else if (playerId == this.opponentId) {
			this.opponentTargetRectangle.setVisible(true);
			this.opponentTargetRectangle.setOnMouseClicked(e -> this.clickToTargetPlayer(e, message));
		}
	}
	
	private void unhighlightPlayerTargetRectangles() {
		this.playerTargetRectangle.setVisible(false);
		this.opponentTargetRectangle.setVisible(false);
	}
	
	private void clickToTargetPlayer(MouseEvent e, UsableActionMessage message) {
		this.createAndSendMessage(message);
	}
	
	private void createOpponentHand(ZoneMessage message) {
		for (int i : message.getEntities()) {
			this.addCardToOpponentHand(i);
		}
	}
	
	private void addCardToOpponentHand(int i) {
		ZoneView<?> opponentHand = this.zoneViewMap.get(this.opponentHandId);
		opponentHand.addSimplePane(i, this.cardForOpponentHand());
	}
	
	private Pane cardForOpponentHand() {
		double paneHeight = opponentHandPane.getHeight();
		double paneWidth = opponentHandPane.getWidth();
		
		int maxCards = 10;
		double cardWidth = paneWidth / maxCards;
		
		Pane card = new Pane();
		Rectangle cardBack = new Rectangle(0,0,cardWidth,paneHeight);
		cardBack.setFill(Color.AQUAMARINE);
		card.getChildren().add(cardBack);
		
		return card;
	}
	
	public void closeWindow() {
		Node source = this.rootPane;
		Stage stage = (Stage)source.getScene().getWindow();
		stage.close();
	}
	
	@SuppressWarnings("unchecked")
	private <T extends ZoneView<?>> T getZoneView(int id) {
		return (T) this.zoneViewMap.get(id);
	}
	
	private ZoneView<?> getZoneViewForCard(int id) {
		for (ZoneView<?> zoneView : this.zoneViewMap.values()) {
			if (zoneView.contains(id)) {
				return zoneView;
			}
		}
		return null;
	}
	public void closeGame() {
		this.send(new ChatMessage(1, "unused", "(Ends game " + gameId + ")"));
		// run on window close
	}
}

