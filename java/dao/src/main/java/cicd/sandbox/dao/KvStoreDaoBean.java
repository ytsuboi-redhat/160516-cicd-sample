package cicd.sandbox.dao;

import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import cicd.sandbox.entity.jpa.KeyValueStore;
import cicd.sandbox.exception.TargetNotFoundException;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/05/20
 */
@Stateless
public class KvStoreDaoBean implements KvStoreDao {

    @PersistenceContext(unitName = "CicdSandboxPU")
    private EntityManager entityManager;

    @Override
    public KeyValueStore find(String key) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager
                    .getCriteriaBuilder();
            CriteriaQuery<KeyValueStore> criteriaQuery = criteriaBuilder
                    .createQuery(KeyValueStore.class);

            Root<KeyValueStore> root = criteriaQuery.from(KeyValueStore.class);
            criteriaQuery.select(root)
                    .where(criteriaBuilder.equal(root.get("key"), key));

            TypedQuery<KeyValueStore> typedQuery = entityManager
                    .createQuery(criteriaQuery);
            return typedQuery.getSingleResult();
        } catch (NoResultException e) {
            throw new TargetNotFoundException(e);
        }
    }

    @Override
    public void create(KeyValueStore entity) {
        entity.setModified(new Date());
        entityManager.persist(entity);
    }

    @Override
    public void update(KeyValueStore entity) {
        entity.setModified(new Date());
        entityManager.merge(entity);
    }

    @Override
    public void remove(KeyValueStore entity) {
        entityManager.remove(entity);
    }

}
