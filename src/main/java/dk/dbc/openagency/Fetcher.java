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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Morten Bøgeskov (mb@dbc.dk)
 */
@Singleton
@Startup
@Lock(LockType.READ)
public class Fetcher {

    private static final Logger log = LoggerFactory.getLogger(Fetcher.class);

    private String openAgencyUrl;
    private UriBuilder uriBuilder;

    @Inject
    Client client;

    @PostConstruct
    public void init() {
        log.info("Init web access");

        openAgencyUrl = System.getenv("OPEN_AGENCY_URL");
        log.debug("openAgencyUrl = {}", openAgencyUrl);
        this.uriBuilder = UriBuilder.fromUri(openAgencyUrl);
    }

    public UriBuilder getUriBuilder() {
        return uriBuilder.clone();
    }

    public InputStream get(URI uri) {
        try {
            log.debug("Fetching: {}", uri);
            InputStream is = client.target(uri)
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(InputStream.class);
            if (is == null)
                throw new ClientErrorException("No content from downstream", 500);
            return is;
        } catch (ClientErrorException | ServerErrorException ex) {
            log.error("Error fetching resource: {}: {}", uri, ex.getMessage());
            log.debug("Error fetching resource: ", ex);
            throw ex;
        }
    }

}
