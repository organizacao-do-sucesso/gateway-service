package store.gateway.security;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

@Component
public class AuthorizationPolicy {

    private static final String PRODUCT_READ = "product.read";
    private static final String PRODUCT_WRITE = "product.write";
    private static final String ORDER_READ = "order.read";
    private static final String ORDER_WRITE = "order.write";
    private static final String EXCHANGE_READ = "exchange.read";
    private static final String ACCOUNT_ADMIN = "account.admin";

    private final List<Rule> rules = List.of(
        rule(HttpMethod.GET, "/products", PRODUCT_READ),
        rule(HttpMethod.GET, "/products/**", PRODUCT_READ),
        rule(HttpMethod.POST, "/products", PRODUCT_WRITE),
        rule(HttpMethod.DELETE, "/products/**", PRODUCT_WRITE),
        rule(HttpMethod.GET, "/orders", ORDER_READ),
        rule(HttpMethod.GET, "/orders/**", ORDER_READ),
        rule(HttpMethod.POST, "/orders", ORDER_WRITE),
        rule(HttpMethod.GET, "/exchanges/**", EXCHANGE_READ),
        rule(HttpMethod.POST, "/accounts/{id}/roles", ACCOUNT_ADMIN)
    );

    public Optional<String> requiredPermission(ServerHttpRequest request) {
        PathContainer path = PathContainer.parsePath(request.getURI().getPath());
        return rules.stream()
            .filter(rule -> rule.matches(request.getMethod(), path))
            .map(Rule::permission)
            .findFirst();
    }

    private static Rule rule(HttpMethod method, String path, String permission) {
        return new Rule(
            method,
            PathPatternParser.defaultInstance.parse(path),
            permission
        );
    }

    private record Rule(
        HttpMethod method,
        PathPattern path,
        String permission
    ) {

        private boolean matches(HttpMethod requestMethod, PathContainer requestPath) {
            return method.equals(requestMethod) && path.matches(requestPath);
        }
    }
}
