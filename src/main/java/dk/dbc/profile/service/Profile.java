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
package dk.dbc.profile.service;

import dk.dbc.openagency.ParsedProfiles;
import dk.dbc.openagency.ProfileBean;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Morten Bøgeskov (mb@dbc.dk)
 */
@Path("profile")
@Stateless
public class Profile {

    private static final Logger log = LoggerFactory.getLogger(Profile.class);

    @EJB
    ProfileBean profileBean;

    @GET
    @Path("search/{agencyId}/{profile}")
    @Produces(MediaType.APPLICATION_JSON)
    public Resp getSearchProfile(@PathParam("agencyId") int agencyId,
                                 @PathParam("profile") String profile) {
        log.info("getSearchProfile()");
        try {
            ParsedProfiles profiles = profileBean.getProfilesFor(agencyId);
            ParsedProfiles.Profile responseProfile = profiles.getProfiles().get(profile);
            if (responseProfile == null)
                return new Resp("Unknown profile");
            return new Resp(responseProfile.getSearch());
        } catch (IOException | RuntimeException ex) {
            log.error("Error processing request for: {}/{}: {}", agencyId, profile, ex.getMessage());
            log.debug("Error processing request for: {}/{}: ", agencyId, profile, ex);
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("relation/{agencyId}/{profile}")
    @Produces(MediaType.APPLICATION_JSON)
    public Resp getRelationProfile(@PathParam("agencyId") int agencyId,
                                   @PathParam("profile") String profile) {
        log.info("getRelationProfile()");
        try {
            ParsedProfiles profiles = profileBean.getProfilesFor(agencyId);
            ParsedProfiles.Profile responseProfile = profiles.getProfiles().get(profile);
            if (responseProfile == null)
                return new Resp("Unknown profile");
            return new Resp(responseProfile.getRelation());
        } catch (IOException | RuntimeException ex) {
            log.error("Error processing request for: {}/{}: {}", agencyId, profile, ex.getMessage());
            log.debug("Error processing request for: {}/{}: ", agencyId, profile, ex);
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @SuppressFBWarnings(value = {"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
    public static class Resp {

        public boolean ok;
        public String error;
        public List<String> collectionIdentifiers;

        public Resp() {
        }

        public Resp(String error) {
            this.ok = false;
            this.error = error;
        }

        public Resp(List<String> collectionIdentifiers) {
            this.ok = true;
            this.collectionIdentifiers = collectionIdentifiers;
        }
    }
}
