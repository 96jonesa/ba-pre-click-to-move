package com.bapreclicktomove;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Singleton;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

/**
 * Tracks which wave the lobby is waiting for.
 * <p>
 * The game announces each wave as it starts with a game message like {@code ---- Wave: 9 ----}. Until the wave is
 * won, the next lobby is for that same wave again, since a failed wave is replayed. Winning opens the wave-complete
 * interface, after which the lobby is for the following wave.
 */
@Singleton
class WaveTracker
{
	static final int UNKNOWN = 0;

	private static final Pattern WAVE_START = Pattern.compile("^---- Wave: (\\d+)");

	private int currentWave = UNKNOWN;

	/**
	 * The wave the lobby is waiting for, or {@link #UNKNOWN} if no wave has started since the plugin started.
	 */
	@Getter
	private int nextWave = UNKNOWN;

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		Matcher m = WAVE_START.matcher(Text.removeTags(event.getMessage()));
		if (m.lookingAt())
		{
			currentWave = Integer.parseInt(m.group(1));
			nextWave = currentWave;
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == InterfaceID.BARBASSAULT_WAVECOMPLETE && currentWave != UNKNOWN)
		{
			nextWave = currentWave + 1;
		}
	}

	void reset()
	{
		currentWave = UNKNOWN;
		nextWave = UNKNOWN;
	}
}
