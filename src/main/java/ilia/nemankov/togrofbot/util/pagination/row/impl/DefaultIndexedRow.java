package ilia.nemankov.togrofbot.util.pagination.row.impl;

import ilia.nemankov.togrofbot.util.pagination.row.IndexedRow;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class DefaultIndexedRow extends IndexedRow {

    public DefaultIndexedRow(String content) {
        super(content);
    }

    @Override
    public String toString() {
        return getIndex() + ") " + getContent();
    }

}
