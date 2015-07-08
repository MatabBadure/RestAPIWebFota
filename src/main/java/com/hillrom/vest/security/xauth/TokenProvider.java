package com.hillrom.vest.security.xauth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.joda.time.DateTime;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Hex;

import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserLoginToken;
import com.hillrom.vest.repository.UserLoginTokenRepository;
import com.hillrom.vest.service.UserService;

public class TokenProvider {

    private static final String SEPERATOR = "#";
    
	private final String secretKey;
    private final int tokenValidity;
    private final UserService userService;
    private final UserLoginTokenRepository userLoginTokenRepository;
    
    public TokenProvider(String secretKey, int tokenValidity,UserService userService,UserLoginTokenRepository userLoginTokenRepository) {
        this.secretKey = secretKey;
        this.tokenValidity = tokenValidity;
        this.userService = userService;
        this.userLoginTokenRepository = userLoginTokenRepository;
    }

    public UserLoginToken createToken(UserDetails userDetails) {
        long expiresAt = System.currentTimeMillis()+ 1000L * tokenValidity;
        String token = userDetails.getUsername() + SEPERATOR + expiresAt + SEPERATOR + computeSignature(userDetails, expiresAt);
        User user = userService.findOneByEmail(userDetails.getUsername()).get();
        UserLoginToken loginToken = new UserLoginToken();
        loginToken.setId(token);
        loginToken.setCreatedTime(new DateTime(expiresAt));
        loginToken.setUser(user);
        userLoginTokenRepository.save(loginToken);
        return loginToken;
    }

    public String computeSignature(UserDetails userDetails, long expires) {
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(userDetails.getUsername()).append(SEPERATOR);
        signatureBuilder.append(expires).append(SEPERATOR);
        signatureBuilder.append(userDetails.getPassword()).append(SEPERATOR);
        signatureBuilder.append(secretKey);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No MD5 algorithm available!");
        }
        return new String(Hex.encode(digest.digest(signatureBuilder.toString().getBytes())));
    }

    public String getUserNameFromToken(String authToken) {
        if (null == authToken) {
            return null;
        }
        String[] parts = authToken.split(SEPERATOR);
        return parts[0];
    }

    public boolean validateToken(String authToken, UserDetails userDetails) {
        String[] parts = authToken.split(SEPERATOR);
        long expires = Long.parseLong(parts[1]);
        String signature = parts[2];
        String signatureToMatch = computeSignature(userDetails, expires);
        return expires >= System.currentTimeMillis() && signature.equals(signatureToMatch);
    }
}
