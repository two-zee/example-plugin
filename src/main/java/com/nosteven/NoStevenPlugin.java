package com.nosteven;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Renderable;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "No Steven",
	configName = "nosteven",
	description = "Hides everything related to the \"SteVen\" bots that plague F2P",
	tags = {
		"bot",
		"hide",
		"player"
	}
)
public class NoStevenPlugin extends Plugin
{
	@Inject
	private Hooks hooks;

	@Inject
	private Client client;

	@Inject
	private NoStevenConfig config;

	private final Hooks.RenderableDrawListener stevenFinder = this::isNotSteven;

	@Override
	protected void startUp()
	{
		hooks.registerRenderableDrawListener(stevenFinder);
	}

	@Override
	protected void shutDown()
	{
		hooks.unregisterRenderableDrawListener(stevenFinder);
	}

	private boolean isNotSteven(final Renderable renderable, final boolean __)
	{
		Player potentialSteven = null;

		if (renderable instanceof Player)
		{
			potentialSteven = (Player) renderable;
		}
		else if (config.hideInteractors() && renderable instanceof NPC)
		{
			final var potentialInteractor = (NPC) renderable;

			if (potentialInteractor.isInteracting())
			{
				potentialSteven = (Player) potentialInteractor.getInteracting();
			}
		}

		if (potentialSteven == null)
		{
			return true;
		}

		// Don't hide the player's own model
		if (potentialSteven.equals(client.getLocalPlayer()))
		{
			return true;
		}

		return !isStevenName(potentialSteven.getName());
	}

	private boolean isStevenName(final String name)
	{
		try
		{
			return config.strictMode() ? name.startsWith("SteVen") : name.toLowerCase().startsWith("steven");
		}
		catch (Exception e)
		{
			return false;
		}
	}

	@Subscribe
	private void onScriptCallbackEvent(final ScriptCallbackEvent scriptCallbackEvent)
	{
		if (!scriptCallbackEvent.getEventName().equals("chatFilterCheck"))
		{
			return;
		}

		// Initialize all of these as early as possible to avoid them changing in the middle of execution
		final var messages = client.getMessages();
		final var intStack = client.getIntStack();
		final var intStackSize = client.getIntStackSize();
		final var stringStack = client.getStringStack();
		final var stringStackSize = client.getStringStackSize();

		final var message = stringStack[stringStackSize - 1];
		final var messageNode = messages.get(intStack[intStackSize - 1]);

		// Don't hide the player's own messages in case they have a Steven name
		if (messageNode.getName() == null || messageNode.getValue().equals(client.getLocalPlayer().getName()))
		{
			return;
		}

		if (isStevenName(messageNode.getName()))
		{
			// Hide Steven's message
			intStack[intStackSize - 3] = 0;
		}

		stringStack[stringStackSize - 1] = message;
	}

	@Provides
	NoStevenConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NoStevenConfig.class);
	}
}
