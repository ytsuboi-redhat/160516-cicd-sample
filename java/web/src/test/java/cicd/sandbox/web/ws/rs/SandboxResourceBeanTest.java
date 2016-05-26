package cicd.sandbox.web.ws.rs;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import cicd.sandbox.entity.jpa.KeyValueStore;
import cicd.sandbox.service.SandboxService;
import cicd.sandbox.web.ws.rs.SandboxResourceBean;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/05/20
 */
public class SandboxResourceBeanTest {

    @Test
    public void test_GET() throws Exception {
        KeyValueStore entity = new KeyValueStore();
        entity.setKey("1");
        entity.setValue("OK");
        SandboxService service = Mockito.mock(SandboxService.class);
        Mockito.when(service.find("1")).thenReturn(entity);

        SandboxResourceBean resource = new SandboxResourceBean();
        resource.service = service;

        Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
        dispatcher.getRegistry().addSingletonResource(resource);
        MockHttpRequest request = MockHttpRequest.get("/sandbox/1");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);

        Assert.assertThat(HttpServletResponse.SC_OK,
                CoreMatchers.is(response.getStatus()));
        Assert.assertThat("OK", CoreMatchers.is(response.getContentAsString()));
    }

    @Test
    public void test_POST() throws Exception {
        ArgumentCaptor<KeyValueStore> argumentCaptor = ArgumentCaptor
                .forClass(KeyValueStore.class);
        SandboxService service = Mockito.mock(SandboxService.class);
        Mockito.doNothing().when(service).create(argumentCaptor.capture());

        SandboxResourceBean resource = new SandboxResourceBean();
        resource.service = service;

        Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
        dispatcher.getRegistry().addSingletonResource(resource);
        MockHttpRequest request = MockHttpRequest.post("/sandbox/1/hoge");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);

        Assert.assertThat(HttpServletResponse.SC_NO_CONTENT,
                CoreMatchers.is(response.getStatus()));
        Assert.assertThat(StringUtils.EMPTY,
                CoreMatchers.is(response.getContentAsString()));
        Assert.assertThat("1",
                CoreMatchers.is(argumentCaptor.getValue().getKey()));
        Assert.assertThat("hoge",
                CoreMatchers.is(argumentCaptor.getValue().getValue()));
    }

    @Test
    public void test_PUT() throws Exception {
        ArgumentCaptor<KeyValueStore> argumentCaptor = ArgumentCaptor
                .forClass(KeyValueStore.class);
        SandboxService service = Mockito.mock(SandboxService.class);
        Mockito.doNothing().when(service).update(argumentCaptor.capture());

        SandboxResourceBean resource = new SandboxResourceBean();
        resource.service = service;

        Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
        dispatcher.getRegistry().addSingletonResource(resource);
        MockHttpRequest request = MockHttpRequest.put("/sandbox/foo/10a");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);

        Assert.assertThat(HttpServletResponse.SC_NO_CONTENT,
                CoreMatchers.is(response.getStatus()));
        Assert.assertThat(StringUtils.EMPTY,
                CoreMatchers.is(response.getContentAsString()));
        Assert.assertThat("foo",
                CoreMatchers.is(argumentCaptor.getValue().getKey()));
        Assert.assertThat("10a",
                CoreMatchers.is(argumentCaptor.getValue().getValue()));
    }

    @Test
    public void test_DELETE() throws Exception {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor
                .forClass(String.class);
        SandboxService service = Mockito.mock(SandboxService.class);
        Mockito.doNothing().when(service).remove(argumentCaptor.capture());

        SandboxResourceBean resource = new SandboxResourceBean();
        resource.service = service;

        Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
        dispatcher.getRegistry().addSingletonResource(resource);
        MockHttpRequest request = MockHttpRequest.delete("/sandbox/key1");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);

        Assert.assertThat(HttpServletResponse.SC_NO_CONTENT,
                CoreMatchers.is(response.getStatus()));
        Assert.assertThat(StringUtils.EMPTY,
                CoreMatchers.is(response.getContentAsString()));
        Assert.assertThat("key1", CoreMatchers.is(argumentCaptor.getValue()));
    }
}
