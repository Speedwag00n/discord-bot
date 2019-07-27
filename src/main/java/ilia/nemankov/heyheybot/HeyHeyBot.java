package ilia.nemankov.heyheybot;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class HeyHeyBot {

    private static final Logger logger = LoggerFactory.getLogger(HeyHeyBot.class);

    public static void main(String[] args) {

        final String TOKEN = args[0];

        try {
            JDABuilder builder = new JDABuilder(AccountType.BOT);
            builder.setToken(TOKEN);

            JDA jda = builder.build();
        } catch (LoginException e) {
            logger.error("Can't build JDA", e);
        }

    }

}
