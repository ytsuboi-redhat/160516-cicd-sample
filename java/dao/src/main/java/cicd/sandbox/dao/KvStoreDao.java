package cicd.sandbox.dao;

import javax.ejb.Local;

import cicd.sandbox.entity.jpa.KeyValueStore;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/05/20
 */
@Local
public interface KvStoreDao {

    KeyValueStore find(String key);

    void create(KeyValueStore entity);

    void update(KeyValueStore entity);

    void remove(KeyValueStore entity);

}
