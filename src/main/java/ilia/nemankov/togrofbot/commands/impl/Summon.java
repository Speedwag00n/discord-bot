package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.AbstractCommand;
import ilia.nemankov.togrofbot.commands.CommandItem;
import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.MessageUtils;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Summon extends AbstractCommand {

    private static final String[] variants = new String[] {"summon", "sum"};

    @Override
    public String[] getVariants() {
        return variants;
    }

    public Summon() {
        List<CommandItem> commandItems = new ArrayList<>();
        commandItems.add(new SummonMentioned());
        setCommandItems(commandItems);
    }

    private class SummonMentioned extends CommandItem {

        public SummonMentioned() {
            super();
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.summon.mentioned.args"),
                    resources.getString("description.command.summon.mentioned.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            List<Member> members = event.getMessage().getMentionedMembers();
            StringBuilder responseBuilder = new StringBuilder();
            GuildVoiceState voiceState = event.getMember().getVoiceState();
            if (voiceState.inVoiceChannel()) {
                responseBuilder.append(MessageFormat.format(
                        resources.getString("message.command.call.message_body_with_channel"),
                        event.getMessage().getAuthor().getAsMention(),
                        event.getGuild().getName(),
                        voiceState.getChannel().getName())
                );
            } else {
                responseBuilder.append(MessageFormat.format(
                        resources.getString("message.command.call.message_body"),
                        event.getMessage().getAuthor().getAsMention(),
                        event.getGuild().getName())
                );
            }
            String response = responseBuilder.toString();
            for (Member member : members) {
                MessageUtils.sendPrivateMessage(response, member.getUser());
            }
            return MessageFormat.format(
                    resources.getString("message.command.call.successful"),
                    members.size()
            );
        }
    }

}
