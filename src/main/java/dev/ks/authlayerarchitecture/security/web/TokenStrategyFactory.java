package dev.ks.authlayerarchitecture.security.web;

import dev.ks.authlayerarchitecture.exception.auth.InvalidTokenStrategyException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TokenStrategyFactory {

    private final Map<String, TokenResponseStrategy> strategies;


    public TokenStrategyFactory(List<TokenResponseStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        TokenResponseStrategy::strategyName,
                        Function.identity()
                ));
    }

    public TokenResponseStrategy resolve(String strategyName) {
        if (strategyName == null || strategyName.isBlank()) {
            throw new InvalidTokenStrategyException();
        }

        TokenResponseStrategy strategy = strategies.get(
                strategyName.toUpperCase()
        );

        if (strategy == null) {
            throw new InvalidTokenStrategyException();
        }

        return strategy;
    }
}
