package com.cardshifter.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cardshifter.api.both.ChatMessage;
import com.cardshifter.api.both.InviteRequest;
import com.cardshifter.api.both.InviteResponse;
import com.cardshifter.api.both.PlayerConfigMessage;
import com.cardshifter.api.config.DeckConfig;
import com.cardshifter.api.incoming.LoginMessage;
import com.cardshifter.api.incoming.ServerQueryMessage;
import com.cardshifter.api.messages.Message;
import com.cardshifter.api.outgoing.AvailableModsMessage;
import com.cardshifter.api.outgoing.NewGameMessage;
import com.cardshifter.api.outgoing.UserStatusMessage;
import com.cardshifter.gdx.*;
import com.cardshifter.gdx.ui.UsersList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zomis on 2014-11-11.
 */
public class ClientScreen implements Screen, CardshifterMessageHandler {
    private final CardshifterClient client;
    private final Map<Class<? extends Message>, SpecificHandler<?>> handlerMap = new HashMap<Class<? extends Message>, SpecificHandler<?>>();
    private final Table table;
    private final HorizontalGroup mods;
    private final CardshifterGame game;
    private final TextArea chatMessages;
    private final UsersList usersList;
    private String[] availableMods;
    private GameScreen gameScreen;
    private String currentModName;

    public ClientScreen(final CardshifterGame game, String host, int port, final String username) {
        this.game = game;
        client = new CardshifterClient(host, port, this);
        table = new Table(game.skin);
        table.setFillParent(true);
        mods = new HorizontalGroup();
        chatMessages = new TextArea("", game.skin);
        usersList = new UsersList(game.skin, new Callback<String>() {
            @Override
            public void callback(String object) {
                currentModName = object;
            }
        });
        table.add(new ScrollPane(chatMessages)).top().expand().fill();
        table.add(usersList.getTable()).right().expandY().fill();
        table.row();
        table.add(mods).bottom().expandX().fill();
        TextButton inviteButton = new TextButton("Invite", game.skin);
        inviteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                usersList.inviteSelected(availableMods, game.stage, client);
            }
        });
        table.add(inviteButton).right().expand().fill();
        handlerMap.put(AvailableModsMessage.class, new SpecificHandler<AvailableModsMessage>() {
            @Override
            public void handle(AvailableModsMessage message) {
                availableMods = message.getMods();
                client.send(new ServerQueryMessage(ServerQueryMessage.Request.USERS));
            }
        });
        handlerMap.put(ChatMessage.class, new SpecificHandler<ChatMessage>() {
            @Override
            public void handle(ChatMessage message) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String append = "\n" + "[" + format.format(Calendar.getInstance().getTime()) + "] " + message.getFrom() + ": " + message.getMessage();
                chatMessages.setText(chatMessages.getText() + append);
            }
        });
        handlerMap.put(UserStatusMessage.class, new SpecificHandler<UserStatusMessage>() {
            @Override
            public void handle(UserStatusMessage message) {
                usersList.handleUserStatus(message);
            }
        });
        handlerMap.put(NewGameMessage.class, new SpecificHandler<NewGameMessage>() {
            @Override
            public void handle(NewGameMessage message) {
                game.setScreen(gameScreen = new GameScreen(game, client, message, ClientScreen.this));
                handlerMap.putAll(gameScreen.getHandlers());
            }
        });
        handlerMap.put(PlayerConfigMessage.class, new SpecificHandler<PlayerConfigMessage>() {
            @Override
            public void handle(final PlayerConfigMessage message) {
                DeckConfig deckConfig = (DeckConfig) message.getConfigs().get("Deck");
                if (deckConfig != null) {
                    game.setScreen(new DeckBuilderScreen(game, currentModName, message.getGameId(), deckConfig, new Callback<DeckConfig>() {
                        @Override
                        public void callback(DeckConfig object) {
                            game.setScreen(gameScreen);
                            client.send(message);
                        }
                    }));
                }
            }
        });
        handlerMap.put(InviteRequest.class, new SpecificHandler<InviteRequest>() {
            @Override
            public void handle(final InviteRequest message) {
                Dialog dialog = new Dialog("Invite", game.skin) {
                    @Override
                    protected void result(Object object) {
                        boolean response = (Boolean) object;
                        client.send(new InviteResponse(message.getId(), response));
                    }
                };
                currentModName = message.getGameType();
                dialog.text(message.getName() + " invites you to play " + message.getGameType());
                dialog.button("Accept", true);
                dialog.button("Decline", false);
                dialog.show(game.stage);
            }
        });

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                client.send(new LoginMessage(username));
            }
        });
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
        game.stage.addActor(table);
    }

    @Override
    public void hide() {
        table.remove();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void handle(final Message message) {
        final SpecificHandler<Message> handler = (SpecificHandler<Message>) handlerMap.get(message.getClass());
        if (handler != null) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    handler.handle(message);
                }
            });
        }
        else {
            Gdx.app.log("Client", "WARNING: Unable to handle " + message + " of type " + message.getClass());
        }
    }
}
