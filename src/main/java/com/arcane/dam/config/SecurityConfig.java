package com.arcane.dam.config;

import com.arcane.dam.filter.JWTFilter;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.*;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.net.URL;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CorsConfig corsConfig;
    private final JWSAlgorithm jwsAlgorithm = JWSAlgorithm.RS256;

    private final JWEAlgorithm jweAlgorithm = JWEAlgorithm.RSA_OAEP_256;

    private final EncryptionMethod encryptionMethod = EncryptionMethod.A256GCM;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    URL jwkSetUri;

    @Value("${sample.jwe-key-value}")
    RSAPrivateKey key;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())

                .httpBasic(withDefaults())
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(withDefaults()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilter(corsConfig.corsFilter())
                .build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return new NimbusJwtDecoder(jwtProcessor());
    }
    private JWTProcessor<SecurityContext> jwtProcessor() {
        JWKSource<SecurityContext> jwsJwkSource = new RemoteJWKSet<>(this.jwkSetUri);
        JWSKeySelector<SecurityContext> jwsKeySelector = new JWSVerificationKeySelector<>(this.jwsAlgorithm,
                jwsJwkSource);

        JWKSource<SecurityContext> jweJwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey()));
        JWEKeySelector<SecurityContext> jweKeySelector = new JWEDecryptionKeySelector<>(this.jweAlgorithm,
                this.encryptionMethod, jweJwkSource);

        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSKeySelector(jwsKeySelector);
        jwtProcessor.setJWEKeySelector(jweKeySelector);

        return jwtProcessor;
    }

    private RSAKey rsaKey() {
        RSAPrivateCrtKey crtKey = (RSAPrivateCrtKey) this.key;
        Base64URL n = Base64URL.encode(crtKey.getModulus());
        Base64URL e = Base64URL.encode(crtKey.getPublicExponent());
        return new RSAKey.Builder(n, e).privateKey(this.key).keyUse(KeyUse.ENCRYPTION).build();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

}


