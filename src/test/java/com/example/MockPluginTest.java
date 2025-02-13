package com.example;

import com.google.inject.testing.fieldbinder.Bind;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MockPluginTest extends MockTestBase {

    @Bind
    protected ExampleConfig config = Mockito.mock(ExampleConfig.class);

    @Bind
    protected Client client = Mockito.mock(Client.class);

    @Bind
    protected ExamplePlugin plugin = Mockito.spy(ExamplePlugin.class);

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        when(config.greeting()).thenReturn("Hello");
    }

    @Test
    public void testLoginGreeting() {
        var event = new GameStateChanged();
        event.setGameState(GameState.LOGGED_IN);
        plugin.onGameStateChanged(event);
        verify(client).addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says Hello", null);
    }

}
