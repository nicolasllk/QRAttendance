package com.example.qrattendance.security.service;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.qrattendance.commons.CommonConstants;
import com.example.qrattendance.repository.AttendanceUserRepository;


@Service
public class TokenService {
    public static final String IV_PARAM_SPEC_TOKEN = "randomToken1234567890";
    public static final String SECRET_KEY_SPEC_TOKEN = "secretKeyToken1234567";
    
    private final AttendanceUserRepository attendanceUserRepository;

    @Autowired
    public TokenService(final AttendanceUserRepository attendanceUserRepository) {
        this.attendanceUserRepository = attendanceUserRepository;
    }

    /**
     * searches unique token assigned to user.
     * @param token
     * @return
     */
    public boolean isValidToken(final String token) {
        final String decryptedToken = decryptToken(token);
        return attendanceUserRepository.findByCurrentTokenSession(decryptedToken).isPresent();
    }

    private String decryptToken(final String token) {
        try {
            final Cipher qrCipher = Cipher.getInstance(CommonConstants.TRANSFORMATION);
            final IvParameterSpec ivParameterSpec = new IvParameterSpec(IV_PARAM_SPEC_TOKEN.getBytes());
            final SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY_SPEC_TOKEN.getBytes(),
                    CommonConstants.AES);
            qrCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] decryptedBytes = qrCipher.doFinal(Base64.getDecoder().decode(token));
            return new String(decryptedBytes);
        } catch (Exception e) {
            // Log the error
            return null;
        }
    }
}
