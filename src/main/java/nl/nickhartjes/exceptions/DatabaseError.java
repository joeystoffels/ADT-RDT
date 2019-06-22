package nl.nickhartjes.exceptions;

import lombok.Getter;
import nl.nickhartjes.persistence.PersistenceAdapter;

@Getter
public class DatabaseError extends RuntimeException {

    private final PersistenceAdapter adapter;

    public DatabaseError(Exception e, PersistenceAdapter adapter) {
        super(e.getMessage());
        this.adapter = adapter;
    }

}
