package ilia.nemankov.togrofbot.util.pagination.row.impl;

import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.pagination.row.Row;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class MarkedRow extends Row {

    private static final String defaultMarker = SettingsProvider.getInstance().getListItemSeparator();
    private String marker = defaultMarker;

    public MarkedRow(String content) {
        super(content);
    }

    @Override
    public String toString() {
        return marker + " " + getContent();
    }

}
