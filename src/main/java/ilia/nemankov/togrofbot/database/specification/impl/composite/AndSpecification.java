package ilia.nemankov.togrofbot.database.specification.impl.composite;

import ilia.nemankov.togrofbot.database.specification.AbstractSpecification;
import ilia.nemankov.togrofbot.database.specification.Specification;
import lombok.AllArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
public class AndSpecification<T> extends AbstractSpecification<T> {

    private Specification<T> firstArgument;
    private Specification<T> secondArgument;

    @Override
    public boolean isSatisfiedBy(T entity) {
        return firstArgument.isSatisfiedBy(entity) && secondArgument.isSatisfiedBy(entity);
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder builder, Root<T> root) {
        return builder.and(firstArgument.getPredicate(builder, root), secondArgument.getPredicate(builder, root));
    }

    @Override
    public Class<T> getType() {
        return firstArgument.getType();
    }

}
