package mctmods.resourcedatapackloader.content.rubic.regionlib;

import java.io.IOException;

public class UnsupportedDataException extends IOException {
    public UnsupportedDataException(String message) { super(message); }

    public UnsupportedDataException(Throwable cause) { super(cause); }

    public static class WithKey extends UnsupportedDataException {
        private final Object key;

        public WithKey(Throwable cause, Object key) {
            super(cause);
            this.key = key;
        }

        @SuppressWarnings("unchecked") public <K> K getKey() { return (K) key; }
    }
}
