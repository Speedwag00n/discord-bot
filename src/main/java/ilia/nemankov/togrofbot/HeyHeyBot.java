package ilia.nemankov.togrofbot;

import ilia.nemankov.togrofbot.commands.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

import javax.security.auth.login.LoginException;

public class HeyHeyBot {

    private static final Logger logger = LoggerFactory.getLogger(HeyHeyBot.class);

    public static void main(String[] args) {

        final String TOKEN = args[0];

        try {
            CommandManager commandManager = CommandManagerMainImpl.getInstance();
            Map<String, Command> commands = commandManager.getCommands();
            CommandHandler commandHandler = CommandHandlerImpl.getInstance();
            commandHandler.initCommands(commands);

            JDABuilder builder = new JDABuilder(AccountType.BOT)
                    .setToken(TOKEN)
                    .addEventListener(commandHandler);

            JDA jda = builder.build();
        } catch (LoginException e) {
            logger.error("Can't build JDA", e);
        } catch (Exception e) {
            logger.error("Appeared unexpected error", e);
        }

    }

}
