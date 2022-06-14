package org.unrecoverable.spotifydjbot;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@JsonInclude
@Data
public class BotConfig
{
	@ToString.Exclude
	private String discordToken;
	private List<String> admins;
}
