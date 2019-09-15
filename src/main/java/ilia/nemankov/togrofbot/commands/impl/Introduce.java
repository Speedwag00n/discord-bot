package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.AbstractCommand;
import ilia.nemankov.togrofbot.commands.CommandItem;
import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.argument.BooleanArgument;
import ilia.nemankov.togrofbot.commands.parsing.argument.NumberArgument;
import ilia.nemankov.togrofbot.commands.parsing.matching.ArgumentsTemplate;
import ilia.nemankov.togrofbot.commands.parsing.matching.BooleanArgumentMatcher;
import ilia.nemankov.togrofbot.commands.parsing.matching.NumberArgumentMatcher;
import ilia.nemankov.togrofbot.commands.parsing.matching.StringArgumentMatcher;
import ilia.nemankov.togrofbot.database.entity.PresentationEntity;
import ilia.nemankov.togrofbot.database.entity.VideoInfo;
import ilia.nemankov.togrofbot.database.repository.PresentationRepository;
import ilia.nemankov.togrofbot.database.repository.impl.PresentationRepositoryImpl;
import ilia.nemankov.togrofbot.database.specification.impl.PresentationSpecificationByGuildId;
import ilia.nemankov.togrofbot.database.specification.impl.PresentationSpecificationByUserId;
import ilia.nemankov.togrofbot.database.specification.impl.composite.AndSpecification;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.LinkUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
public class Introduce extends AbstractCommand {

    private static final String[] variants = new String[] {"introduce", "intr"};

    @Override
    public String[] getVariants() {
        return variants;
    }

    public Introduce() {
        List<CommandItem> commandItems = new ArrayList<>();
        commandItems.add(new IntroduceSetDuration());
        commandItems.add(new IntroduceSetActive());
        commandItems.add(new IntroduceSetTrack());
        commandItems.add(new IntroduceSetMessage());
        setCommandItems(commandItems);
    }

    private class IntroduceSetTrack extends CommandItem {

        public IntroduceSetTrack() {
            super(new ArgumentsTemplate("setTrack", new StringArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.introduce.set.track.args"),
                    resources.getString("description.command.introduce.set.track.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            String link = arguments.get(1).getArgument();

            PresentationEntity entity = getPresentation(event.getGuild().getIdLong(), event.getAuthor().getIdLong());
            VideoInfo info = LinkUtils.parseLink(link);
            if (info == null) {
                return resources.getString("message.command.introduce.set.track.not_found");
            }
            entity.setTitle(info.getTitle());
            entity.setIdentifier(info.getIdentifier());
            entity.setSource(info.getSource());

            PresentationRepository repository = new PresentationRepositoryImpl();
            try {
                repository.saveOrUpdatePresentation(entity);
                return resources.getString("message.command.introduce.set.track.successful");
            } catch (Throwable e) {
                log.error("Failed to set presentation track", e);
                return resources.getString("message.command.introduce.set.track.failed");
            }
        }
    }

    private class IntroduceSetDuration extends CommandItem {

        public IntroduceSetDuration() {
            super(new ArgumentsTemplate("setDuration", new NumberArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.introduce.set.duration.args"),
                    resources.getString("description.command.introduce.set.duration.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            int duration = ((NumberArgument)arguments.get(1)).getNumberArgument().intValue();

            if (duration < 0) {
                return resources.getString("description.command.introduce.set.duration.negative_duration");
            }

            PresentationEntity entity = getPresentation(event.getGuild().getIdLong(), event.getAuthor().getIdLong());
            entity.setDuration(duration);

            PresentationRepository repository = new PresentationRepositoryImpl();
            try {
                repository.saveOrUpdatePresentation(entity);
                return resources.getString("message.command.introduce.set.duration.successful");
            } catch (Throwable e) {
                log.error("Failed to set presentation duration", e);
                return resources.getString("message.command.introduce.set.duration.failed");
            }
        }
    }

    private class IntroduceSetActive extends CommandItem {

        public IntroduceSetActive() {
            super(new ArgumentsTemplate("setActive", new BooleanArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.introduce.set.active.args"),
                    resources.getString("description.command.introduce.set.active.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            boolean active = ((BooleanArgument)arguments.get(1)).getBooleanArgument().booleanValue();

            PresentationEntity entity = getPresentation(event.getGuild().getIdLong(), event.getAuthor().getIdLong());
            entity.setActive(active);

            PresentationRepository repository = new PresentationRepositoryImpl();
            try {
                repository.saveOrUpdatePresentation(entity);
                if (active) {
                    return resources.getString("message.command.introduce.set.active.enabled");
                } else {
                    return resources.getString("message.command.introduce.set.active.disabled");
                }
            } catch (Throwable e) {
                log.error("Failed to change active property for presentation", e);
                return resources.getString("message.command.introduce.set.active.failed");
            }
        }
    }

    private class IntroduceSetMessage extends CommandItem {

        public IntroduceSetMessage() {
            super(new ArgumentsTemplate("setMessage", new StringArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.introduce.set.message.args"),
                    resources.getString("description.command.introduce.set.message.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            String message = arguments.get(1).getArgument();

            PresentationEntity entity = getPresentation(event.getGuild().getIdLong(), event.getAuthor().getIdLong());
            entity.setMessage(message);

            PresentationRepository repository = new PresentationRepositoryImpl();
            try {
                repository.saveOrUpdatePresentation(entity);
                return resources.getString("message.command.introduce.set.message.successful");
            } catch (Throwable e) {
                log.error("Failed to set presentation message", e);
                return resources.getString("message.command.introduce.set.message.failed");
            }
        }
    }

    private PresentationEntity getPresentation(Long guildId, Long userId) {
        PresentationRepository repository = new PresentationRepositoryImpl();
        List<PresentationEntity> entities = repository.query(
                new AndSpecification<>(
                        new PresentationSpecificationByUserId(guildId),
                        new PresentationSpecificationByGuildId(userId)
                ),
                null
        );
        if (!entities.isEmpty()) {
            return entities.get(0);
        } else {
            PresentationEntity entity = new PresentationEntity();
            entity.setGuildId(guildId);
            entity.setUserId(userId);
            return entity;
        }
    }

}
