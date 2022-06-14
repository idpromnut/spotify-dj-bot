package org.unrecoverable.spotifydjbot.bot.commands;

import org.unrecoverable.spotifydjbot.bot.BasicBot;
import org.unrecoverable.spotifydjbot.bot.Command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.MessageCreateSpec;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuitCommand extends ControllerEnabledCommand implements Command {

    public QuitCommand(@NonNull BasicBot bot) {
        super(bot);
    }

    @Override
    public void execute(MessageCreateEvent event) {
        final Member member = event.getMember().orElse(null);
        String memberName = member.getDisplayName();
        MessageChannel mc = event.getMessage().getChannel().block();
        mc.createMessage(MessageCreateSpec.builder().content("BAI FELICIA!").build()).block();

        log.info("Bot disconnect requested by user {}", memberName);
        log.info("Bot disconnecting...");
        event.getClient().logout().subscribe(e -> log.info("Bot terminated."));
    }

    @Override
    public String getHelpMessage() {
        return "tells DjBot they need to jump into traffic. Life is rough.";
    }

}
