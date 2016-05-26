package cicd.sandbox.entity.jpa;

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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/05/20
 */
@Entity
@Table(name = "kv_store", uniqueConstraints = @UniqueConstraint(columnNames = { "key" }))
public class KeyValueStore {

    // ---------------------------------------------------- Instance Variables

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kv_store_seq_gen")
    @SequenceGenerator(name = "kv_store_seq_gen", sequenceName = "kv_store_seq")
    @Column(name = "ID", nullable = false)
    private long id;

    @NotNull
    @Column
    private String key;

    @NotNull
    @Column
    private String value;

    @NotNull
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    // ------------------------------------------------------------- Accessors

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

}
