package nitchcorp.backend.titan.shared.utils;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity implements Serializable {


    @CreatedBy
    @Column(length = 36, name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "date_created", updatable = false)
    @CreatedDate
    @CreationTimestamp
    private LocalDateTime dateCreated;

    @LastModifiedBy
    @Column(length = 36)
    private String updatedBy;

    @LastModifiedDate
    @UpdateTimestamp
    private LocalDateTime dateUpdated;

}