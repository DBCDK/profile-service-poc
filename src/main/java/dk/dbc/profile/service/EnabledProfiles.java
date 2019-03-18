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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Morten Bøgeskov (mb@dbc.dk)
 */
@Path("enabled-profiles")
@Singleton
@Lock(LockType.READ)
public class EnabledProfiles {

    private List<String> enabled;

    @PostConstruct
    public void init() {
        enabled = Arrays.asList(System.getenv("ENABLED_PROFILES").split(","));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEnabledProfiles() {
        return Response.ok(new Resp(enabled)).build();
    }

    @SuppressFBWarnings(value = {"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
    public static class Resp {

        public boolean ok;
        public String error;
        public List<String> profiles;

        public Resp() {
        }

        public Resp(String error) {
            this.ok = false;
            this.error = error;
        }

        public Resp(List<String> profiles) {
            this.ok = true;
            this.profiles = profiles;
        }
    }

}
