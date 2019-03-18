/*
 * Copyright (C) 2019 DBC A/S (http://dbc.dk/)
 *
 * This is part of profile-service
 *
 * profile-service is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * profile-service is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dbc.openagency;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Morten Bøgeskov (mb@dbc.dk)
 */
@Singleton
@Startup
@Lock(LockType.READ)
public class ClientProvider {

    private static final Logger log = LoggerFactory.getLogger(ClientProvider.class);
    private Client client;

    @PostConstruct
    public void init() {
        log.info("Creating http-client");
        String userAgent = System.getenv().getOrDefault("USER_AGENT", "Unknown/0.1");
        this.client = ClientBuilder.newBuilder()
                .register((ClientRequestFilter) (ClientRequestContext context) -> {
                    context.getHeaders().putSingle("User-Agent", userAgent);
                }).build();
    }

    @Produces
    public Client makeClient(InjectionPoint ip) {
        return client;
    }

}
