/*
 * Copyright 2015-2017 floragunn GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.security.securityconf;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import com.google.common.collect.Multimap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensearch.common.settings.Settings;
import org.opensearch.security.auth.AuthDomain;
import org.opensearch.security.auth.AuthFailureListener;
import org.opensearch.security.auth.AuthorizationBackend;
import org.opensearch.security.auth.blocking.ClientBlockRegistry;
import org.opensearch.security.auth.http.jwt.HTTPJwtAuthenticator;
import org.opensearch.security.auth.http.jwt.keybyoidc.HTTPJwtKeyByOpenIdConnectAuthenticator;
import org.opensearch.security.auth.http.kerberos.HTTPSpnegoAuthenticator;
import org.opensearch.security.auth.http.saml.HTTPSamlAuthenticator;
import org.opensearch.security.auth.internal.InternalAuthenticationBackend;
import org.opensearch.security.auth.internal.NoOpAuthenticationBackend;
import org.opensearch.security.auth.internal.NoOpAuthorizationBackend;
import org.opensearch.security.auth.ldap.backend.LDAPAuthenticationBackend;
import org.opensearch.security.auth.ldap.backend.LDAPAuthorizationBackend;
import org.opensearch.security.auth.ldap2.LDAPAuthenticationBackend2;
import org.opensearch.security.auth.ldap2.LDAPAuthorizationBackend2;
import org.opensearch.security.auth.limiting.AddressBasedRateLimiter;
import org.opensearch.security.auth.limiting.UserNameBasedRateLimiter;
import org.opensearch.security.http.HTTPBasicAuthenticator;
import org.opensearch.security.http.HTTPClientCertAuthenticator;
import org.opensearch.security.http.HTTPProxyAuthenticator;
import org.opensearch.security.http.proxy.HTTPExtendedProxyAuthenticator;
import org.opensearch.security.securityconf.impl.DashboardSignInOption;

public abstract class DynamicConfigModel {

    protected final Logger log = LogManager.getLogger(this.getClass());

    public abstract SortedSet<AuthDomain> getRestAuthDomains();

    public abstract Set<AuthorizationBackend> getRestAuthorizers();

    public abstract boolean isAnonymousAuthenticationEnabled();

    public abstract boolean isXffEnabled();

    public abstract String getInternalProxies();

    public abstract String getRemoteIpHeader();

    public abstract boolean isRestAuthDisabled();

    public abstract boolean isInterTransportAuthDisabled();

    public abstract boolean isRespectRequestIndicesEnabled();

    public abstract String getDashboardsServerUsername();

    public abstract String getDashboardsOpenSearchRole();

    public abstract String getDashboardsIndexname();

    public abstract boolean isDashboardsMultitenancyEnabled();

    public abstract boolean isDashboardsPrivateTenantEnabled();

    public abstract String getDashboardsDefaultTenant();

    public abstract boolean isDnfofEnabled();

    public abstract boolean isMultiRolespanEnabled();

    public abstract String getFilteredAliasMode();

    public abstract String getHostsResolverMode();

    public abstract boolean isDnfofForEmptyResultsEnabled();

    public abstract List<AuthFailureListener> getIpAuthFailureListeners();

    public abstract Multimap<String, AuthFailureListener> getAuthBackendFailureListeners();

    public abstract List<ClientBlockRegistry<InetAddress>> getIpClientBlockRegistries();

    public abstract Multimap<String, ClientBlockRegistry<String>> getAuthBackendClientBlockRegistries();

    public abstract List<DashboardSignInOption> getSignInOptions();

    public abstract Settings getDynamicOnBehalfOfSettings();

    protected final Map<String, String> authImplMap = new HashMap<>();

    public DynamicConfigModel() {
        super();

        authImplMap.put("intern_c", InternalAuthenticationBackend.class.getName());
        authImplMap.put("intern_z", NoOpAuthorizationBackend.class.getName());

        authImplMap.put("internal_c", InternalAuthenticationBackend.class.getName());
        authImplMap.put("internal_z", NoOpAuthorizationBackend.class.getName());

        authImplMap.put("noop_c", NoOpAuthenticationBackend.class.getName());
        authImplMap.put("noop_z", NoOpAuthorizationBackend.class.getName());

        authImplMap.put("ldap_c", LDAPAuthenticationBackend.class.getName());
        authImplMap.put("ldap_z", LDAPAuthorizationBackend.class.getName());

        authImplMap.put("ldap2_c", LDAPAuthenticationBackend2.class.getName());
        authImplMap.put("ldap2_z", LDAPAuthorizationBackend2.class.getName());

        authImplMap.put("basic_h", HTTPBasicAuthenticator.class.getName());
        authImplMap.put("proxy_h", HTTPProxyAuthenticator.class.getName());
        authImplMap.put("extended-proxy_h", HTTPExtendedProxyAuthenticator.class.getName());
        authImplMap.put("clientcert_h", HTTPClientCertAuthenticator.class.getName());
        authImplMap.put("kerberos_h", HTTPSpnegoAuthenticator.class.getName());
        authImplMap.put("jwt_h", HTTPJwtAuthenticator.class.getName());
        authImplMap.put("openid_h", HTTPJwtKeyByOpenIdConnectAuthenticator.class.getName());
        authImplMap.put("saml_h", HTTPSamlAuthenticator.class.getName());

        authImplMap.put("ip_authFailureListener", AddressBasedRateLimiter.class.getName());
        authImplMap.put("username_authFailureListener", UserNameBasedRateLimiter.class.getName());
    }

}
