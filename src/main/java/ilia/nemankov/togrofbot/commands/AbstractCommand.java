package ilia.nemankov.togrofbot.commands;

import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Setter
@Slf4j
public abstract class AbstractCommand implements Command {

    private List<CommandItem> commandItems;

    @Override
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public List<CommandVariantDescription> getDescriptions() {
        if (commandItems != null) {
            return commandItems.stream().map(command -> command.getDescription()).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public List<CommandItem> getCommandItems() {
        return Collections.unmodifiableList(commandItems);
    }

    @Override
    public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
        ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
        SettingsProvider settingsProvider = SettingsProvider.getInstance();
        for (CommandItem item : commandItems) {
            if (item.getArgumentsTemplate() == null || item.getArgumentsTemplate().validate(arguments)) {
                log.debug("Selected {} item", item.getClass().getSimpleName());
                return item.execute(event, arguments);
            }
        }
        return MessageFormat.format(resources.getString("message.command.not_found"), settingsProvider.getCommandPrefix(), getName());
    }

}
