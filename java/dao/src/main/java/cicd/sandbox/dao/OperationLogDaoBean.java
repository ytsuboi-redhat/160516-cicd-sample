package cicd.sandbox.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import cicd.sandbox.entity.jpa.OperationLog;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/05/30
 */
public class OperationLogDaoBean implements OperationLogDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void create(OperationLog entity) {
        entity.setOperated(new Date());
        entityManager.persist(entity);
    }

}
