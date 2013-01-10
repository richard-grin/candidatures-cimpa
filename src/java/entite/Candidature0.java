/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entite;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Candidature "brute" (pas traitée par le CIMPA) à une école de recherche.
 * Remarques : un candidat a pu candidater plusieurs fois à une même école, 
 * et meme sous 2 noms différents (faute de frappe).
 * @author richard
 */
@Entity
@Table(name = "candidature0")
@NamedQueries({
  // Ajouté pour faire afficher ces noms dans une liste déroulante à présenter à l'utilisateur
  @NamedQuery(name = "Candidature0.findNomsEcolesVisiblesPar2", query = "SELECT distinct c.titreEcole FROM Candidature0 c where c.ecole in (SELECT e FROM Personne p join p.ecolesConsultables e where p.login = :login) order by c.ecole.codeEcole"),
//  @NamedQuery(name = "Candidature0.findNomsEcolesVisiblesPar", query = "SELECT distinct c.titreEcole FROM Candidature0 c where substring(c.titreEcole, locate(':', titreEcole) in (SELECT e FROM Personne p join p.ecoles e where p.login = :login)order by c.ecole.codeEcole"),
  @NamedQuery(name = "Candidature0.findAllNomsEcoles", query = "SELECT distinct c.titreEcole FROM Candidature0 c order by c.ecole.codeEcole"),
  @NamedQuery(name = "Candidature0.findByCodeEcole", query = "SELECT c FROM Candidature0 c WHERE c.ecole.codeEcole = :codeEcole order by c.ecole.codeEcole"),
  // Candidats d'une certaine école qui ont candidaté à une autre école la même année qu'une certaine école (appelons-la E0) dont le code est passé en paramètre
  // Chaque ligne contient l'id de la candidature dans l'école E0 et le titre d'une autre école où le candidat a candidaté
  @NamedQuery(name = "Candidature0.findAutresCandidatures", query = "select distinct c0.id, e1.codeEcole, e1.nomEnAbrege, e1.paysEn from Candidature0 c0 join c0.ecole e0, Candidature0 c1 join c1.ecole e1 where e0.codeEcole = :codeEcole and c1.nom = c0.nom and c1.prenoms = c0.prenoms and e0.anCimpa = e1.anCimpa and e0.codeEcole != e1.codeEcole order by c0.id, c1.titreEcole"),
  @NamedQuery(name = "Candidature0.findAll", query = "SELECT c FROM Candidature0 c"),
  @NamedQuery(name = "Candidature0.findById", query = "SELECT c FROM Candidature0 c WHERE c.id = :id"),
  @NamedQuery(name = "Candidature0.findByIdEcole", query = "SELECT c FROM Candidature0 c WHERE c.idEcole = :idEcole"),
  @NamedQuery(name = "Candidature0.findByTitreEcole", query = "SELECT c FROM Candidature0 c WHERE c.titreEcole = :titreEcole"),
  @NamedQuery(name = "Candidature0.findByIdDossierInscription", query = "SELECT c FROM Candidature0 c WHERE c.idDossierInscription = :idDossierInscription"),
  @NamedQuery(name = "Candidature0.findByNom", query = "SELECT c FROM Candidature0 c WHERE c.nom = :nom"),
  @NamedQuery(name = "Candidature0.findByPrenoms", query = "SELECT c FROM Candidature0 c WHERE c.prenoms = :prenoms"),
  @NamedQuery(name = "Candidature0.findBySexe", query = "SELECT c FROM Candidature0 c WHERE c.sexe = :sexe"),
  @NamedQuery(name = "Candidature0.findByEmail", query = "SELECT c FROM Candidature0 c WHERE c.email = :email"),
  @NamedQuery(name = "Candidature0.findByDateNaissance", query = "SELECT c FROM Candidature0 c WHERE c.dateNaissance = :dateNaissance"),
  @NamedQuery(name = "Candidature0.findByNationalite", query = "SELECT c FROM Candidature0 c WHERE c.nationalite = :nationalite"),
  @NamedQuery(name = "Candidature0.findByLangue", query = "SELECT c FROM Candidature0 c WHERE c.langue = :langue"),
  @NamedQuery(name = "Candidature0.findByFonction", query = "SELECT c FROM Candidature0 c WHERE c.fonction = :fonction"),
  @NamedQuery(name = "Candidature0.findByDomaineRecherche", query = "SELECT c FROM Candidature0 c WHERE c.domaineRecherche = :domaineRecherche"),
  @NamedQuery(name = "Candidature0.findByPublications", query = "SELECT c FROM Candidature0 c WHERE c.publications = :publications"),
  @NamedQuery(name = "Candidature0.findByAdresseInstitution", query = "SELECT c FROM Candidature0 c WHERE c.adresseInstitution = :adresseInstitution"),
  @NamedQuery(name = "Candidature0.findByTelInstitution", query = "SELECT c FROM Candidature0 c WHERE c.telInstitution = :telInstitution"),
  @NamedQuery(name = "Candidature0.findByParticipationsAnciennes", query = "SELECT c FROM Candidature0 c WHERE c.participationsAnciennes = :participationsAnciennes"),
  @NamedQuery(name = "Candidature0.findByDemandeAide", query = "SELECT c FROM Candidature0 c WHERE c.demandeAide = :demandeAide"),
  @NamedQuery(name = "Candidature0.findByAdressePerso", query = "SELECT c FROM Candidature0 c WHERE c.adressePerso = :adressePerso"),
  @NamedQuery(name = "Candidature0.findByTelPerso", query = "SELECT c FROM Candidature0 c WHERE c.telPerso = :telPerso"),
  @NamedQuery(name = "Candidature0.findByAdresseCourrierPreferee", query = "SELECT c FROM Candidature0 c WHERE c.adresseCourrierPreferee = :adresseCourrierPreferee"),
  @NamedQuery(name = "Candidature0.findByDiplomeAcquisNom", query = "SELECT c FROM Candidature0 c WHERE c.diplomeAcquisNom = :diplomeAcquisNom"),
  @NamedQuery(name = "Candidature0.findByDiplomeAcquisDate", query = "SELECT c FROM Candidature0 c WHERE c.diplomeAcquisDate = :diplomeAcquisDate"),
  @NamedQuery(name = "Candidature0.findByDiplomeAcquisUniversite", query = "SELECT c FROM Candidature0 c WHERE c.diplomeAcquisUniversite = :diplomeAcquisUniversite"),
  @NamedQuery(name = "Candidature0.findByDiplomePrepareNom", query = "SELECT c FROM Candidature0 c WHERE c.diplomePrepareNom = :diplomePrepareNom"),
  @NamedQuery(name = "Candidature0.findByDiplomePrepareUniversite", query = "SELECT c FROM Candidature0 c WHERE c.diplomePrepareUniversite = :diplomePrepareUniversite"),
  @NamedQuery(name = "Candidature0.findBySelectionne", query = "SELECT c FROM Candidature0 c WHERE c.selectionne = :selectionne"),
  @NamedQuery(name = "Candidature0.findByConfirmation", query = "SELECT c FROM Candidature0 c WHERE c.confirmation = :confirmation"),
  @NamedQuery(name = "Candidature0.findByParticipation", query = "SELECT c FROM Candidature0 c WHERE c.participation = :participation"),
  @NamedQuery(name = "Candidature0.findByDateArrivee", query = "SELECT c FROM Candidature0 c WHERE c.dateArrivee = :dateArrivee"),
  @NamedQuery(name = "Candidature0.findByDateDepart", query = "SELECT c FROM Candidature0 c WHERE c.dateDepart = :dateDepart"),
  @NamedQuery(name = "Candidature0.findByMontantVoyageEstime", query = "SELECT c FROM Candidature0 c WHERE c.montantVoyageEstime = :montantVoyageEstime"),
  @NamedQuery(name = "Candidature0.findByFinancementExterieurDemande", query = "SELECT c FROM Candidature0 c WHERE c.financementExterieurDemande = :financementExterieurDemande"),
  @NamedQuery(name = "Candidature0.findByFinancementExterieurEu", query = "SELECT c FROM Candidature0 c WHERE c.financementExterieurEu = :financementExterieurEu"),
  @NamedQuery(name = "Candidature0.findByDescFinancementExterieur", query = "SELECT c FROM Candidature0 c WHERE c.descFinancementExterieur = :descFinancementExterieur"),
  @NamedQuery(name = "Candidature0.findByFinancExtVoyageMontant", query = "SELECT c FROM Candidature0 c WHERE c.financExtVoyageMontant = :financExtVoyageMontant"),
  @NamedQuery(name = "Candidature0.findByFinancExtVoyagePar", query = "SELECT c FROM Candidature0 c WHERE c.financExtVoyagePar = :financExtVoyagePar"),
  @NamedQuery(name = "Candidature0.findByFinancExtVoyageEu", query = "SELECT c FROM Candidature0 c WHERE c.financExtVoyageEu = :financExtVoyageEu"),
  @NamedQuery(name = "Candidature0.findByFinancExtSejourMontant", query = "SELECT c FROM Candidature0 c WHERE c.financExtSejourMontant = :financExtSejourMontant"),
  @NamedQuery(name = "Candidature0.findByFinancExtSejourPar", query = "SELECT c FROM Candidature0 c WHERE c.financExtSejourPar = :financExtSejourPar"),
  @NamedQuery(name = "Candidature0.findByFinancExtSejourEu", query = "SELECT c FROM Candidature0 c WHERE c.financExtSejourEu = :financExtSejourEu"),
  @NamedQuery(name = "Candidature0.findByFinancExtInscripMontant", query = "SELECT c FROM Candidature0 c WHERE c.financExtInscripMontant = :financExtInscripMontant"),
  @NamedQuery(name = "Candidature0.findByFinancExtInscripPar", query = "SELECT c FROM Candidature0 c WHERE c.financExtInscripPar = :financExtInscripPar"),
  @NamedQuery(name = "Candidature0.findByFinancExtInscripEu", query = "SELECT c FROM Candidature0 c WHERE c.financExtInscripEu = :financExtInscripEu"),
  @NamedQuery(name = "Candidature0.findByMontantDemandeVoyage", query = "SELECT c FROM Candidature0 c WHERE c.montantDemandeVoyage = :montantDemandeVoyage"),
  @NamedQuery(name = "Candidature0.findByFraisInscriptionPeutPayer", query = "SELECT c FROM Candidature0 c WHERE c.fraisInscriptionPeutPayer = :fraisInscriptionPeutPayer"),
  @NamedQuery(name = "Candidature0.findByRaisonDemandeExemption", query = "SELECT c FROM Candidature0 c WHERE c.raisonDemandeExemption = :raisonDemandeExemption"),
  @NamedQuery(name = "Candidature0.findByMontantDemandeInscription", query = "SELECT c FROM Candidature0 c WHERE c.montantDemandeInscription = :montantDemandeInscription"),
  @NamedQuery(name = "Candidature0.findByTypeDemandeSejour", query = "SELECT c FROM Candidature0 c WHERE c.typeDemandeSejour = :typeDemandeSejour"),
  @NamedQuery(name = "Candidature0.findByMontantDejaEuPourSejour", query = "SELECT c FROM Candidature0 c WHERE c.montantDejaEuPourSejour = :montantDejaEuPourSejour"),
  @NamedQuery(name = "Candidature0.findByTypePriseEnChargeSejour", query = "SELECT c FROM Candidature0 c WHERE c.typePriseEnChargeSejour = :typePriseEnChargeSejour"),
  @NamedQuery(name = "Candidature0.findByMontantSejourParCandidat", query = "SELECT c FROM Candidature0 c WHERE c.montantSejourParCandidat = :montantSejourParCandidat"),
  @NamedQuery(name = "Candidature0.findByTypeDispenseFraisInscrip", query = "SELECT c FROM Candidature0 c WHERE c.typeDispenseFraisInscrip = :typeDispenseFraisInscrip"),
  @NamedQuery(name = "Candidature0.findByMontantFraisInscripAPayer", query = "SELECT c FROM Candidature0 c WHERE c.montantFraisInscripAPayer = :montantFraisInscripAPayer"),
  @NamedQuery(name = "Candidature0.findByMontantAideVoyage", query = "SELECT c FROM Candidature0 c WHERE c.montantAideVoyage = :montantAideVoyage"),
  @NamedQuery(name = "Candidature0.findByVeutRecevoirPgmCimpa", query = "SELECT c FROM Candidature0 c WHERE c.veutRecevoirPgmCimpa = :veutRecevoirPgmCimpa"),
  @NamedQuery(name = "Candidature0.findByLettreMotivation", query = "SELECT c FROM Candidature0 c WHERE c.lettreMotivation = :lettreMotivation"),
  @NamedQuery(name = "Candidature0.findByCv", query = "SELECT c FROM Candidature0 c WHERE c.cv = :cv"),
  @NamedQuery(name = "Candidature0.findByNbLettresRecommandation", query = "SELECT c FROM Candidature0 c WHERE c.nbLettresRecommandation = :nbLettresRecommandation"),
  @NamedQuery(name = "Candidature0.findByLettresRecommandation", query = "SELECT c FROM Candidature0 c WHERE c.lettresRecommandation = :lettresRecommandation"),
  @NamedQuery(name = "Candidature0.findByPhotocopiePasseport", query = "SELECT c FROM Candidature0 c WHERE c.photocopiePasseport = :photocopiePasseport"),
  @NamedQuery(name = "Candidature0.findByJustificatifsAutresDemandes", query = "SELECT c FROM Candidature0 c WHERE c.justificatifsAutresDemandes = :justificatifsAutresDemandes"),
  @NamedQuery(name = "Candidature0.findByRemarques", query = "SELECT c FROM Candidature0 c WHERE c.remarques = :remarques"),
  @NamedQuery(name = "Candidature0.findByVersion", query = "SELECT c FROM Candidature0 c WHERE c.version = :version"),
  @NamedQuery(name = "Candidature0.findByDateCreation", query = "SELECT c FROM Candidature0 c WHERE c.dateCreation = :dateCreation"),
  @NamedQuery(name = "Candidature0.findByDateModification", query = "SELECT c FROM Candidature0 c WHERE c.dateModification = :dateModification"),
  @NamedQuery(name = "Candidature0.findByOperateurModif", query = "SELECT c FROM Candidature0 c WHERE c.operateurModif = :operateurModif")})
public class Candidature0 implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Integer id;
  @Column(name = "id_ecole")
  private Integer idEcole;

  @ManyToOne
  @JoinColumn(name = "code_ecole")
  private Ecole ecole;

  @Basic(optional = false)
  @Column(name = "titre_ecole")
  private String titreEcole;
  @Column(name = "id_dossier_inscription")
  private Integer idDossierInscription;
  @Basic(optional = false)
  @Column(name = "nom")
  private String nom;
  @Column(name = "prenoms")
  private String prenoms;
  @Column(name = "sexe")
  private Character sexe;
  @Column(name = "email")
  private String email;
  @Column(name = "date_naissance")
  private String dateNaissance;
  @Column(name = "nationalite")
  private String nationalite;
  @Column(name = "langue")
  private String langue;
  @Column(name = "fonction")
  private String fonction;
  @Column(name = "domaine_recherche")
  private String domaineRecherche;
  @Column(name = "publications")
  private String publications;
  @Column(name = "adresse_institution")
  private String adresseInstitution;

  @Column(name = "pays_institution")
  private String paysInstitution;

  @Column(name = "tel_institution")
  private String telInstitution;
  @Column(name = "participations_anciennes")
  private String participationsAnciennes;
  @Column(name = "demande_aide")
  private Character demandeAide;
  @Column(name = "adresse_perso")
  private String adressePerso;
  @Column(name = "tel_perso")
  private String telPerso;
  @Column(name = "adresse_courrier_preferee")
  private Character adresseCourrierPreferee;
  @Column(name = "diplome_acquis_nom")
  private String diplomeAcquisNom;
  @Column(name = "diplome_acquis_date")
  private String diplomeAcquisDate;
  @Column(name = "diplome_acquis_universite")
  private String diplomeAcquisUniversite;
  @Column(name = "diplome_prepare_nom")
  private String diplomePrepareNom;
  @Column(name = "diplome_prepare_universite")
  private String diplomePrepareUniversite;
  @Column(name = "selectionne")
  private Character selectionne;
  @Column(name = "confirmation")
  private Character confirmation;
  @Column(name = "participation")
  private Character participation;
  @Column(name = "date_arrivee")
  @Temporal(TemporalType.DATE)
  private Date dateArrivee;
  @Column(name = "date_depart")
  @Temporal(TemporalType.DATE)
  private Date dateDepart;
  @Column(name = "montant_voyage_estime")
  private String montantVoyageEstime;
  @Column(name = "financement_exterieur_demande")
  private String financementExterieurDemande;
  @Column(name = "financement_exterieur_eu")
  private String financementExterieurEu;
  @Column(name = "desc_financement_exterieur")
  private String descFinancementExterieur;
  @Column(name = "financ_ext_voyage_montant")
  private String financExtVoyageMontant;
  @Column(name = "financ_ext_voyage_par")
  private String financExtVoyagePar;
  @Column(name = "financ_ext_voyage_eu")
  private Character financExtVoyageEu;
  @Column(name = "financ_ext_sejour_montant")
  private String financExtSejourMontant;
  @Column(name = "financ_ext_sejour_par")
  private String financExtSejourPar;
  @Column(name = "financ_ext_sejour_eu")
  private Character financExtSejourEu;
  @Column(name = "financ_ext_inscrip_montant")
  private String financExtInscripMontant;
  @Column(name = "financ_ext_inscrip_par")
  private String financExtInscripPar;
  @Column(name = "financ_ext_inscrip_eu")
  private Character financExtInscripEu;
  @Column(name = "montant_demande_voyage")
  private String montantDemandeVoyage;
  @Column(name = "frais_inscription_peut_payer")
  private String fraisInscriptionPeutPayer;
  @Column(name = "raison_demande_exemption")
  private String raisonDemandeExemption;
  @Column(name = "montant_demande_inscription")
  private String montantDemandeInscription;
  @Column(name = "type_demande_sejour")
  private Character typeDemandeSejour;
  @Column(name = "montant_deja_eu_pour_sejour")
  private String montantDejaEuPourSejour;
  @Column(name = "type_prise_en_charge_sejour")
  private Character typePriseEnChargeSejour;
  @Column(name = "montant_sejour_par_candidat")
  private String montantSejourParCandidat;
  @Column(name = "type_dispense_frais_inscrip")
  private Character typeDispenseFraisInscrip;
  @Column(name = "montant_frais_inscrip_a_payer")
  private String montantFraisInscripAPayer;
  @Column(name = "montant_aide_voyage")
  private String montantAideVoyage;
  @Basic(optional = false)
  @Column(name = "veut_recevoir_pgm_cimpa")
  private char veutRecevoirPgmCimpa;
  @Column(name = "lettre_motivation")
  private String lettreMotivation;
  @Lob
//    @Basic(fetch=FetchType.LAZY)
  @Column(name = "lettre_motivation_pdf")
  private byte[] lettreMotivationPdf;
  @Column(name = "cv")
  private String cv;
  @Lob
//    @Basic(fetch=FetchType.LAZY)
  @Column(name = "cv_pdf")
  private byte[] cvPdf;
  @Column(name = "nb_lettres_recommandation")
  private Integer nbLettresRecommandation;
  @Column(name = "lettres_recommandation")
  private String lettresRecommandation;
  @Lob
//    @Basic(fetch=FetchType.LAZY)
  @Column(name = "lettres_recommandation_pdf")
  private byte[] lettresRecommandationPdf;
  @Column(name = "photocopie_passeport")
  private Character photocopiePasseport;
  @Column(name = "justificatifs_autres_demandes")
  private Character justificatifsAutresDemandes;
  @Column(name = "remarques")
  private String remarques;
  @Column(name = "version")
  private Integer version;
  @Column(name = "date_creation")
  @Temporal(TemporalType.TIMESTAMP)
  private Date dateCreation;
  @Column(name = "date_modification")
  @Temporal(TemporalType.TIMESTAMP)
  private Date dateModification;
  @Column(name = "operateur_modif")
  private String operateurModif;

  public Candidature0() {
  }

  public Candidature0(Integer id) {
    this.id = id;
  }

  public Candidature0(Integer id, Ecole ecole, String titreEcole, String nom, char veutRecevoirPgmCimpa) {
    this.id = id;
    this.ecole = ecole;
    this.titreEcole = titreEcole;
    this.nom = nom;
    this.veutRecevoirPgmCimpa = veutRecevoirPgmCimpa;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getIdEcole() {
    return idEcole;
  }

  public void setIdEcole(Integer idEcole) {
    this.idEcole = idEcole;
  }

  public Ecole getEcole() {
    return ecole;
  }

  public void setEcole(Ecole ecole) {
    this.ecole = ecole;
  }

  public String getTitreEcole() {
    return titreEcole;
  }

  public void setTitreEcole(String titreEcole) {
    this.titreEcole = titreEcole;
  }

  public Integer getIdDossierInscription() {
    return idDossierInscription;
  }

  public void setIdDossierInscription(Integer idDossierInscription) {
    this.idDossierInscription = idDossierInscription;
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

  public Character getSexe() {
    return sexe;
  }

  public void setSexe(Character sexe) {
    this.sexe = sexe;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getDateNaissance() {
    return dateNaissance;
  }

  public void setDateNaissance(String dateNaissance) {
    this.dateNaissance = dateNaissance;
  }

  public String getNationalite() {
    return nationalite;
  }

  public void setNationalite(String nationalite) {
    this.nationalite = nationalite;
  }

  public String getPaysInstitution() {
    return paysInstitution;
  }

  public void setPaysInstitution(String paysInstitution) {
    this.paysInstitution = paysInstitution;
  }

  public String getLangue() {
    return langue;
  }

  public void setLangue(String langue) {
    this.langue = langue;
  }

  public String getFonction() {
    return fonction;
  }

  public void setFonction(String fonction) {
    this.fonction = fonction;
  }

  public String getDomaineRecherche() {
    return domaineRecherche;
  }

  public void setDomaineRecherche(String domaineRecherche) {
    this.domaineRecherche = domaineRecherche;
  }

  public String getPublications() {
    return publications;
  }

  public void setPublications(String publications) {
    this.publications = publications;
  }

  public String getAdresseInstitution() {
    return adresseInstitution;
  }

  public void setAdresseInstitution(String adresseInstitution) {
    this.adresseInstitution = adresseInstitution;
  }

  public String getTelInstitution() {
    return telInstitution;
  }

  public void setTelInstitution(String telInstitution) {
    this.telInstitution = telInstitution;
  }

  public String getParticipationsAnciennes() {
    return participationsAnciennes;
  }

  public void setParticipationsAnciennes(String participationsAnciennes) {
    this.participationsAnciennes = participationsAnciennes;
  }

  public Character getDemandeAide() {
    return demandeAide;
  }

  public void setDemandeAide(Character demandeAide) {
    this.demandeAide = demandeAide;
  }

  public String getAdressePerso() {
    return adressePerso;
  }

  public void setAdressePerso(String adressePerso) {
    this.adressePerso = adressePerso;
  }

  public String getTelPerso() {
    return telPerso;
  }

  public void setTelPerso(String telPerso) {
    this.telPerso = telPerso;
  }

  public Character getAdresseCourrierPreferee() {
    return adresseCourrierPreferee;
  }

  public void setAdresseCourrierPreferee(Character adresseCourrierPreferee) {
    this.adresseCourrierPreferee = adresseCourrierPreferee;
  }

  public String getDiplomeAcquisNom() {
    return diplomeAcquisNom;
  }

  public void setDiplomeAcquisNom(String diplomeAcquisNom) {
    this.diplomeAcquisNom = diplomeAcquisNom;
  }

  public String getDiplomeAcquisDate() {
    return diplomeAcquisDate;
  }

  public void setDiplomeAcquisDate(String diplomeAcquisDate) {
    this.diplomeAcquisDate = diplomeAcquisDate;
  }

  public String getDiplomeAcquisUniversite() {
    return diplomeAcquisUniversite;
  }

  public void setDiplomeAcquisUniversite(String diplomeAcquisUniversite) {
    this.diplomeAcquisUniversite = diplomeAcquisUniversite;
  }

  public String getDiplomePrepareNom() {
    return diplomePrepareNom;
  }

  public void setDiplomePrepareNom(String diplomePrepareNom) {
    this.diplomePrepareNom = diplomePrepareNom;
  }

  public String getDiplomePrepareUniversite() {
    return diplomePrepareUniversite;
  }

  public void setDiplomePrepareUniversite(String diplomePrepareUniversite) {
    this.diplomePrepareUniversite = diplomePrepareUniversite;
  }

  public Character getSelectionne() {
    return selectionne;
  }

  public void setSelectionne(Character selectionne) {
    this.selectionne = selectionne;
  }

  public Character getConfirmation() {
    return confirmation;
  }

  public void setConfirmation(Character confirmation) {
    this.confirmation = confirmation;
  }

  public Character getParticipation() {
    return participation;
  }

  public void setParticipation(Character participation) {
    this.participation = participation;
  }

  public Date getDateArrivee() {
    return dateArrivee;
  }

  public void setDateArrivee(Date dateArrivee) {
    this.dateArrivee = dateArrivee;
  }

  public Date getDateDepart() {
    return dateDepart;
  }

  public void setDateDepart(Date dateDepart) {
    this.dateDepart = dateDepart;
  }

  public String getMontantVoyageEstime() {
    return montantVoyageEstime;
  }

  public void setMontantVoyageEstime(String montantVoyageEstime) {
    this.montantVoyageEstime = montantVoyageEstime;
  }

  public String getFinancementExterieurDemande() {
    return financementExterieurDemande;
  }

  public void setFinancementExterieurDemande(String financementExterieurDemande) {
    this.financementExterieurDemande = financementExterieurDemande;
  }

  public String getFinancementExterieurEu() {
    return financementExterieurEu;
  }

  public void setFinancementExterieurEu(String financementExterieurEu) {
    this.financementExterieurEu = financementExterieurEu;
  }

  public String getDescFinancementExterieur() {
    return descFinancementExterieur;
  }

  public void setDescFinancementExterieur(String descFinancementExterieur) {
    this.descFinancementExterieur = descFinancementExterieur;
  }

  public String getFinancExtVoyageMontant() {
    return financExtVoyageMontant;
  }

  public void setFinancExtVoyageMontant(String financExtVoyageMontant) {
    this.financExtVoyageMontant = financExtVoyageMontant;
  }

  public String getFinancExtVoyagePar() {
    return financExtVoyagePar;
  }

  public void setFinancExtVoyagePar(String financExtVoyagePar) {
    this.financExtVoyagePar = financExtVoyagePar;
  }

  public Character getFinancExtVoyageEu() {
    return financExtVoyageEu;
  }

  public void setFinancExtVoyageEu(Character financExtVoyageEu) {
    this.financExtVoyageEu = financExtVoyageEu;
  }

  public String getFinancExtSejourMontant() {
    return financExtSejourMontant;
  }

  public void setFinancExtSejourMontant(String financExtSejourMontant) {
    this.financExtSejourMontant = financExtSejourMontant;
  }

  public String getFinancExtSejourPar() {
    return financExtSejourPar;
  }

  public void setFinancExtSejourPar(String financExtSejourPar) {
    this.financExtSejourPar = financExtSejourPar;
  }

  public Character getFinancExtSejourEu() {
    return financExtSejourEu;
  }

  public void setFinancExtSejourEu(Character financExtSejourEu) {
    this.financExtSejourEu = financExtSejourEu;
  }

  public String getFinancExtInscripMontant() {
    return financExtInscripMontant;
  }

  public void setFinancExtInscripMontant(String financExtInscripMontant) {
    this.financExtInscripMontant = financExtInscripMontant;
  }

  public String getFinancExtInscripPar() {
    return financExtInscripPar;
  }

  public void setFinancExtInscripPar(String financExtInscripPar) {
    this.financExtInscripPar = financExtInscripPar;
  }

  public Character getFinancExtInscripEu() {
    return financExtInscripEu;
  }

  public void setFinancExtInscripEu(Character financExtInscripEu) {
    this.financExtInscripEu = financExtInscripEu;
  }

  public String getMontantDemandeVoyage() {
    return montantDemandeVoyage;
  }

  public void setMontantDemandeVoyage(String montantDemandeVoyage) {
    this.montantDemandeVoyage = montantDemandeVoyage;
  }

  public String getFraisInscriptionPeutPayer() {
    return fraisInscriptionPeutPayer;
  }

  public void setFraisInscriptionPeutPayer(String fraisInscriptionPeutPayer) {
    this.fraisInscriptionPeutPayer = fraisInscriptionPeutPayer;
  }

  public String getRaisonDemandeExemption() {
    return raisonDemandeExemption;
  }

  public void setRaisonDemandeExemption(String raisonDemandeExemption) {
    this.raisonDemandeExemption = raisonDemandeExemption;
  }

  public String getMontantDemandeInscription() {
    return montantDemandeInscription;
  }

  public void setMontantDemandeInscription(String montantDemandeInscription) {
    this.montantDemandeInscription = montantDemandeInscription;
  }

  public Character getTypeDemandeSejour() {
    return typeDemandeSejour;
  }

  public void setTypeDemandeSejour(Character typeDemandeSejour) {
    this.typeDemandeSejour = typeDemandeSejour;
  }

  public String getMontantDejaEuPourSejour() {
    return montantDejaEuPourSejour;
  }

  public void setMontantDejaEuPourSejour(String montantDejaEuPourSejour) {
    this.montantDejaEuPourSejour = montantDejaEuPourSejour;
  }

  public Character getTypePriseEnChargeSejour() {
    return typePriseEnChargeSejour;
  }

  public void setTypePriseEnChargeSejour(Character typePriseEnChargeSejour) {
    this.typePriseEnChargeSejour = typePriseEnChargeSejour;
  }

  public String getMontantSejourParCandidat() {
    return montantSejourParCandidat;
  }

  public void setMontantSejourParCandidat(String montantSejourParCandidat) {
    this.montantSejourParCandidat = montantSejourParCandidat;
  }

  public Character getTypeDispenseFraisInscrip() {
    return typeDispenseFraisInscrip;
  }

  public void setTypeDispenseFraisInscrip(Character typeDispenseFraisInscrip) {
    this.typeDispenseFraisInscrip = typeDispenseFraisInscrip;
  }

  public String getMontantFraisInscripAPayer() {
    return montantFraisInscripAPayer;
  }

  public void setMontantFraisInscripAPayer(String montantFraisInscripAPayer) {
    this.montantFraisInscripAPayer = montantFraisInscripAPayer;
  }

  public String getMontantAideVoyage() {
    return montantAideVoyage;
  }

  public void setMontantAideVoyage(String montantAideVoyage) {
    this.montantAideVoyage = montantAideVoyage;
  }

  public char getVeutRecevoirPgmCimpa() {
    return veutRecevoirPgmCimpa;
  }

  public void setVeutRecevoirPgmCimpa(char veutRecevoirPgmCimpa) {
    this.veutRecevoirPgmCimpa = veutRecevoirPgmCimpa;
  }

  public String getLettreMotivation() {
    return lettreMotivation;
  }

  public void setLettreMotivation(String lettreMotivation) {
    this.lettreMotivation = lettreMotivation;
  }

  public byte[] getLettreMotivationPdf() {
    return lettreMotivationPdf;
  }

  public void setLettreMotivationPdf(byte[] lettreMotivationPdf) {
    this.lettreMotivationPdf = lettreMotivationPdf;
  }

  public String getCv() {
    return cv;
  }

  public void setCv(String cv) {
    this.cv = cv;
  }

  public byte[] getCvPdf() {
    return cvPdf;
  }

  public void setCvPdf(byte[] cvPdf) {
    this.cvPdf = cvPdf;
  }

  public Integer getNbLettresRecommandation() {
    return nbLettresRecommandation;
  }

  public void setNbLettresRecommandation(Integer nbLettresRecommandation) {
    this.nbLettresRecommandation = nbLettresRecommandation;
  }

  public String getLettresRecommandation() {
    return lettresRecommandation;
  }

  public void setLettresRecommandation(String lettresRecommandation) {
    this.lettresRecommandation = lettresRecommandation;
  }

  public byte[] getLettresRecommandationPdf() {
    return lettresRecommandationPdf;
  }

  public void setLettresRecommandationPdf(byte[] lettresRecommandationPdf) {
    this.lettresRecommandationPdf = lettresRecommandationPdf;
  }

  public Character getPhotocopiePasseport() {
    return photocopiePasseport;
  }

  public void setPhotocopiePasseport(Character photocopiePasseport) {
    this.photocopiePasseport = photocopiePasseport;
  }

  public Character getJustificatifsAutresDemandes() {
    return justificatifsAutresDemandes;
  }

  public void setJustificatifsAutresDemandes(Character justificatifsAutresDemandes) {
    this.justificatifsAutresDemandes = justificatifsAutresDemandes;
  }

  public String getRemarques() {
    return remarques;
  }

  public void setRemarques(String remarques) {
    this.remarques = remarques;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public Date getDateCreation() {
    return dateCreation;
  }

  public void setDateCreation(Date dateCreation) {
    this.dateCreation = dateCreation;
  }

  public Date getDateModification() {
    return dateModification;
  }

  public void setDateModification(Date dateModification) {
    this.dateModification = dateModification;
  }

  public String getOperateurModif() {
    return operateurModif;
  }

  public void setOperateurModif(String operateurModif) {
    this.operateurModif = operateurModif;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Candidature0)) {
      return false;
    }
    Candidature0 other = (Candidature0) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "entite.Candidature0[id=" + id + "]";
  }
}
