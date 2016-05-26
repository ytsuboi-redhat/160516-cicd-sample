package cicd.sandbox.web.ws.rs;

import javax.ejb.EJBException;
import javax.inject.Inject;

import cicd.sandbox.entity.jpa.KeyValueStore;
import cicd.sandbox.service.SandboxService;
import cicd.sandbox.service.exception.NotFoundException;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/04/26
 */
public class SandboxResourceBean implements SandboxResource {

    @Inject
    SandboxService service;

    @Override
    public String get(String key) {
        try {
            KeyValueStore entity = service.find(key);
            if (entity == null) {
                return null;
            }
            return entity.getValue();
        } catch (EJBException e) {
            if (e.getCausedByException() instanceof NotFoundException) {
                throw new javax.ws.rs.NotFoundException();
            }
            throw e;
        }
    }

    @Override
    public void post(String key, String value) {
        KeyValueStore entity = new KeyValueStore();
        entity.setKey(key);
        entity.setValue(value);
        service.create(entity);
    }

    @Override
    public void put(String key, String value) {
        try {
            KeyValueStore entity = new KeyValueStore();
            entity.setKey(key);
            entity.setValue(value);
            service.update(entity);
        } catch (EJBException e) {
            if (e.getCausedByException() instanceof NotFoundException) {
                throw new javax.ws.rs.NotFoundException();
            }
            throw e;
        }
    }

    @Override
    public void remove(String key) {
        try {
            service.remove(key);
        } catch (EJBException e) {
            if (e.getCausedByException() instanceof NotFoundException) {
                throw new javax.ws.rs.NotFoundException();
            }
            throw e;
        }
    }

}
