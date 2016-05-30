package cicd.sandbox.entity.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/05/30
 */
@Entity
@Table(name = "operation_log")
public class OperationLog implements Serializable {

    // ---------------------------------------------------- Instance Variables

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operation_log_seq_gen")
    @SequenceGenerator(name = "operation_log_seq_gen", sequenceName = "operation_log_seq")
    @Column(name = "ID", nullable = false)
    private long id;

    @NotNull
    @Column
    private String operation;

    @NotNull
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date operated;

    // ------------------------------------------------------------- Accessors

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Date getOperated() {
        return operated;
    }

    public void setOperated(Date operated) {
        this.operated = operated;
    }
}
