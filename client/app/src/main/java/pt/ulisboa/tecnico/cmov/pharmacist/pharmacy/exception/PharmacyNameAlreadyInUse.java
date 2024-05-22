package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.exception;

public class PharmacyNameAlreadyInUse extends RuntimeException{
    private String name;

    public PharmacyNameAlreadyInUse(String name) {
        super("Pharmacy name " + name + " is already in use.");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
