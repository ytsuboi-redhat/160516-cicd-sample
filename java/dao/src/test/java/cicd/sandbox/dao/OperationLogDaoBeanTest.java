package cicd.sandbox.dao;

import java.util.Date;

import javax.persistence.EntityManager;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import cicd.sandbox.dao.OperationLogDaoBean;
import cicd.sandbox.entity.jpa.OperationLog;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/05/23
 */
public class OperationLogDaoBeanTest {

    @Test
    public void test_create() {
        ArgumentCaptor<OperationLog> argumentCaptor = ArgumentCaptor
                .forClass(OperationLog.class);
        EntityManager entityManager = Mockito.mock(EntityManager.class);
        Mockito.doNothing().when(entityManager)
                .persist(argumentCaptor.capture());
        OperationLogDaoBean dao = new OperationLogDaoBean();
        Whitebox.setInternalState(dao, "entityManager", entityManager);

        OperationLog entity = new OperationLog();
        entity.setOperation("test1");
        entity.setOperated(new Date());

        dao.create(entity);

        Assert.assertThat(entity.getOperation(),
                CoreMatchers.is(argumentCaptor.getValue().getOperation()));
        Assert.assertThat(entity.getOperated(),
                CoreMatchers.is(argumentCaptor.getValue().getOperated()));
    }

}
