package de.chojo.jdautil.localization;

import de.chojo.jdautil.localization.util.Replacement;
import de.chojo.jdautil.wrapper.MessageEventWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.slf4j.ILoggerFactory;

public interface ILocalizer {
    ILocalizer DEFAULT = new ILocalizer() {
        @Override
        public ContextLocalizer getContextLocalizer(Guild guild) {
            return new ContextLocalizer(this, null);
        }

        @Override
        public ContextLocalizer getContextLocalizer(MessageEventWrapper wrapper) {
            return new ContextLocalizer(this, null);
        }

        @Override
        public ContextLocalizer getContextLocalizer(MessageChannel channel) {
            return new ContextLocalizer(this, null);
        }

        @Override
        public String localize(String message, MessageEventWrapper wrapper, Replacement... replacements) {
            return message;
        }

        @Override
        public String localize(String message, MessageChannel channel, Replacement... replacements) {
            return message;
        }

        @Override
        public String localize(String message, Replacement... replacements) {
            return message;
        }

        @Override
        public String localize(String message, Guild guild, Replacement... replacements) {
            return message;
        }
    };

    ContextLocalizer getContextLocalizer(Guild guild);

    ContextLocalizer getContextLocalizer(MessageEventWrapper wrapper);

    ContextLocalizer getContextLocalizer(MessageChannel channel);

    String localize(String message, MessageEventWrapper wrapper, Replacement... replacements);

    String localize(String message, MessageChannel channel, Replacement... replacements);

    String localize(String message, Replacement... replacements);

    String localize(String message, Guild guild, Replacement... replacements);
}
