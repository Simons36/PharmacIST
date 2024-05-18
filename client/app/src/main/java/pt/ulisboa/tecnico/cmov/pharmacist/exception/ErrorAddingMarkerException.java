package pt.ulisboa.tecnico.cmov.pharmacist.exception;

public class ErrorAddingMarkerException extends RuntimeException{

    public ErrorAddingMarkerException() {
        super("Unexpected error adding marker.");
    }
}
