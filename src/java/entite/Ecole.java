/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entite;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author richard
 */
@Entity
@Table(name = "ecole")
@NamedQueries({
  @NamedQuery(name = "Ecole.findAll", query = "SELECT e FROM Ecole e"),
  @NamedQuery(name = "Ecole.findAllEcolesConsultables", query = "SELECT e FROM Ecole e WHERE e.candidaturesConsultables = 'o'"),
  @NamedQuery(name = "Ecole.findByCodeEcole", query = "SELECT e FROM Ecole e WHERE e.codeEcole = :codeEcole"),
  @NamedQuery(name = "Ecole.findByNomFr", query = "SELECT e FROM Ecole e WHERE e.nomFr = :nomFr"),
  @NamedQuery(name = "Ecole.findByNomFrAbrege", query = "SELECT e FROM Ecole e WHERE e.nomFrAbrege = :nomFrAbrege"),
  @NamedQuery(name = "Ecole.findByNomEn", query = "SELECT e FROM Ecole e WHERE e.nomEn = :nomEn"),
  @NamedQuery(name = "Ecole.findByNomEnAbrege", query = "SELECT e FROM Ecole e WHERE e.nomEnAbrege = :nomEnAbrege"),
  @NamedQuery(name = "Ecole.findByAnCimpa", query = "SELECT e FROM Ecole e WHERE e.anCimpa = :anCimpa"),
  @NamedQuery(name = "Ecole.findByPaysFr", query = "SELECT e FROM Ecole e WHERE e.paysFr = :paysFr"),
  @NamedQuery(name = "Ecole.findByPaysEn", query = "SELECT e FROM Ecole e WHERE e.paysEn = :paysEn"),
  @NamedQuery(name = "Ecole.findByRegionFr", query = "SELECT e FROM Ecole e WHERE e.regionFr = :regionFr"),
  @NamedQuery(name = "Ecole.findByRegionEn", query = "SELECT e FROM Ecole e WHERE e.regionEn = :regionEn"),
  @NamedQuery(name = "Ecole.findByThemeFr", query = "SELECT e FROM Ecole e WHERE e.themeFr = :themeFr"),
  @NamedQuery(name = "Ecole.findByThemeEn", query = "SELECT e FROM Ecole e WHERE e.themeEn = :themeEn"),
  @NamedQuery(name = "Ecole.findByTypeActivite", query = "SELECT e FROM Ecole e WHERE e.typeActivite = :typeActivite"),
  @NamedQuery(name = "Ecole.findByUrlFr", query = "SELECT e FROM Ecole e WHERE e.urlFr = :urlFr"),
  @NamedQuery(name = "Ecole.findByUrlEn", query = "SELECT e FROM Ecole e WHERE e.urlEn = :urlEn"),
  @NamedQuery(name = "Ecole.findByRegionCimpaFr", query = "SELECT e FROM Ecole e WHERE e.regionCimpaFr = :regionCimpaFr"),
  @NamedQuery(name = "Ecole.findByRegionCimpaEn", query = "SELECT e FROM Ecole e WHERE e.regionCimpaEn = :regionCimpaEn"),
  @NamedQuery(name = "Ecole.findByPaiementFraisInscription", query = "SELECT e FROM Ecole e WHERE e.paiementFraisInscription = :paiementFraisInscription"),
  @NamedQuery(name = "Ecole.findByInscriptionOuverte", query = "SELECT e FROM Ecole e WHERE e.inscriptionOuverte = :inscriptionOuverte")})
public class Ecole implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @Column(name = "code_ecole")
  private String codeEcole;
  @Basic(optional = false)
  @Column(name = "nom_fr")
  private String nomFr;
  @Basic(optional = false)
  @Column(name = "nom_fr_abrege")
  private String nomFrAbrege;
  @Basic(optional = false)
  @Column(name = "nom_en")
  private String nomEn;
  @Basic(optional = false)
  @Column(name = "nom_en_abrege")
  private String nomEnAbrege;
  @Column(name = "an_cimpa")
  private Integer anCimpa;
  @Column(name = "pays_fr")
  private String paysFr;
  @Column(name = "pays_en")
  private String paysEn;
  @Column(name = "region_fr")
  private String regionFr;
  @Column(name = "region_en")
  private String regionEn;
  @Column(name = "theme_fr")
  private String themeFr;
  @Column(name = "theme_en")
  private String themeEn;
  @Column(name = "type_activite")
  private String typeActivite;
  @Column(name = "url_fr")
  private String urlFr;
  @Column(name = "url_en")
  private String urlEn;
  @Column(name = "region_cimpa_fr")
  private String regionCimpaFr;
  @Column(name = "region_cimpa_en")
  private String regionCimpaEn;
  @Column(name = "paiement_frais_inscription")
  private String paiementFraisInscription;
  @Column(name = "inscription_ouverte")
  private Character inscriptionOuverte;
  @Column(name = "candidatures_consultables")
  private Character candidaturesConsultables;
//  @JoinTable(name = "personne_ecole", joinColumns = {
//    @JoinColumn(name = "code_ecole", referencedColumnName = "code_ecole")}, inverseJoinColumns = {
//    @JoinColumn(name = "id_personne", referencedColumnName = "id")})
//  @ManyToMany
//  private Collection<Personne> personneCollection;

  public Ecole() {
  }

  public Ecole(String codeEcole) {
    this.codeEcole = codeEcole;
  }

  public Ecole(String codeEcole, String nomFr, String nomFrAbrege, String nomEn, String nomEnAbrege) {
    this.codeEcole = codeEcole;
    this.nomFr = nomFr;
    this.nomFrAbrege = nomFrAbrege;
    this.nomEn = nomEn;
    this.nomEnAbrege = nomEnAbrege;
  }

  public String getCodeEcole() {
    return codeEcole;
  }

  public void setCodeEcole(String codeEcole) {
    this.codeEcole = codeEcole;
  }

  public String getNomFr() {
    return nomFr;
  }

  public void setNomFr(String nomFr) {
    this.nomFr = nomFr;
  }

  public String getNomFrAbrege() {
    return nomFrAbrege;
  }

  public void setNomFrAbrege(String nomFrAbrege) {
    this.nomFrAbrege = nomFrAbrege;
  }

  public String getNomEn() {
    return nomEn;
  }

  public void setNomEn(String nomEn) {
    this.nomEn = nomEn;
  }

  public String getNomEnAbrege() {
    return nomEnAbrege;
  }

  public void setNomEnAbrege(String nomEnAbrege) {
    this.nomEnAbrege = nomEnAbrege;
  }

  public Integer getAnCimpa() {
    return anCimpa;
  }

  public void setAnCimpa(Integer anCimpa) {
    this.anCimpa = anCimpa;
  }

  public String getPaysFr() {
    return paysFr;
  }

  public void setPaysFr(String paysFr) {
    this.paysFr = paysFr;
  }

  public String getPaysEn() {
    return paysEn;
  }

  public void setPaysEn(String paysEn) {
    this.paysEn = paysEn;
  }

  public String getRegionFr() {
    return regionFr;
  }

  public void setRegionFr(String regionFr) {
    this.regionFr = regionFr;
  }

  public String getRegionEn() {
    return regionEn;
  }

  public void setRegionEn(String regionEn) {
    this.regionEn = regionEn;
  }

  public String getThemeFr() {
    return themeFr;
  }

  public void setThemeFr(String themeFr) {
    this.themeFr = themeFr;
  }

  public String getThemeEn() {
    return themeEn;
  }

  public void setThemeEn(String themeEn) {
    this.themeEn = themeEn;
  }

  public String getTypeActivite() {
    return typeActivite;
  }

  public void setTypeActivite(String typeActivite) {
    this.typeActivite = typeActivite;
  }

  public String getUrlFr() {
    return urlFr;
  }

  public void setUrlFr(String urlFr) {
    this.urlFr = urlFr;
  }

  public String getUrlEn() {
    return urlEn;
  }

  public void setUrlEn(String urlEn) {
    this.urlEn = urlEn;
  }

  public String getRegionCimpaFr() {
    return regionCimpaFr;
  }

  public void setRegionCimpaFr(String regionCimpaFr) {
    this.regionCimpaFr = regionCimpaFr;
  }

  public String getRegionCimpaEn() {
    return regionCimpaEn;
  }

  public void setRegionCimpaEn(String regionCimpaEn) {
    this.regionCimpaEn = regionCimpaEn;
  }

  public String getPaiementFraisInscription() {
    return paiementFraisInscription;
  }

  public void setPaiementFraisInscription(String paiementFraisInscription) {
    this.paiementFraisInscription = paiementFraisInscription;
  }

  public Character getInscriptionOuverte() {
    return inscriptionOuverte;
  }

  public void setInscriptionOuverte(Character inscriptionOuverte) {
    this.inscriptionOuverte = inscriptionOuverte;
  }

  public Character getCandidaturesConsultables() {
    return candidaturesConsultables;
  }

  public void setCandidaturesConsultables(Character candidaturesConsultables) {
    this.candidaturesConsultables = candidaturesConsultables;
  }

//  public Collection<Personne> getPersonneCollection() {
//    return personneCollection;
//  }
//
//  public void setPersonneCollection(Collection<Personne> personneCollection) {
//    this.personneCollection = personneCollection;
//  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (codeEcole != null ? codeEcole.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Ecole)) {
      return false;
    }
    Ecole other = (Ecole) object;
    if ((this.codeEcole == null && other.codeEcole != null) || (this.codeEcole != null && !this.codeEcole.equals(other.codeEcole))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return codeEcole + ": " + (! nomFrAbrege.equals("")?nomFrAbrege:nomFr) + " - " + paysFr ;
  }
}
