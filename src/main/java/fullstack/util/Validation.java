package fullstack.util;

import fullstack.rest.model.CreateUserRequest;
import fullstack.service.exception.UserCreationException;

public class Validation {

    public static void validateUserRequest(CreateUserRequest request) throws UserCreationException {
        if (request == null) {
            throw new UserCreationException("La richiesta non può essere vuota. Nome, cognome, password e almeno un contatto sono obbligatori.");
        }
        if (!request.hasValidNameAndSurname()) {
            throw new UserCreationException("Nome e cognome sono obbligatori.");
        }
        if (!request.hasValidContact()) {
            throw new UserCreationException("È necessario inserire almeno un'email o un numero di telefono.");
        }
    }

}
