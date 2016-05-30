package cicd.sandbox.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import cicd.sandbox.dao.KvStoreDaoBean;
import cicd.sandbox.entity.jpa.KeyValueStore;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/05/23
 */
public class KvStoreDaoBeanTest {

    @SuppressWarnings("unchecked")
    @Test
    public void test_find() {
        KeyValueStore entity = new KeyValueStore();
        entity.setId(1);
        entity.setKey("test0");
        entity.setValue("value0");
        entity.setModified(new Date());

        // Rootモックを作成
        Root<KeyValueStore> root = Mockito.mock(Root.class);
        // Predicateモックの作成
        Predicate predicate = Mockito.mock(Predicate.class);
        // CriteriaBuilderモックの作成
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        Mockito.when(criteriaBuilder.equal(root.get("key"), entity.getKey()))
                .thenReturn(predicate);
        // CriteriaQueryモックの作成
        CriteriaQuery<KeyValueStore> criteriaQuery = Mockito
                .mock(CriteriaQuery.class);
        // TypedQueryモックの作成
        TypedQuery<KeyValueStore> typedQuery = Mockito.mock(TypedQuery.class);
        // EntityManagerモックの作成
        EntityManager entityManager = Mockito.mock(EntityManager.class);
        // CriteriaBuilderモックの振る舞いを定義
        Mockito.when(criteriaBuilder.createQuery(KeyValueStore.class))
                .thenReturn(criteriaQuery);
        // CriteriaQueryモックの振る舞いを定義
        Mockito.when(criteriaQuery.from(KeyValueStore.class)).thenReturn(root);
        Mockito.when(criteriaQuery.select(root)).thenReturn(criteriaQuery);
        Mockito.when(criteriaQuery.where(predicate)).thenReturn(criteriaQuery);
        // TypedQueryモックの振る舞いを定義
        Mockito.when(typedQuery.getSingleResult()).thenReturn(entity);
        // EntityManagerモックの振る舞いを定義
        Mockito.when(entityManager.getCriteriaBuilder())
                .thenReturn(criteriaBuilder);
        Mockito.when(entityManager.createQuery(criteriaQuery))
                .thenReturn(typedQuery);
        // テスト対象サービスをインスタンス化
        KvStoreDaoBean service = new KvStoreDaoBean();
        Whitebox.setInternalState(service, "entityManager", entityManager);

        KeyValueStore result = service.find(entity.getKey());

        Assert.assertThat(entity, CoreMatchers.is(result));
    }

    @Test
    public void test_create() {
        ArgumentCaptor<KeyValueStore> argumentCaptor = ArgumentCaptor
                .forClass(KeyValueStore.class);
        EntityManager entityManager = Mockito.mock(EntityManager.class);
        Mockito.doNothing().when(entityManager)
                .persist(argumentCaptor.capture());
        KvStoreDaoBean service = new KvStoreDaoBean();
        Whitebox.setInternalState(service, "entityManager", entityManager);

        KeyValueStore entity = new KeyValueStore();
        entity.setKey("test1");
        entity.setValue("value1");
        entity.setModified(new Date());

        service.create(entity);

        Assert.assertThat(entity.getKey(),
                CoreMatchers.is(argumentCaptor.getValue().getKey()));
        Assert.assertThat(entity.getValue(),
                CoreMatchers.is(argumentCaptor.getValue().getValue()));
        Assert.assertThat(entity.getModified(),
                CoreMatchers.is(argumentCaptor.getValue().getModified()));
    }

    @Test
    public void test_update() {
        ArgumentCaptor<KeyValueStore> argumentCaptor = ArgumentCaptor
                .forClass(KeyValueStore.class);
        EntityManager entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(entityManager.merge(argumentCaptor.capture()))
                .thenReturn(null);
        KvStoreDaoBean service = new KvStoreDaoBean();
        Whitebox.setInternalState(service, "entityManager", entityManager);

        KeyValueStore entity = new KeyValueStore();
        entity.setKey("test2");
        entity.setValue("value2");
        entity.setModified(new Date());

        service.update(entity);

        Assert.assertThat(entity.getKey(),
                CoreMatchers.is(argumentCaptor.getValue().getKey()));
        Assert.assertThat(entity.getValue(),
                CoreMatchers.is(argumentCaptor.getValue().getValue()));
        Assert.assertThat(entity.getModified(),
                CoreMatchers.is(argumentCaptor.getValue().getModified()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_remove() {
        KeyValueStore entity = new KeyValueStore();
        entity.setKey("test3");
        entity.setValue("value3");
        entity.setModified(new Date());

        ArgumentCaptor<KeyValueStore> argumentCaptor = ArgumentCaptor
                .forClass(KeyValueStore.class);

        // Rootモックを作成
        Root<KeyValueStore> root = Mockito.mock(Root.class);
        // Predicateモックの作成
        Predicate predicate = Mockito.mock(Predicate.class);
        // CriteriaBuilderモックの作成
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        Mockito.when(criteriaBuilder.equal(root.get("key"), entity.getKey()))
                .thenReturn(predicate);
        // CriteriaQueryモックの作成
        CriteriaQuery<KeyValueStore> criteriaQuery = Mockito
                .mock(CriteriaQuery.class);
        // TypedQueryモックの作成
        TypedQuery<KeyValueStore> typedQuery = Mockito.mock(TypedQuery.class);
        // EntityManagerモックの作成
        EntityManager entityManager = Mockito.mock(EntityManager.class);
        // CriteriaBuilderモックの振る舞いを定義
        Mockito.when(criteriaBuilder.createQuery(KeyValueStore.class))
                .thenReturn(criteriaQuery);
        // CriteriaQueryモックの振る舞いを定義
        Mockito.when(criteriaQuery.from(KeyValueStore.class)).thenReturn(root);
        Mockito.when(criteriaQuery.select(root)).thenReturn(criteriaQuery);
        Mockito.when(criteriaQuery.where(predicate)).thenReturn(criteriaQuery);
        // TypedQueryモックの振る舞いを定義
        Mockito.when(typedQuery.getSingleResult()).thenReturn(entity);
        // EntityManagerモックの振る舞いを定義
        Mockito.when(entityManager.getCriteriaBuilder())
                .thenReturn(criteriaBuilder);
        Mockito.when(entityManager.createQuery(criteriaQuery))
                .thenReturn(typedQuery);
        Mockito.doNothing().when(entityManager)
                .remove(argumentCaptor.capture());
        KvStoreDaoBean service = new KvStoreDaoBean();
        Whitebox.setInternalState(service, "entityManager", entityManager);

        service.remove(entity);

        Assert.assertThat(entity.getKey(),
                CoreMatchers.is(argumentCaptor.getValue().getKey()));
        Assert.assertThat(entity.getValue(),
                CoreMatchers.is(argumentCaptor.getValue().getValue()));
        Assert.assertThat(entity.getModified(),
                CoreMatchers.is(argumentCaptor.getValue().getModified()));
    }

}
