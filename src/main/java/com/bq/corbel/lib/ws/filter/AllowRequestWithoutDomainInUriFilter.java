package com.bq.corbel.lib.ws.filter;

import com.bq.corbel.lib.token.exception.TokenVerificationException;
import com.bq.corbel.lib.token.parser.TokenParser;
import com.bq.corbel.lib.token.reader.TokenReader;
import com.bq.corbel.lib.ws.auth.priority.CorbelPriorities;

import java.net.URI;
import java.util.regex.Pattern;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alberto J. Rubio
 */
@PreMatching @Priority(CorbelPriorities.ALLOW_REQUEST_WITHOUT_DOMAIN_IN_URI_FILTER) public class AllowRequestWithoutDomainInUriFilter
        extends
            OptionalContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(AllowRequestWithoutDomainInUriFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String UNAUTHENTICATED = "unauthenticated";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String EMPTY_STRING = "";

    private final TokenParser tokenParser;
    private final String unAuthenticatedPathPattern;
    private final Pattern requestWithDomainPattern;
    private final Pattern requestWithoutDomainPattern;

    public AllowRequestWithoutDomainInUriFilter(boolean enabled, TokenParser tokenParser, String unAuthenticatedPathPattern,
            String endpoints) {
        super(enabled);
        this.tokenParser = tokenParser;
        this.unAuthenticatedPathPattern = unAuthenticatedPathPattern;
        this.requestWithDomainPattern = Pattern.compile("v[0-9]+\\.[0-9]+/[\\w\\-:\\.]+/(" + endpoints.replace(",", "|") + ")(/.*)?");
        this.requestWithoutDomainPattern = Pattern.compile("v[0-9]+\\.[0-9]+/(" + endpoints.replace(",", "|") + ")(/.*)?");
    }

    @Override
    public void filter(ContainerRequestContext request) {
        try {
            String path = request.getUriInfo().getPath();
            if (!path.matches(unAuthenticatedPathPattern) && includeDomainInUri(path)) {
                String domain = extractRequestDomain(request);
                int slashIndex = path.indexOf("/");
                if (domain != null && slashIndex != -1) {
                    String version = path.substring(0, slashIndex);
                    String versionAndDomainPath = version + "/" + domain;
                    String pathWithoutVersion = path.replace(version, EMPTY_STRING);
                    String pathWithDomain = versionAndDomainPath + pathWithoutVersion;
                    URI requestUriWithDomain = request.getUriInfo().getRequestUriBuilder().replacePath(pathWithDomain).build();
                    request.setRequestUri(requestUriWithDomain);
                }
            }
        } catch (TokenVerificationException ignored) {
            LOG.debug("Cannot parse authorization token");
        }
    }

    private boolean includeDomainInUri(String path) {
        return !requestWithDomainPattern.matcher(path).matches() && requestWithoutDomainPattern.matcher(path).matches();
    }

    private String extractRequestDomain(ContainerRequestContext request) throws TokenVerificationException {
        String domain = UNAUTHENTICATED;
        String authorizationHeader = request.getHeaderString(AUTHORIZATION_HEADER);
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            TokenReader tokenReader = tokenParser.parseAndVerify(authorizationHeader.substring(TOKEN_PREFIX.length()));
            domain = tokenReader.getInfo().getDomainId();
        }
        return domain;
    }
}