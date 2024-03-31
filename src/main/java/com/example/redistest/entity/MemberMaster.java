package com.example.redistest.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Entity
@Table(name = "MEMBER_MASTER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class MemberMaster {
    @Id
    @Column(name = "MEM_SEQ", nullable = false)
    private Long memSeq;

    @Size(max = 50)
    @NotNull
    @Column(name = "MEM_ID", nullable = false, length = 50)
    private String memId;

    @NotNull
    @Column(name = "MEM_STATUS", nullable = false)
    private Character memStatus;

    @NotNull
    @Column(name = "REG_TYPE", nullable = false)
    private Character regType;

    @Size(max = 100)
    @NotNull
    @Column(name = "PASSWORD", nullable = false, length = 100)
    private String password;

    @Size(max = 50)
    @Column(name = "FIRST_NAME", length = 50)
    private String firstName;

    @Size(max = 50)
    @Column(name = "LAST_NAME", length = 50)
    private String lastName;

    @Size(max = 50)
    @Column(name = "FIRST_NAME_EN", length = 50)
    private String firstNameEn;

    @Size(max = 50)
    @Column(name = "LAST_NAME_EN", length = 50)
    private String lastNameEn;

    @Size(max = 100)
    @Column(name = "MEM_EMAIL", length = 100)
    private String memEmail;

    @Size(max = 10)
    @Column(name = "COUNTRY", length = 10)
    private String country;

    @Size(max = 10)
    @Column(name = "LANGUAGE", length = 10)
    private String language;

    @Size(max = 8)
    @Column(name = "BIRTH_DAY", length = 8)
    private String birthDay;

    @Size(max = 2)
    @Column(name = "COUNTRY_TEL_NO", length = 2)
    private String countryTelNo;

    @Size(max = 20)
    @Column(name = "TEL_NO", length = 20)
    private String telNo;

    @Size(max = 10)
    @Column(name = "EDUCATION_LEVEL", length = 10)
    private String educationLevel;

    @Size(max = 100)
    @Column(name = "SCHOOL_NAME", length = 100)
    private String schoolName;

    @Column(name = "GRADUATE_FLAG")
    private Character graduateFlag;

    @Column(name = "SCHOOL_GRADER")
    private Integer schoolGrader;

    @NotNull
    @Column(name = "GENDER", nullable = false)
    private Character gender;

    @Size(max = 10)
    @Column(name = "WEIGHT_CLASS", length = 10)
    private String weightClass;

    @Size(max = 10)
    @Column(name = "CERTIFICATION_DIV", length = 10)
    private String certificationDiv;

    @Size(max = 10)
    @Column(name = "CERTIFICATION_LEVEL", length = 10)
    private String certificationLevel;

    @Column(name = "TERM_AGREE_SERVICE")
    private Character termAgreeService;

    @Column(name = "TERM_AGREE_SERVICE_DATE")
    private LocalDateTime termAgreeServiceDate;

    @Column(name = "TERM_AGREE_LOCATION")
    private Character termAgreeLocation;

    @Column(name = "TERM_AGREE_LOCATION_DATE")
    private LocalDateTime termAgreeLocationDate;

    @Column(name = "TERM_AGREE_EVENT")
    private Character termAgreeEvent;

    @Column(name = "TERM_AGREE_EVENT_DATE")
    private LocalDateTime termAgreeEventDate;

    @Column(name = "TERM_AGREE_PERSONAL")
    private Character termAgreePersonal;

    @Column(name = "TERM_AGREE_PERSONAL_DATE")
    private LocalDateTime termAgreePersonalDate;

    @Column(name = "TERM_AGREE_PERSONAL_PERIOD")
    private Character termAgreePersonalPeriod;

    @Column(name = "TERM_AGREE_INTEGRATE")
    private Character termAgreeIntegrate;

    @Column(name = "TERM_AGREE_INTEGRATE_DATE")
    private LocalDateTime termAgreeIntegrateDate;

    @Column(name = "PHOTO_FILE_SEQ")
    private Integer photoFileSeq;

    @NotNull
    @Column(name = "CREATOR", nullable = false)
    private Integer creator;

    @Column(name = "MODIFIER")
    private Integer modifier;

    @Column(name = "REG_DATE")
    private LocalDateTime regDate;

    @Column(name = "REG_CHANNEL")
    private String regChannel;

    @Column(name = "LEAVE_DATE")
    private LocalDateTime leaveDate;

    @Column(name = "LEAVE_CHANNEL")
    private String leaveChannel;
}
