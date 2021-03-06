package com.bq.corbel.lib.ws.auth;

import com.bq.corbel.lib.ws.auth.repository.AuthorizationRulesRepository;
import com.google.gson.JsonObject;

import java.util.Set;

/**
 * @author Alexander De Leon
 * 
 */
public class DefaultAuthorizationRulesService implements AuthorizationRulesService {

	private final AuthorizationRulesRepository repository;

	public DefaultAuthorizationRulesService(AuthorizationRulesRepository repository) {
		this.repository = repository;
	}

	@Override
	public Set<JsonObject> getAuthorizationRules(String token, String audience) {
		String key = repository.getKeyForAuthorizationRules(token, audience);
		return repository.get(key);
	}

	@Override
	public boolean existsRulesForToken(String token, String audience) {
		String key = repository.getKeyForAuthorizationRules(token, audience);
		return repository.existsRules(key);
	}

}
