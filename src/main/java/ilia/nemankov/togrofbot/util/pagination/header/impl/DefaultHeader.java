package ilia.nemankov.togrofbot.util.pagination.header.impl;

import ilia.nemankov.togrofbot.util.pagination.header.Header;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DefaultHeader extends Header {

    @Override
    public String toString() {
        return getPageNumber() + " of " + getMaxPageNumber() + " page:";
    }

}
