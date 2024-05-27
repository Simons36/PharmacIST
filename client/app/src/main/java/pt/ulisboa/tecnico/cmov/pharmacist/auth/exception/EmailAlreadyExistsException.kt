package pt.ulisboa.tecnico.cmov.pharmacist.auth.exception

class EmailAlreadyExistsException(val email: String) : RuntimeException("Email $email already exists")

