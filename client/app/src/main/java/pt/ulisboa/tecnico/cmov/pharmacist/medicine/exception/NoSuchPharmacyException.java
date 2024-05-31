package pt.ulisboa.tecnico.cmov.pharmacist.medicine.exception;

public class NoSuchPharmacyException extends RuntimeException{

    private final String pharmacyName;

    public NoSuchPharmacyException(String pharmacyName) {
        super("Pharmacy with name " + pharmacyName + " does not exist.");
        this.pharmacyName = pharmacyName;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

}
