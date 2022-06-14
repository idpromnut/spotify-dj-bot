package org.unrecoverable.spotifydjbot.bot.commands;

import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.unrecoverable.spotifydjbot.Version;
import org.unrecoverable.spotifydjbot.bot.BasicBot;
import org.unrecoverable.spotifydjbot.bot.Command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DisplayVersionCommand extends ControllerEnabledCommand implements Command {

    private static final String ICON_FILENAME = "multiplexor_icon.png";
    private static final ClassPathResource ICON_RESOURCE_PROD = new ClassPathResource("icon/multiplexor.png");
    private static final ClassPathResource ICON_RESOURCE_STAGING = new ClassPathResource("icon/multiplexor-staging.png");
    
    private boolean extendedInfo = false;
    
    public DisplayVersionCommand(@NonNull BasicBot bot) {
        this(bot, true);
    }

    public DisplayVersionCommand(@NonNull BasicBot bot, boolean extendedInfo) {
        super(bot);
        this.extendedInfo = extendedInfo;
    }

    @Override
    public void execute(MessageCreateEvent event) {
        MessageChannel mc = event.getMessage().getChannel().block();
        EmbedCreateSpec embeddedIcon = EmbedCreateSpec.builder()
                .image("attachment://" + ICON_FILENAME)
                .build();
        try {
            mc.createMessage(MessageCreateSpec.builder()
                    .content(getVersionString())
                    .addFile(ICON_FILENAME, getIconResource().getInputStream())
                    .addEmbed(embeddedIcon)
                    .build()).block();
        }
        catch (IOException e) {
            log.error("Could not load icon", e);
        }

        
        log.info("Version: {}", Version.getVersion());
    }

    @Override
    public String getHelpMessage() {
        return "displays info about the currently running version of DjBot";
    }

    private String getVersionString() {
        if (extendedInfo) {
            return String.format("My name is multiplexor. Version %s", Version.getVersion());
        }
        else {
            return String.format("My name is multiplexor.\nWritten by idpromnut, graphics by Lynn.");
        }
    }
    
    private ClassPathResource getIconResource() {
        String versionString = Version.getVersion();
        if (versionString.toUpperCase().contains("SNAPSHOT")) {
            return ICON_RESOURCE_STAGING;
        }
        else {
            return ICON_RESOURCE_PROD;
        }
    }
}
