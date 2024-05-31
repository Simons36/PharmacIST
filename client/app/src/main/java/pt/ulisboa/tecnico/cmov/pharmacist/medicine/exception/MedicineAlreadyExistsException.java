package pt.ulisboa.tecnico.cmov.pharmacist.medicine.exception;


public class MedicineAlreadyExistsException extends RuntimeException{

    private final String medicineName;

    public MedicineAlreadyExistsException(String medicineName) {
        super("Medicine with name " + medicineName + " already exists.");
        this.medicineName = medicineName;
    }

    public String getMedicineName() {
        return medicineName;
    }

}
