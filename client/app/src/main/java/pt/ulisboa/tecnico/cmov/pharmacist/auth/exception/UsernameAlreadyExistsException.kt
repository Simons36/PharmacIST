package pt.ulisboa.tecnico.cmov.pharmacist.auth.exception

class UsernameAlreadyExistsException(val username: String) : RuntimeException("Username $username already exists")