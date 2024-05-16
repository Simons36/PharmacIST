package pt.ulisboa.tecnico.cmov.pharmacist.exception;

import java.util.List;

public class AddPharmacyMissingPropertiesException extends RuntimeException {

    public AddPharmacyMissingPropertiesException(List<String> missingProperties) {
        super(buildMessage(missingProperties));
    }

    private static String buildMessage(List<String> missingProperties) {
        StringBuilder sb = new StringBuilder().append("Missing the following properties to add pharmacy:");
        for (String property : missingProperties) {
            sb.append("\n").append(property);
        }
        return sb.toString();
    }
}
