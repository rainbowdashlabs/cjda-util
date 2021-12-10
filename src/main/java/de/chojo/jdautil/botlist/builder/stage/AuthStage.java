package de.chojo.jdautil.botlist.builder.stage;

import de.chojo.jdautil.botlist.builder.BotlistBuilder;
import de.chojo.jdautil.botlist.modules.shared.AuthHandler;

public interface AuthStage {
    ConfigurationStage withAuthentication(AuthHandler authHandler);
}
