package cicd.sandbox.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import cicd.sandbox.entity.jpa.KeyValueStore;
import cicd.sandbox.exception.TargetNotFoundException;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/05/23
 */
@RunWith(Arquillian.class)
public class KvStoreDaoBeanArqTestIT {

    @Inject
    private KvStoreDao kvStoreDao;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @UsingDataSet("datasets/KvStoreDaoBeanArqTestIT/case000_find_init.yml")
    public void test_find() {
        assertThat("値１", is(kvStoreDao.find("キー１").getValue()));
    }

    @Test
    @UsingDataSet("datasets/KvStoreDaoBeanArqTestIT/case001_create_init.yml")
    public void test_create() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        KeyValueStore entity = new KeyValueStore();
        entity.setKey("key２");
        entity.setValue("value２");
        kvStoreDao.create(entity);
        entityManager.flush();

        @SuppressWarnings("unchecked")
        List<KeyValueStore> list = entityManager
                .createQuery("select kvs from KeyValueStore kvs")
                .getResultList();
        assertThat(2, is(list.size()));
        assertThat(90000001L, is(list.get(0).getId()));
        assertThat("key1", is(list.get(0).getKey()));
        assertThat("value1", is(list.get(0).getValue()));
        assertThat(sdf.parse("2016-05-24 12:00:00"),
                is(list.get(0).getModified()));
        assertThat("key２", is(list.get(1).getKey()));
        assertThat("value２", is(list.get(1).getValue()));
    }

    @Test
    @UsingDataSet("datasets/KvStoreDaoBeanArqTestIT/case002_update_init.yml")
    public void test_update() {
        KeyValueStore entity = entityManager.find(KeyValueStore.class, 1L);
        long prevId = entity.getId();
        Date prevModified = entity.getModified();
        entity.setKey("key更新");
        entity.setValue("value更新");
        kvStoreDao.update(entity);
        entityManager.flush();

        Assert.assertThat(prevId, CoreMatchers.is(entity.getId()));
        Assert.assertThat("key更新", CoreMatchers.is(entity.getKey()));
        Assert.assertThat("value更新", CoreMatchers.is(entity.getValue()));
        Assert.assertThat(prevModified,
                CoreMatchers.not(entity.getModified().getTime()));

        Query query = entityManager
                .createNativeQuery("select count(1) from kv_store");
        Assert.assertThat("2",
                CoreMatchers.is(String.valueOf(query.getSingleResult())));
    }

    @Test
    @UsingDataSet("datasets/KvStoreDaoBeanArqTestIT/case003_remove_init.yml")
    public void test_remove() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        KeyValueStore entity = entityManager.find(KeyValueStore.class, 1L);
        kvStoreDao.remove(entity);
        entityManager.flush();

        @SuppressWarnings("unchecked")
        List<KeyValueStore> list = entityManager
                .createQuery("select kvs from KeyValueStore kvs")
                .getResultList();
        assertThat(1, is(list.size()));
        assertThat(90000002L, is(list.get(0).getId()));
        assertThat("イエロー", is(list.get(0).getKey()));
        assertThat("黄色", is(list.get(0).getValue()));
        assertThat(sdf.parse("2016-05-31 00:00:01"),
                is(list.get(0).getModified()));
    }

    @Deployment
    public static EnterpriseArchive createDeployment() {
        // テスト対象JARを生成
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "test-dao.jar")
                .addClasses(KvStoreDao.class, KvStoreDaoBean.class,
                        TargetNotFoundException.class, KeyValueStore.class,
                        KvStoreDaoBeanArqTestIT.class)
                .addAsManifestResource("test-beans.xml", "beans.xml")
                .addAsManifestResource("persistence.xml");

        // EARを生成
        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class,
                "test.ear");
        ear.addAsModule(jar);

        return ear;
    }

}
