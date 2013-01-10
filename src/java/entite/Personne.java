/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entite;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Personne qui utilise l'application. 
 * Attention, n'a rien à voir avec la classe Personne utilisée pour désigner
 * toutes les personnes connues du CIMPA, en partiulier les candidats ou les
 * conférenciers des écoles de recherche. Il faudra voir comment gérer ce
 * problème lors de la récriture des programmes de gestion des écoles
 * de recherche !
 * @author richard
 */
@Entity
@Table(name = "personne")
@NamedQueries({
  // Les écoles visibles par cette personne
  @NamedQuery(name = "Personne.findEcolesVisiblesPar", query = "SELECT e FROM Personne p join p.ecolesConsultables e where p.login = :login and e.candidaturesConsultables = 'o'"),
  @NamedQuery(name = "Personne.findAll", query = "SELECT p FROM Personne p"),
  @NamedQuery(name = "Personne.findById", query = "SELECT p FROM Personne p WHERE p.id = :id"),
  @NamedQuery(name = "Personne.findByLogin", query = "SELECT p FROM Personne p WHERE p.login = :login"),
  @NamedQuery(name = "Personne.findByMotDePasse", query = "SELECT p FROM Personne p WHERE p.motDePasse = :motDePasse"),
  @NamedQuery(name = "Personne.findByNom", query = "SELECT p FROM Personne p WHERE p.nom = :nom"),
  @NamedQuery(name = "Personne.findByPrenoms", query = "SELECT p FROM Personne p WHERE p.prenoms = :prenoms")})
public class Personne implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Integer id;
  @Basic(optional = false)
  @Column(name = "login")
  private String login;
  @Basic(optional = false)
  @Column(name = "mot_de_passe")
  private String motDePasse;
  @Basic(optional = false)
  @Column(name = "nom")
  private String nom;
  @Basic(optional = false)
  @Column(name = "prenoms")
  private String prenoms;
//  /**
//   * Les écoles consultables par cette personne.
//   */
//  @ManyToMany(mappedBy = "personneCollection")
//  private Collection<Ecole> ecolesConsultables;

    /**
   * Les écoles consultables par cette personne.
   */
  @JoinTable(name = "personne_ecole", joinColumns = {
  @JoinColumn(name = "id_personne", referencedColumnName = "id")},
  inverseJoinColumns = {
  @JoinColumn(name = "code_ecole", referencedColumnName = "code_ecole")})
  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  private List<Ecole> ecolesConsultables;

  public Personne() {
  }

  public Personne(Integer id) {
    this.id = id;
  }

  public Personne(Integer id, String login, String motDePasse, String nom, String prenoms) {
    this.id = id;
    this.login = login;
    this.motDePasse = motDePasse;
    this.nom = nom;
    this.prenoms = prenoms;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getMotDePasse() {
    return motDePasse;
  }

  public void setMotDePasse(String motDePasse) {
    this.motDePasse = motDePasse;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public String getPrenoms() {
    return prenoms;
  }

  public void setPrenoms(String prenoms) {
    this.prenoms = prenoms;
  }

     public List<Ecole> getEcolesConsultables() {
    return ecolesConsultables;
  }

  public void setEcolesConsultables(List<Ecole> ecoleList) {
    this.ecolesConsultables = ecoleList;
  }
// public Collection<Ecole> getEcoleCollection() {
//    return ecolesConsultables;
//  }
//
//  public void setEcoleCollection(Collection<Ecole> ecoleCollection) {
//    this.ecolesConsultables = ecoleCollection;
//  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Personne)) {
      return false;
    }
    Personne other = (Personne) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "entite.Personne[id=" + id + "]";
  }

}
