package com.travelPlanWithAccounting.service.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Valid
@Data
@SuperBuilder
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "member")
public class Member extends BaseEntity {

  @Column(name = "status")
  private Short status;

  @Column(name = "given_name")
  private String givenName;

  @Column(name = "family_name")
  private String familyName;

  @Column(name = "nick_name")
  private String nickName;

  @Column(name = "birthday")
  private LocalDate birthday;

  @Column(name = "subscribe")
  private Boolean subscribe;

  @Column(name = "email")
  private String email;

  @Column(name = "mobile")
  private String mobile;

  @Column(name = "lang_type")
  private String langType;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  @ToString.Exclude
  private List<MemberLog> logs;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  @ToString.Exclude
  private List<MemberSubscribe> memberSubscribes;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  @ToString.Exclude
  private List<AuthInfo> authInfos;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  @ToString.Exclude
  private List<FavoriteInfo> favoriteInfos;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  @ToString.Exclude
  private List<TravelFav> travelFavs;
}
