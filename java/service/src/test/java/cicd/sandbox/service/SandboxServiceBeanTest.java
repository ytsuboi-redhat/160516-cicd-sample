package cicd.sandbox.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import cicd.sandbox.dao.KvStoreDao;
import cicd.sandbox.dao.OperationLogDao;
import cicd.sandbox.entity.jpa.KeyValueStore;
import cicd.sandbox.entity.jpa.OperationLog;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/05/23
 */
public class SandboxServiceBeanTest {

    @Test
    public void test_find() {
        KeyValueStore entity = new KeyValueStore();
        entity.setId(1);
        entity.setKey("test0");
        entity.setValue("value0");
        entity.setModified(new Date());

        // DAOモックを作成
        KvStoreDao kvStoreDao = Mockito.mock(KvStoreDao.class);
        Mockito.when(kvStoreDao.find(entity.getKey())).thenReturn(entity);
        // テスト対象サービスをインスタンス化
        SandboxServiceBean service = new SandboxServiceBean();
        Whitebox.setInternalState(service, "kvStoreDao", kvStoreDao);

        KeyValueStore result = service.find(entity.getKey());

        Assert.assertThat(entity, CoreMatchers.is(result));
    }

    @Test
    public void test_create() {
        ArgumentCaptor<KeyValueStore> keyValueStoreCaptor = ArgumentCaptor
                .forClass(KeyValueStore.class);
        ArgumentCaptor<OperationLog> operationLogCaptor = ArgumentCaptor
                .forClass(OperationLog.class);
        KvStoreDao kvStoreDao = Mockito.mock(KvStoreDao.class);
        Mockito.doNothing().when(kvStoreDao)
                .create(keyValueStoreCaptor.capture());
        OperationLogDao operationLogDao = Mockito.mock(OperationLogDao.class);
        Mockito.doNothing().when(operationLogDao)
                .create(operationLogCaptor.capture());
        SandboxServiceBean service = new SandboxServiceBean();
        Whitebox.setInternalState(service, "kvStoreDao", kvStoreDao);
        Whitebox.setInternalState(service, "operationLogDao", operationLogDao);

        KeyValueStore entity = new KeyValueStore();
        entity.setKey("test1");
        entity.setValue("value1");
        entity.setModified(new Date());

        OperationLog operationLog = new OperationLog();
        operationLog.setOperation(
                String.format("KeyValueStore[%s] created", entity.getKey()));

        service.create(entity);

        Assert.assertThat(entity.getKey(),
                CoreMatchers.is(keyValueStoreCaptor.getValue().getKey()));
        Assert.assertThat(entity.getValue(),
                CoreMatchers.is(keyValueStoreCaptor.getValue().getValue()));
        Assert.assertThat(entity.getModified(),
                CoreMatchers.is(keyValueStoreCaptor.getValue().getModified()));
        Assert.assertThat(operationLog.getOperation(),
                CoreMatchers.is(operationLogCaptor.getValue().getOperation()));
        Assert.assertThat(operationLog.getOperated(),
                CoreMatchers.is(operationLogCaptor.getValue().getOperated()));
    }

    @Test
    public void test_update() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String key = "test2";
        String valueBefore = "value2";
        String valueAfter = "ばりゅー２";
        Date modified = sdf.parse("2016-05-30 00:00:00");

        KeyValueStore keyValueStore = new KeyValueStore();
        keyValueStore.setId(1);
        keyValueStore.setKey(key);
        keyValueStore.setValue(valueBefore);
        keyValueStore.setModified(modified);

        ArgumentCaptor<KeyValueStore> keyValueStoreCaptor = ArgumentCaptor
                .forClass(KeyValueStore.class);
        ArgumentCaptor<OperationLog> operationLogCaptor = ArgumentCaptor
                .forClass(OperationLog.class);
        KvStoreDao kvStoreDao = Mockito.mock(KvStoreDao.class);
        Mockito.when(kvStoreDao.find(key)).thenReturn(keyValueStore);
        Mockito.doNothing().when(kvStoreDao)
                .update(keyValueStoreCaptor.capture());
        OperationLogDao operationLogDao = Mockito.mock(OperationLogDao.class);
        Mockito.doNothing().when(operationLogDao)
                .create(operationLogCaptor.capture());
        SandboxServiceBean service = new SandboxServiceBean();
        Whitebox.setInternalState(service, "kvStoreDao", kvStoreDao);
        Whitebox.setInternalState(service, "operationLogDao", operationLogDao);

        keyValueStore.setValue(valueAfter);

        OperationLog operationLog = new OperationLog();
        operationLog
                .setOperation(String.format("KeyValueStore[%s] updated", key));

        service.update(keyValueStore);

        Assert.assertThat(key,
                CoreMatchers.is(keyValueStoreCaptor.getValue().getKey()));
        Assert.assertThat(valueAfter,
                CoreMatchers.is(keyValueStoreCaptor.getValue().getValue()));
        Assert.assertThat(modified,
                CoreMatchers.is(keyValueStoreCaptor.getValue().getModified()));
        Assert.assertThat(operationLog.getOperation(),
                CoreMatchers.is(operationLogCaptor.getValue().getOperation()));
        Assert.assertThat(operationLog.getOperated(),
                CoreMatchers.is(operationLogCaptor.getValue().getOperated()));
    }

    @Test
    public void test_remove() {
        KeyValueStore entity = new KeyValueStore();
        entity.setKey("test3");
        entity.setValue("value3");
        entity.setModified(new Date());

        ArgumentCaptor<KeyValueStore> keyValueStoreCaptor = ArgumentCaptor
                .forClass(KeyValueStore.class);
        ArgumentCaptor<OperationLog> operationLogCaptor = ArgumentCaptor
                .forClass(OperationLog.class);
        // DAOモックの振る舞いを定義
        KvStoreDao kvStoreDao = Mockito.mock(KvStoreDao.class);
        Mockito.when(kvStoreDao.find(entity.getKey())).thenReturn(entity);
        Mockito.doNothing().when(kvStoreDao)
                .remove(keyValueStoreCaptor.capture());
        OperationLogDao operationLogDao = Mockito.mock(OperationLogDao.class);
        Mockito.doNothing().when(operationLogDao)
                .create(operationLogCaptor.capture());
        SandboxServiceBean service = new SandboxServiceBean();
        Whitebox.setInternalState(service, "kvStoreDao", kvStoreDao);
        Whitebox.setInternalState(service, "operationLogDao", operationLogDao);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperation(
                String.format("KeyValueStore[%s] removed", entity.getKey()));

        service.remove(entity.getKey());

        Assert.assertThat(entity.getKey(),
                CoreMatchers.is(keyValueStoreCaptor.getValue().getKey()));
        Assert.assertThat(entity.getValue(),
                CoreMatchers.is(keyValueStoreCaptor.getValue().getValue()));
        Assert.assertThat(entity.getModified(),
                CoreMatchers.is(keyValueStoreCaptor.getValue().getModified()));
        Assert.assertThat(operationLog.getOperation(),
                CoreMatchers.is(operationLogCaptor.getValue().getOperation()));
        Assert.assertThat(operationLog.getOperated(),
                CoreMatchers.is(operationLogCaptor.getValue().getOperated()));
    }

}
