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

import dk.dbc.badgerfish.BadgerFishReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Locale;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Morten Bøgeskov (mb@dbc.dk)
 */
@Stateless
public class ProfileBean {

    @EJB
    Fetcher fetcher;

    private static final Logger log = LoggerFactory.getLogger(ProfileBean.class);

    @CacheResult(cacheName = "oaProfile",
                 exceptionCacheName = "oaProfileError",
                 cachedExceptions = {ClientErrorException.class,
                                     ServerErrorException.class,
                                     IOException.class})
    public ParsedProfiles getProfilesFor(int agencyId) throws IOException {
        log.info("getProfilesFor({})", agencyId);

        URI uri = fetcher.getUriBuilder()
                .queryParam("action", "openSearchProfile")
                .queryParam("agencyId", String.format(Locale.ROOT, "%06d", agencyId))
                .queryParam("profileVersion", 3)
                .queryParam("outputType", "json")
                .build();
        try (InputStream is = fetcher.get(uri)) {
            OAProfileResponse response = BadgerFishReader.O.readValue(is, OAProfileResponse.class);
            log.trace("OAProfileResponse: {}", response);
            return new ParsedProfiles(response);
        }
    }

    @CacheRemoveAll(cacheName = "oaProfile")
    public void evict() {
    }
    @CacheRemoveAll(cacheName = "oaProfileError")
    public void evictError() {
    }


}
