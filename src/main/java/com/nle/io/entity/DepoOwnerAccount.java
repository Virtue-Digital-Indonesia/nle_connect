package com.nle.io.entity;

import com.nle.constant.enums.AccountStatus;
import com.nle.constant.enums.ApprovalStatus;
import com.nle.constant.enums.TaxMinistryStatusEnum;
import com.nle.io.entity.common.AbstractAuditingEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static com.nle.constant.AppConstant.Pattern.EMAIL_PATTERN;

/**
 * A DepoOwnerAccount.
 */
@Entity
@Table(name = "depo_owner_account")
@Getter
@Setter
@ToString
public class DepoOwnerAccount extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Pattern(regexp = EMAIL_PATTERN)
    @Column(name = "company_email", nullable = false, unique = true)
    private String companyEmail;

    @NotNull
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "organization_code")
    private String organizationCode;

    @Column(name = "ftp_folder")
    private String ftpFolder;

    @Column(name = "ftp_password")
    private String ftpPassword;

    @Column(name = "xendit_va_id")
    private String xenditVaId;

    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @Column(name = "approval_status")
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @Column(name = "tax_ministry_status")
    @Enumerated(EnumType.STRING)
    private TaxMinistryStatusEnum taxMinistryStatusEnum;

    @Column(name = "depo_address")
    private String address;

    @OneToMany(mappedBy = "depoOwnerAccount")
    private Set<GateMove> gateMoves = new HashSet<>();

    @OneToMany(mappedBy = "depoOwnerAccount")
    private Set<FtpFile> ftpFiles = new HashSet<>();

    public boolean isActivated() {
        return AccountStatus.ACTIVE == accountStatus;
    }

}
