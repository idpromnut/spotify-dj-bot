package org.unrecoverable.spotifydjbot.bot;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.MessageChannel;

public interface BasicBot {

    MessageChannel getReplyChannel(Guild guild);
    void executeLater(Runnable work);
    boolean isAdminCommand(Command command);
}
