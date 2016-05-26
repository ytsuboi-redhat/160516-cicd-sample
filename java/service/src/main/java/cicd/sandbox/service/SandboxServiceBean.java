package cicd.sandbox.service;

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
import cicd.sandbox.service.exception.NotFoundException;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/05/20
 */
@Stateless
public class SandboxServiceBean implements SandboxService {

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
            throw new NotFoundException();
        }
    }

    @Override
    public void create(KeyValueStore entity) {
        entity.setModified(new Date());
        entityManager.persist(entity);
    }

    @Override
    public void update(KeyValueStore entity) {
        KeyValueStore persisted = find(entity.getKey());
        if (persisted == null) {
            throw new NotFoundException();
        }
        persisted.setValue(entity.getValue());
        persisted.setModified(new Date());
        entityManager.merge(persisted);
    }

    @Override
    public void remove(String key) {
        KeyValueStore entity = find(key);
        if (entity == null) {
            throw new NotFoundException();
        }
        entityManager.remove(entity);
    }

}
