/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.configuration;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

/**
 * Guice configuration module for providing Jersey Clients.
 * 
 * @author dbauman
 * 
 */
public final class JerseyModule extends AbstractModule {

    /**
     * Class logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyModule.class);

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public Client provideClient(
            @Named(ConfigurationModule.PROPERTY_SCOM_CONNECT_TIMEOUT) final int connectTimeout,
            @Named(ConfigurationModule.PROPERTY_SCOM_READ_TIMEOUT) final int readTimeout,
            @Named(ConfigurationModule.PROPERTY_SCOM_USERNAME) final String username,
            @Named(ConfigurationModule.PROPERTY_SCOM_PASSWORD) final String password) {

        final ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(ObjectMapperProvider.class);
        config.getClasses().add(JacksonJsonProvider.class);

        /* SSL config to accept all certificates */
        /*
         * Straight-up copied from this gist: https://gist.github.com/outbounder/1069465
         */
        final TrustManager[] certs = new TrustManager[] { new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            }
        } };

        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, certs, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        } catch (final java.security.GeneralSecurityException ex) {
            JerseyModule.LOGGER.error("SSLContext error", ex);
        }

        config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(
                new HostnameVerifier() {
                    @Override
                    public boolean verify(final String hostname, final SSLSession session) {
                        return true;
                    }
                }, ctx));

        /* Create a new Client from the config */
        final Client client = Client.create(config);
        client.setFollowRedirects(true);

        /* Basic Authorization */
        final HTTPBasicAuthFilter authorizationFilter = new HTTPBasicAuthFilter(username, password);
        client.addFilter(authorizationFilter);
        client.addFilter(new LoggingFilter());

        /* Set timeouts */
        client.setConnectTimeout(connectTimeout);
        client.setReadTimeout(readTimeout);

        return client;
    }
}
