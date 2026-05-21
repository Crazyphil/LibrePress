package it.kapfer.librepress.drm;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

class OpenSSL {
    private OpenSSL() {
        // this is a static-only class
    }

    /**
     * Derives a key and IV from various parameters.
     * <p>
     * Thanks go to Ola Bini for releasing this source on his blog.
     * The source was obtained from <a href="http://olabini.com/blog/tag/evp_bytestokey/">here</a>.
     *
     * @param key_len length of the key to generate, in bytes
     * @param iv_len  length of the IV to generate, in bytes
     * @param md      the message digest to use
     * @param salt    the salt used in the derivation, either an 8 byte array or {@code null} if no salt is used
     * @param data    the password to derive key data from
     * @param count   the iteration count to use
     * @return an object containing the key and the IV to initialize a {@link javax.crypto.Cipher} with
     */
    static CipherParameters EVP_BytesToKey(int key_len, int iv_len, MessageDigest md, byte[] salt, byte[] data, int count) {
        byte[][] both = new byte[2][];
        byte[] key = new byte[key_len];
        int key_ix = 0;
        byte[] iv = new byte[iv_len];
        int iv_ix = 0;
        both[0] = key;
        both[1] = iv;
        byte[] md_buf = null;
        int nkey = key_len;
        int niv = iv_len;
        int i = 0;
        if (data == null) {
            return new CipherParameters(both);
        }
        int addmd = 0;
        for (; ; ) {
            md.reset();
            if (addmd++ > 0) {
                md.update(md_buf);
            }
            md.update(data);
            if (null != salt) {
                md.update(salt, 0, 8);
            }
            md_buf = md.digest();
            for (i = 1; i < count; i++) {
                md.reset();
                md.update(md_buf);
                md_buf = md.digest();
            }
            i = 0;
            if (nkey > 0) {
                for (; ; ) {
                    if (nkey == 0) break;
                    if (i == md_buf.length) break;
                    key[key_ix++] = md_buf[i];
                    nkey--;
                    i++;
                }
            }
            if (niv > 0 && i != md_buf.length) {
                for (; ; ) {
                    if (niv == 0) break;
                    if (i == md_buf.length) break;
                    iv[iv_ix++] = md_buf[i];
                    niv--;
                    i++;
                }
            }
            if (nkey == 0 && niv == 0) {
                break;
            }
        }
        for (i = 0; i < md_buf.length; i++) {
            md_buf[i] = 0;
        }
        return new CipherParameters(both);
    }

    static class CipherParameters {
        private final SecretKeySpec secretKeySpec;
        private final IvParameterSpec ivParameterSpec;

        private CipherParameters(byte[][] keyAndIV) {
            secretKeySpec = new SecretKeySpec(keyAndIV[0], "AES");
            ivParameterSpec = new IvParameterSpec(keyAndIV[1]);
        }

        public SecretKey getSecretKey() {
            return secretKeySpec;
        }

        public AlgorithmParameterSpec getAlgorithmParameters() {
            return ivParameterSpec;
        }
    }
}
