package ma.ensa.retrofitreservation.models;

public class Chambre {

    private Long id;
    private TypeChambre typeChambre; // Enum pour le type de chambre
    private double prix;
    private DispoChambre dispoChambre; // Enum pour la disponibilité de la chambre

    // Constructeur vide
    public Chambre() {
    }

    // Constructeur complet
    public Chambre(Long id, TypeChambre typeChambre, double prix, DispoChambre dispoChambre) {
        this.id = id;
        this.typeChambre = typeChambre;
        this.prix = prix;
        this.dispoChambre = dispoChambre;
    }

    // Getter et Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public void setTypeChambre(TypeChambre typeChambre) {
        this.typeChambre = typeChambre;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public TypeChambre getTypeChambre() {
        return typeChambre;
    }

    public DispoChambre getDispoChambre() {
        return dispoChambre;
    }

    public void setDispoChambre(DispoChambre dispoChambre) {
        this.dispoChambre = dispoChambre;
    }

    @Override
    public String toString() {
        return "Room: " + id + " ( " + typeChambre + " à "  + prix + " MAD)"; // Customize as needed
    }
}
