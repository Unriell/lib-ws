package com.bq.corbel.lib.ws.auth;

import com.google.gson.JsonObject;

import java.util.Set;

/**
 * @author Alexander De Leon
 * 
 */
public interface AuthorizationRulesService {

	Set<JsonObject> getAuthorizationRules(String token, String audience);

	boolean existsRulesForToken(String token, String audience);
}
