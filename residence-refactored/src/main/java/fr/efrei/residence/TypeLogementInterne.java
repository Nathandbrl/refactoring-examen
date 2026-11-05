package fr.efrei.residence;

import java.util.Optional;

public enum TypeLogementInterne {
    STUDIO, T1, T2, COLOCATION, DUPLEX;

    public static Optional<TypeLogementInterne> depuisCode(String code) {
        try {
            return Optional.of(valueOf(code));
        } catch (IllegalArgumentException | NullPointerException ex) {
            return Optional.empty();
        }
    }
}
