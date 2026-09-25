package com.bapreclicktomove;

import static org.junit.jupiter.api.Assertions.assertEquals;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class WaveTrackerTest
{
	private WaveTracker tracker;

	@BeforeEach
	void setUp()
	{
		tracker = new WaveTracker();
	}

	private void chat(ChatMessageType type, String message)
	{
		ChatMessage event = new ChatMessage();
		event.setType(type);
		event.setMessage(message);
		tracker.onChatMessage(event);
	}

	private void waveStarts(int wave)
	{
		chat(ChatMessageType.GAMEMESSAGE, "---- Wave: " + wave + " ----");
	}

	private void loaded(int groupId)
	{
		WidgetLoaded event = new WidgetLoaded();
		event.setGroupId(groupId);
		tracker.onWidgetLoaded(event);
	}

	private void waveCompletes()
	{
		loaded(InterfaceID.BARBASSAULT_WAVECOMPLETE);
	}

	@Test
	void unknownBeforeAnyWave()
	{
		assertEquals(WaveTracker.UNKNOWN, tracker.getNextWave());
	}

	@Test
	void lobbyAfterWave9IsWave10()
	{
		waveStarts(9);
		waveCompletes();

		assertEquals(10, tracker.getNextWave());
	}

	@Test
	void failedWaveIsReplayed()
	{
		waveStarts(9);

		assertEquals(9, tracker.getNextWave());
	}

	@Test
	void wholeGameCountsUp()
	{
		for (int wave = 1; wave <= 9; wave++)
		{
			waveStarts(wave);
			waveCompletes();
			assertEquals(wave + 1, tracker.getNextWave());
		}
	}

	@Test
	void newGameStartsOver()
	{
		waveStarts(10);
		waveCompletes();
		waveStarts(1);
		waveCompletes();

		assertEquals(2, tracker.getNextWave());
	}

	@Test
	void waveCompleteWithoutKnownWaveStaysUnknown()
	{
		waveCompletes();

		assertEquals(WaveTracker.UNKNOWN, tracker.getNextWave());
	}

	@Test
	void reopenedWaveCompleteDoesNotSkipAhead()
	{
		waveStarts(9);
		waveCompletes();
		waveCompletes();

		assertEquals(10, tracker.getNextWave());
	}

	@Nested
	class TestOnChatMessage
	{
		@Test
		void ignoresNonGameMessages()
		{
			chat(ChatMessageType.PUBLICCHAT, "---- Wave: 9 ----");

			assertEquals(WaveTracker.UNKNOWN, tracker.getNextWave());
		}

		@Test
		void ignoresOtherGameMessages()
		{
			chat(ChatMessageType.GAMEMESSAGE, "Wave: 9");

			assertEquals(WaveTracker.UNKNOWN, tracker.getNextWave());
		}

		@Test
		void onlyNeedsThePrefix()
		{
			chat(ChatMessageType.GAMEMESSAGE, "---- Wave: 9");

			assertEquals(9, tracker.getNextWave());
		}

		@Test
		void stripsTags()
		{
			chat(ChatMessageType.GAMEMESSAGE, "<col=ff0000>---- Wave: 9 ----</col>");

			assertEquals(9, tracker.getNextWave());
		}
	}

	@Nested
	class TestOnWidgetLoaded
	{
		@Test
		void ignoresOtherInterfaces()
		{
			waveStarts(9);
			loaded(InterfaceID.BARBASSAULT_OVER_ATT);

			assertEquals(9, tracker.getNextWave());
		}
	}

	@Nested
	class TestReset
	{
		@Test
		void forgetsWave()
		{
			waveStarts(9);
			waveCompletes();
			tracker.reset();

			assertEquals(WaveTracker.UNKNOWN, tracker.getNextWave());
		}
	}
}
