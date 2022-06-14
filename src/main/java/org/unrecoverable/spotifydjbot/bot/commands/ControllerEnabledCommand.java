package org.unrecoverable.spotifydjbot.bot.commands;

import org.unrecoverable.spotifydjbot.bot.BasicBot;
import org.unrecoverable.spotifydjbot.bot.Command;
import org.unrecoverable.spotifydjbot.bot.DjMessageHelper;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ControllerEnabledCommand implements Command {

    @Getter
    @NonNull
    private BasicBot bot;
    
    protected DjMessageHelper messageHelper = new DjMessageHelper();
}
