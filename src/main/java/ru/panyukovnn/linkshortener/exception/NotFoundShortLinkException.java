package ru.panyukovnn.linkshortener.exception;

public class NotFoundShortLinkException extends LinkShortenerException {

    public NotFoundShortLinkException(String message) {
        super(message);
    }
}
