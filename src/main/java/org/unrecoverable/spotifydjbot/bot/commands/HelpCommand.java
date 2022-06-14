package org.unrecoverable.spotifydjbot.bot.commands;

import java.util.Map;

import org.unrecoverable.spotifydjbot.bot.BasicBot;
import org.unrecoverable.spotifydjbot.bot.Command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.MessageCreateSpec;
import lombok.NonNull;

public class HelpCommand extends ControllerEnabledCommand implements Command {

    private String commandPrefix;
    private Map<String, Command> commands;

    public HelpCommand(@NonNull BasicBot bot, String commandPrefix, Map<String, Command> commands) {
        super(bot);
        this.commandPrefix = commandPrefix;
        this.commands = commands;
    }

    @Override
    public void execute(MessageCreateEvent event) {
        StringBuilder helpBlurb = new StringBuilder();
        for(Map.Entry<String, Command> command: commands.entrySet()) {
            if (!getBot().isAdminCommand(command.getValue())) {
                helpBlurb
                .append(commandPrefix)
                .append(command.getKey())
                .append(": ")
                .append(command.getValue().getHelpMessage())
                .append("\n");
            }
        }
        MessageChannel mc = event.getMessage().getChannel().block();
        mc.createMessage(MessageCreateSpec.builder().content(helpBlurb.toString()).build()).block();
    }

    @Override
    public String getHelpMessage() {
        return "Er, you found it, this be it! Congrats, have a fucking cookie.";
    }

}
