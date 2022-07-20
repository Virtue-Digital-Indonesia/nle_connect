package com.nle.entity;

import com.nle.entity.common.AbstractAuditingEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "MEDIA")
@Getter
@Setter
public class Media extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MEDIA_TYPE")
    private String mediaType;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "FILE_SIZE")
    private long fileSize;

    @Column(name = "FILE_PATH")
    private String filePath;

    @ManyToOne(optional = false)
    @JoinColumn(name = "GATE_MOVE_ID", referencedColumnName = "ID")
    private GateMove gateMove;
}
