package cicd.sandbox.dao;

import cicd.sandbox.entity.jpa.OperationLog;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/05/30
 */
public interface OperationLogDao {

    void create(OperationLog entity);
}
