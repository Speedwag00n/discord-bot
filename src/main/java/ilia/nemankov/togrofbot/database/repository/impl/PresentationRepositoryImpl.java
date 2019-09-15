package ilia.nemankov.togrofbot.database.repository.impl;

import ilia.nemankov.togrofbot.database.entity.PresentationEntity;
import ilia.nemankov.togrofbot.database.repository.PresentationRepository;
import ilia.nemankov.togrofbot.util.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PresentationRepositoryImpl implements PresentationRepository {

    @Override
    public void saveOrUpdatePresentation(PresentationEntity entity) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.saveOrUpdate(entity);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

}
