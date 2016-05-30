package cicd.sandbox.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import cicd.sandbox.dao.KvStoreDao;
import cicd.sandbox.dao.OperationLogDao;
import cicd.sandbox.entity.jpa.KeyValueStore;
import cicd.sandbox.entity.jpa.OperationLog;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/05/20
 */
@Stateless
public class SandboxServiceBean implements SandboxService {

    @Inject
    private KvStoreDao kvStoreDao;

    @Inject
    private OperationLogDao operationLogDao;

    @Override
    public KeyValueStore find(String key) {
        return kvStoreDao.find(key);
    }

    @Override
    public void create(KeyValueStore entity) {
        createOperationLog(entity.getKey(), "created");
        kvStoreDao.create(entity);
    }

    @Override
    public void update(KeyValueStore entity) {
        createOperationLog(entity.getKey(), "updated");
        KeyValueStore persisted = kvStoreDao.find(entity.getKey());
        persisted.setValue(entity.getValue());
        kvStoreDao.update(persisted);
    }

    @Override
    public void remove(String key) {
        createOperationLog(key, "removed");
        KeyValueStore entity = kvStoreDao.find(key);
        kvStoreDao.remove(entity);
    }

    private void createOperationLog(String key, String operation) {
        OperationLog entity = new OperationLog();
        entity.setOperation("KeyValueStore[" + key + "] " + operation);
        operationLogDao.create(entity);
    }

}
