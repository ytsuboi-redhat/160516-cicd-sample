package cicd.sandbox.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import cicd.sandbox.entity.jpa.KeyValueStore;
import cicd.sandbox.service.SandboxService;
import cicd.sandbox.service.SandboxServiceBean;
import cicd.sandbox.service.exception.NotFoundException;
import cicd.sandbox.web.ws.rs.SandboxResource;
import cicd.sandbox.web.ws.rs.SandboxResourceBean;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/05/23
 */
@RunWith(Arquillian.class)
public class SandboxResourceArqTestIT {

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @UsingDataSet("datasets/case000_get_init.yml")
    public void test_get() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client
                .target("http://localhost:8080/test/rs/sandbox/キー１");
        Response response = target.request().get();

        assertThat(Response.Status.OK.getStatusCode(),
                is(response.getStatus()));
        assertThat("値１", is(response.readEntity(String.class)));
    }

    @Deployment
    public static EnterpriseArchive createDeployment() {
        // JARを生成
        JavaArchive jar = ShrinkWrap
                .create(JavaArchive.class, "test-service.jar")
                .addClasses(SandboxService.class, SandboxServiceBean.class,
                        NotFoundException.class, KeyValueStore.class)
                .addAsManifestResource("test-beans.xml", "beans.xml")
                .addAsManifestResource("persistence.xml");
        // WARを生成
        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
                .addClasses(SandboxResourceArqTestIT.class,
                        SandboxResource.class, SandboxResourceBean.class)
                .addAsWebInfResource("test-beans.xml", "beans.xml")
                .addAsWebInfResource("test-web.xml", "web.xml");
        // EARを生成
        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class,
                "test.ear");
        //ear.addAsManifestResource("test-ds.xml");
        ear.addAsModule(jar);
        ear.addAsModule(war);
        ear.setApplicationXML("test-application.xml");

        return ear;
    }

}
