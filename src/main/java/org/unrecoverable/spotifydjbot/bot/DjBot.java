package org.unrecoverable.spotifydjbot.bot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.unrecoverable.spotifydjbot.BotConfig;
import org.unrecoverable.spotifydjbot.bot.commands.DisplayVersionCommand;
import org.unrecoverable.spotifydjbot.bot.commands.HelpCommand;
import org.unrecoverable.spotifydjbot.bot.commands.QuitCommand;
import org.unrecoverable.spotifydjbot.storage.LocalDiskStore;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class DjBot implements BasicBot {
    
    private static final List<String> adminCommands = new LinkedList<>();
    private static final Map<String, Command> commands = new HashMap<>();

    private String commandPrefix = "!";

    private Map<Guild, MessageChannel> guildToReplyChannelMap = new HashMap<>();

    private Executor workerPool = Executors.newCachedThreadPool();

    @NonNull
    private BotConfig config;

    @NonNull
    private LocalDiskStore store;

    private void initializeCommands() {

        commands.put("help", new HelpCommand(this, commandPrefix, commands));
        commands.put("version", new DisplayVersionCommand(this, true));
        commands.put("about", new DisplayVersionCommand(this));
        commands.put("fuckoff", new QuitCommand(this));
        
        // TODO: Add roles for more granular permissions
        adminCommands.add("version");
        adminCommands.add("fuckoff");
    }
    
    public void run() {
        initializeCommands();

        final DiscordClient client = DiscordClient.create(config.getDiscordToken());
        // Configure Bot behavior
        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {

            // Setup callback to register all guilds with the bot.
            Mono<Void> guildRegister = gateway.on(ReadyEvent.class, event -> Mono.fromRunnable(() -> {
                GatewayDiscordClient localClient = event.getClient();
                for (ReadyEvent.Guild partialGuild : event.getGuilds()) {
                    Guild guild = localClient.getGuildById(partialGuild.getId()).block();
                    log.debug("Registered with guild: {}", guild.getName());
                }
            })).then();

            // Setup command message handler
            Mono<Void> messageCommandHandler = gateway.on(MessageCreateEvent.class, event -> Mono.fromRunnable(() -> {
                final Guild guild = event.getGuild().block();
                final Optional<Member> member = event.getMember();
                final MessageChannel channel = event.getMessage().getChannel().block();
                final String content = event.getMessage().getContent();
                final Member botMember = guild.getSelfMember().block();
                if (botMember != null && member.isPresent() && !member.get().equals(botMember)) {
                    log.trace("Processing request from {}: {}", (member.isPresent() ? member.get().getUsername() : "Unknown"), content);
                    for (final Map.Entry<String, Command> entry : commands.entrySet()) {
                        if (content.startsWith(commandPrefix + entry.getKey())) {
                            // check if the member is allowed to execute this command
                            if (adminCommands.contains(entry.getKey()) && member.isPresent() && !isPermittedToExecuteCommand(member.get())) {
                                log.warn("{} is not an admin and tried to execute command !fuckoff", member.get().getDisplayName());
                            }
                            else {
                                guildToReplyChannelMap.put(guild, channel);
                                entry.getValue().execute(event);
                            }
                            break;
                        }
                    }
                }
            })).then();

            return guildRegister.and(messageCommandHandler);
        });
        login.block();
    }


    private boolean isPermittedToExecuteCommand(Member member) {
        final String userTag = member.getTag();
        final String username = member.getUsername();
        final String userSnowflake = member.getId().asString();
        List<String> admins = config.getAdmins();
        if (admins != null && admins.contains(userTag) || admins.contains(username) || admins.contains(userSnowflake)) {

            return true;
        }
        return false;
    }

    @Override
    public void executeLater(Runnable work) {
        workerPool.execute(work);
    }

    @Override
    public boolean isAdminCommand(Command command) {
        for(Map.Entry<String, Command> commandDefinition: commands.entrySet()) {
            if (command.equals(commandDefinition.getValue())) {
                return adminCommands.contains(commandDefinition.getKey());
            }
        }
        return false;
    }

    @Override
    public MessageChannel getReplyChannel(Guild guild) {
        if (guild != null) {
            return guildToReplyChannelMap.get(guild);
        }
        return null;
    }
}
