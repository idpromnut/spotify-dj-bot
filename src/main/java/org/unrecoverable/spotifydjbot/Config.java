package org.unrecoverable.spotifydjbot;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude
@Data
public class Config {
	private BotConfig botConfig;
}
