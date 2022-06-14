package org.unrecoverable.spotifydjbot.bot;

import discord4j.core.event.domain.message.MessageCreateEvent;

public interface Command {
    void execute(MessageCreateEvent event);
    String getHelpMessage();
}