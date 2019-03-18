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

import dk.dbc.openagency.ProfileBean;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Morten Bøgeskov (mb@dbc.dk)
 */
@Path("status")
@Stateless
public class Status {

    @EJB
    ProfileBean profileBean;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Resp status() {
        return Resp.OK;
    }

    @GET
    @Path("evict-all")
    @Produces(MediaType.APPLICATION_JSON)
    public Resp evictAll() {
        profileBean.evict();
        profileBean.evictError();
        return Resp.OK;
    }

    @SuppressFBWarnings(value = {"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
    public static class Resp {

        private static final Resp OK = new Resp();

        public boolean ok;
        public List<String> errors;

        public Resp() {
            this.ok = true;
        }

        public Resp(String... errors) {
            this(Arrays.asList(errors));
        }

        public Resp(List<String> errors) {
            this.ok = false;
            this.errors = errors;
        }
    }
}
