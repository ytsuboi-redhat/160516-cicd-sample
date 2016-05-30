package cicd.sandbox.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import cicd.sandbox.entity.jpa.KeyValueStore;
import cicd.sandbox.entity.jpa.OperationLog;
import cicd.sandbox.exception.TargetNotFoundException;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/05/30
 */
@RunWith(Arquillian.class)
public class OperationLogDaoBeanArqTestIT {

    @Inject
    private OperationLogDao operationLogDao;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @UsingDataSet("datasets/OperationLogDaoBeanArqTestIT/case000_create_init.yml")
    public void test_create() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        OperationLog entity = new OperationLog();
        entity.setOperation("操作２");
        operationLogDao.create(entity);
        entityManager.flush();

        @SuppressWarnings("unchecked")
        List<OperationLog> list = entityManager
                .createQuery("select opl from OperationLog opl")
                .getResultList();
        assertThat(2, is(list.size()));
        assertThat(90000001L, is(list.get(0).getId()));
        assertThat("ope1", is(list.get(0).getOperation()));
        assertThat(sdf.parse("2016-05-30 12:00:00"),
                is(list.get(0).getOperated()));
        assertThat("操作２", is(list.get(1).getOperation()));
    }

    @Deployment
    public static EnterpriseArchive createDeployment() {
        // テスト対象JARを生成
        JavaArchive jar = ShrinkWrap
                .create(JavaArchive.class, "test-dao.jar")
                .addClasses(KvStoreDao.class, KvStoreDaoBean.class, // TODO なぜKvStoreを含めないと動作しない？
                        KeyValueStore.class, TargetNotFoundException.class,
                        OperationLogDao.class, OperationLogDaoBean.class,
                        OperationLog.class, OperationLogDaoBeanArqTestIT.class)
                .addAsManifestResource("test-beans.xml", "beans.xml")
                .addAsManifestResource("persistence.xml");

        // EARを生成
        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class,
                "test.ear");
        ear.addAsModule(jar);

        return ear;
    }

}
