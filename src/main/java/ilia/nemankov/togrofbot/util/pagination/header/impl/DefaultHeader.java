package ilia.nemankov.togrofbot.util.pagination.header.impl;

import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.pagination.header.Header;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;
import java.util.ResourceBundle;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DefaultHeader extends Header {

    public DefaultHeader(int pageNumber, int maxPageNumber) {
        super(pageNumber, maxPageNumber);
    }

    @Override
    public String toString() {
        ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
        return MessageFormat.format(
                resources.getString("message.pagination.header.default"),
                getPageNumber(),
                getMaxPageNumber(),
                resources.getString("arguments.page")
        );
    }

}
