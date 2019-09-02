package ilia.nemankov.togrofbot;

import ilia.nemankov.togrofbot.commands.*;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;
import java.util.List;

@Slf4j
public class TogrofBot {

    public static void main(String[] args) {

        final String TOKEN = args[0];

        try {
            CommandManager commandManager = CommandManagerMainImpl.getInstance();
            List<Command> commands = commandManager.getCommands();
            CommandHandler commandHandler = CommandHandlerImpl.getInstance();
            commandHandler.initCommands(commands);

            JDABuilder builder = new JDABuilder(AccountType.BOT)
                    .setToken(TOKEN)
                    .addEventListener(commandHandler);

            JDA jda = builder.build();
        } catch (LoginException e) {
            log.error("Can't build JDA", e);
        } catch (Exception e) {
            log.error("Appeared unexpected error", e);
        }

    }

}
