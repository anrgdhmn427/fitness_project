package com.fitness.gateway.configuration;


import com.fitness.gateway.userservicecode.RegisterRequest;
import com.fitness.gateway.userservicecode.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyCloakUserSyncFilter implements WebFilter {


    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (userId != null && token != null) {

            return userService.validateUser(userId)
                    .flatMap(exist -> {
                        if (!exist) {
                            RegisterRequest registerRequest = getUserDetails(token);

                            if (registerRequest != null) {
                                return userService.registerUser(registerRequest)
                                        .then(Mono.empty());
                            } else {
                                return Mono.empty();

                            }

                        } else {
                            log.info("User already exist , skipping sync");
                            return Mono.empty();
                        }
                    })
                    .then(Mono.defer(() -> {
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header("X-User_ID", userId)
                                .build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }));

        }
        return chain.filter(exchange);

    }

    private RegisterRequest getUserDetails(String token) {

        try {

            String tokenWithoutBearer = token.replace("Bearer ", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            RegisterRequest registerRequest = new RegisterRequest();
            RegisterRequest register = new RegisterRequest();
            register.setEmail(claimsSet.getStringClaim("email"));
            register.setKeyCloakId(claimsSet.getStringClaim("sub"));
            register.setPassword(claimsSet.getStringClaim("dummy@123"));
            register.setFirstName(claimsSet.getStringClaim("given_name"));
            register.setLastName(claimsSet.getStringClaim("family_name"));
            return register;


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }
}
