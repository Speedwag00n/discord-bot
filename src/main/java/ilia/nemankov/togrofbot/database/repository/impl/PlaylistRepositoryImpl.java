package ilia.nemankov.togrofbot.database.repository.impl;

import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.specification.Specification;
import ilia.nemankov.togrofbot.util.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

public class PlaylistRepositoryImpl implements PlaylistRepository {

    @Override
    public void addPlaylist(PlaylistEntity entity) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.save(entity);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public boolean removePlaylist(PlaylistEntity entity) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        Query query = session.createQuery("DELETE PlaylistEntity WHERE id = :paramId");
        query.setParameter("paramId", entity.getId());

        try {
            int deleted = query.executeUpdate();
            transaction.commit();
            return deleted != 0;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public long removePlaylists(Specification<PlaylistEntity> specification) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaDelete<PlaylistEntity> delete = builder.createCriteriaDelete(specification.getType());

        Root<PlaylistEntity> root = delete.from(specification.getType());
        delete.where(specification.getPredicate(builder, root));

        TypedQuery<Long> query = session.createQuery(delete);
        try {
            long result = query.executeUpdate();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public int updatePlaylistName(Specification specification, String name) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaUpdate<PlaylistEntity> criteria = builder.createCriteriaUpdate(PlaylistEntity.class);

        Root<PlaylistEntity> root = criteria.from(PlaylistEntity.class);
        criteria.set(root.get("name"), name).where(specification.getPredicate(builder, root));

        TypedQuery<Long> query = session.createQuery(criteria);
        try {
            int updated = query.executeUpdate();
            transaction.commit();
            return updated;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

}
