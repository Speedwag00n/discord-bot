package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.AbstractCommand;
import ilia.nemankov.togrofbot.commands.CommandItem;
import ilia.nemankov.togrofbot.commands.ExecutingCommand;
import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.argument.NumberArgument;
import ilia.nemankov.togrofbot.commands.parsing.matching.ArgumentsTemplate;
import ilia.nemankov.togrofbot.commands.parsing.matching.NumberArgumentMatcher;
import ilia.nemankov.togrofbot.commands.parsing.matching.StringArgumentMatcher;
import ilia.nemankov.togrofbot.database.entity.AliasEntity;
import ilia.nemankov.togrofbot.database.repository.AliasRepository;
import ilia.nemankov.togrofbot.database.repository.QuerySettings;
import ilia.nemankov.togrofbot.database.repository.impl.AliasRepositoryImpl;
import ilia.nemankov.togrofbot.database.specification.impl.AliasSpecificationByGuildId;
import ilia.nemankov.togrofbot.database.specification.impl.AliasSpecificationByNameAndGuildId;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.pagination.PaginationUtils;
import ilia.nemankov.togrofbot.util.pagination.header.impl.DefaultHeader;
import ilia.nemankov.togrofbot.util.pagination.row.Row;
import ilia.nemankov.togrofbot.util.pagination.row.impl.MarkedRow;
import net.dv8tion.jda.core.entities.impl.AbstractMessage;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Alias extends AbstractCommand implements ExecutingCommand {

    private static final Logger logger = LoggerFactory.getLogger(Alias.class);

    private static final String[] variants = new String[] {"alias", "al", "a"};

    private ListenerAdapter adapter;

    @Override
    public String[] getVariants() {
        return variants;
    }

    public Alias() {
        List<CommandItem> commandItems = new ArrayList<>();
        commandItems.add(new AliasAdd());
        commandItems.add(new AliasRemoveByName());
        commandItems.add(new AliasShowFirstPage());
        commandItems.add(new AliasShowSpecifiedPage());
        commandItems.add(new AliasExecute());
        commandItems.add(new AliasUpdate());
        setCommandItems(commandItems);
    }

    @Override
    public void subscribe(ListenerAdapter adapter) {
        this.adapter = adapter;
    }

    private class AliasAdd extends CommandItem {

        public AliasAdd() {
            super(new ArgumentsTemplate("add", new StringArgumentMatcher(), new StringArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.alias.add.args"),
                    resources.getString("description.command.alias.add.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            String name = arguments.get(1).getArgument();
            String command = arguments.get(2).getArgument();

            for (CommandItem item : getCommandItems()) {
                String determinant = item.getArgumentsTemplate().getDeterminant();
                if (determinant != null && determinant.equals(name)) {
                    return resources.getString("message.command.alias.add.invalid_name");
                }
            }
            if (command.startsWith(SettingsProvider.getInstance().getCommandPrefix())) {
                command = command.substring(1);
            }
            if (!canBeAliased(command)) {
                return resources.getString("message.command.alias.add.invalid_name");
            }
            try {
                AliasEntity entity = new AliasEntity();
                entity.setName(name);
                entity.setGuildId(event.getGuild().getIdLong());
                entity.setCommand(command);
                entity.setCreationDatetime(new Date());

                AliasRepository repository = new AliasRepositoryImpl();
                repository.addAlias(entity);

                return MessageFormat.format(
                        resources.getString("message.command.alias.add.successful"),
                        name
                );
            } catch (Throwable e) {
                if (e instanceof ConstraintViolationException) {
                    return resources.getString("message.command.alias.add.exists");
                } else {
                    logger.error("Failed to create alias", e);
                    return resources.getString("message.command.alias.add.failed");
                }
            }
        }
    }

    private class AliasExecute extends CommandItem {

        public AliasExecute() {
            super(new ArgumentsTemplate(null, new StringArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.alias.execute.args"),
                    resources.getString("description.command.alias.execute.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            String name = arguments.get(0).getArgument();

            AliasRepository repository = new AliasRepositoryImpl();
            List<AliasEntity> entities = repository.query(new AliasSpecificationByNameAndGuildId(name, event.getGuild().getIdLong()), "alias-entity");

            if (entities.isEmpty()) {
                return resources.getString("message.command.alias.not_found");
            }
            try {
                Field field = AbstractMessage.class.getDeclaredField("content");
                field.setAccessible(true);
                field.set(event.getMessage(), SettingsProvider.getInstance().getCommandPrefix() + entities.get(0).getCommand());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.error("Failed to execute alias", e);
                return resources.getString("message.command.alias.execute.failed");
            }
            if (adapter != null) {
                adapter.onGuildMessageReceived(event);
                return null;
            } else {
                logger.error("Failed to execute alias");
                return resources.getString("message.command.alias.execute.failed");
            }
        }
    }

    private class AliasRemoveByName extends CommandItem {

        public AliasRemoveByName() {
            super(new ArgumentsTemplate("remove", new StringArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.alias.remove.args"),
                    resources.getString("description.command.alias.remove.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            String name = arguments.get(1).getArgument();

            AliasEntity entity = new AliasEntity();
            entity.setName(name);
            entity.setGuildId(event.getGuild().getIdLong());

            AliasRepositoryImpl repository = new AliasRepositoryImpl();
            if (repository.removeAlias(entity) == 0) {
                return resources.getString("message.command.alias.not_found");
            } else {
                return MessageFormat.format(
                        resources.getString("message.command.alias.remove.successful"),
                        name
                );
            }
        }
    }

    private class AliasShowFirstPage extends CommandItem {

        public AliasShowFirstPage() {
            super(new ArgumentsTemplate("show"));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.alias.show_first_page.args"),
                    resources.getString("description.command.alias.show_first_page.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            int itemsOnPage = SettingsProvider.getInstance().getDefaultPageSize();
            int maxPageNumber = getMaxPageNumber(event.getGuild().getIdLong(), itemsOnPage);
            if (maxPageNumber == 0) {
                return resources.getString("message.command.alias.show.empty");
            }

            List<AliasEntity> entities = getAliasesFromDB(1, itemsOnPage, event.getGuild().getIdLong());
            List<Row> aliases = mapEntitiesToRows(entities);

            return PaginationUtils.buildPage(new DefaultHeader(1, maxPageNumber), aliases, null).toString();
        }
    }

    private class AliasShowSpecifiedPage extends CommandItem {

        public AliasShowSpecifiedPage() {
            super(new ArgumentsTemplate("show", new NumberArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.alias.show_specified_page.args"),
                    resources.getString("description.command.alias.show_specified_page.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            int page = ((NumberArgument)arguments.get(1)).getNumberArgument().intValue();

            int itemsOnPage = SettingsProvider.getInstance().getDefaultPageSize();
            int maxPageNumber = getMaxPageNumber(event.getGuild().getIdLong(), itemsOnPage);
            if (maxPageNumber == 0) {
                return resources.getString("message.command.alias.show.empty");
            }
            if (maxPageNumber < page || page <= 0) {
                return MessageFormat.format(
                        resources.getString("message.pagination.page.not_found"),
                        page
                );
            }

            List<AliasEntity> entities = getAliasesFromDB(page, itemsOnPage, event.getGuild().getIdLong());
            List<Row> aliases = mapEntitiesToRows(entities);

            return PaginationUtils.buildPage(new DefaultHeader(page, maxPageNumber), aliases, null).toString();
        }
    }

    private class AliasUpdate extends CommandItem {

        public AliasUpdate() {
            super(new ArgumentsTemplate("rename", new StringArgumentMatcher(), new StringArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.alias.update.args"),
                    resources.getString("description.command.alias.update.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            String oldName = arguments.get(1).getArgument();
            String newName = arguments.get(2).getArgument();

            AliasRepository repository = new AliasRepositoryImpl();

            try {
                int deleted = repository.updateAliasName(new AliasSpecificationByNameAndGuildId(oldName, event.getGuild().getIdLong()), newName);
                if (deleted == 0) {
                    return resources.getString("message.command.alias.update.not_found");
                } else {
                    return resources.getString("message.command.alias.update.successful");
                }
            } catch (Throwable e) {
                if (e.getCause() instanceof ConstraintViolationException) {
                    return resources.getString("message.command.alias.update.exists");
                } else {
                    System.out.println(e);
                    logger.error("Failed to update playlist", e);
                    return resources.getString("message.command.alias.update.failed");
                }
            }
        }
    }

    private boolean canBeAliased(String request) {
        for (String alias : variants) {
            if (request.startsWith(alias)) {
                return false;
            }
        }
        return true;
    }

    private String buildContent(String commandName, String commandRequest) {
        String content = "\"" + commandName + "\": " + "\"" + commandRequest + "\"";
        return content;
    }

    private int getMaxPageNumber(long guildId, int itemsOnPage) {
        if (guildId < 0 || itemsOnPage <= 0) {
            logger.error("Received invalid args: guildId={}, itemsOnPage={}", guildId, itemsOnPage);
            throw new IllegalArgumentException();
        }
        AliasRepository repository = new AliasRepositoryImpl();

        return PaginationUtils.maxPage(itemsOnPage, repository.count(new AliasSpecificationByGuildId(guildId)));
    }

    private List<AliasEntity> getAliasesFromDB(int page, int itemsOnPage, long guildId) {
        if (page <= 0 || itemsOnPage <= 0 || guildId < 0) {
            logger.error("Received invalid args: page={}, itemsOnPage={}, guildId={}", page, itemsOnPage, guildId);
            throw new IllegalArgumentException();
        }
        AliasRepository repository = new AliasRepositoryImpl();

        QuerySettings querySettings = new QuerySettings();
        querySettings.setFirstResult((page - 1) * itemsOnPage);
        querySettings.setMaxResult(itemsOnPage);

        return repository.query(new AliasSpecificationByGuildId(guildId), "alias-entity", querySettings);
    }

    private List<Row> mapEntitiesToRows(List<AliasEntity> entities) {
        return entities
                .parallelStream()
                .map(entity -> new MarkedRow(buildContent(entity.getName(), entity.getCommand())))
                .collect(Collectors.toList());
    }

}
