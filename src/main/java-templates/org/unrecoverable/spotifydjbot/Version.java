package org.unrecoverable.spotifydjbot;

public class Version {
    private static final String APPLICATION_VERSION = "v${project.version}";
    private static final String APPLICATION_COMMIT = "${git.branch}-${git.commit.id.abbrev}";
    private static final String BUILD_NUMBER = "${timestamp}";
    
    public static String getVersion() { return APPLICATION_VERSION + " (build: " + APPLICATION_COMMIT + ")"; }

    public static String getBuildDetails() { return APPLICATION_VERSION + " (build: " + APPLICATION_COMMIT + "-" + BUILD_NUMBER + ")"; }
}
