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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Morten Bøgeskov (mb@dbc.dk)
 */
@SuppressFBWarnings(value = {"UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD"})
public class ParsedProfiles implements Serializable {

    private static final long serialVersionUID = -3527064622411416196L;

    private final Map<String, Profile> profiles;

    public ParsedProfiles(OAProfileResponse response) {
        this.profiles = response.openSearchProfileResponse.profile.stream()
                .map(Profile::new)
                .collect(Collectors.toMap(Profile::getName, p -> p));
    }

    public Map<String, Profile> getProfiles() {
        return profiles;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.profiles);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ParsedProfiles other = (ParsedProfiles) obj;
        if (!Objects.equals(this.profiles, other.profiles))
            return false;
        return true;
    }

    @SuppressFBWarnings(value = {"UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD"})
    public static class Profile implements Serializable {

        private static final long serialVersionUID = 9173310341499192210L;

        private final String name;
        private final List<String> relation;
        private final List<String> search;

        public Profile(OAProfile profile) {
            name = profile.profileName;

            search = profile.source.stream()
                    .filter(source -> source.sourceSearchable)
                    .map(source -> source.sourceIdentifier)
                    .collect(Collectors.toSet()) // UNIQ
                    .stream()
                    .sorted()
                    .collect(Collectors.toList());
            relation = Stream.concat(search.stream(),
                                     profile.source.stream()
                                             .filter(source -> source.relation != null && !source.relation.isEmpty())
                                             .map(source -> source.sourceIdentifier))
                    .collect(Collectors.toSet()) // UNIQ
                    .stream()
                    .sorted()
                    .collect(Collectors.toList());

        }

        public String getName() {
            return name;
        }

        public List<String> getRelation() {
            return relation;
        }

        public List<String> getSearch() {
            return search;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.name);
            hash = 71 * hash + Objects.hashCode(this.relation);
            hash = 71 * hash + Objects.hashCode(this.search);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Profile other = (Profile) obj;
            if (!Objects.equals(this.name, other.name))
                return false;
            if (!Objects.equals(this.relation, other.relation))
                return false;
            if (!Objects.equals(this.search, other.search))
                return false;
            return true;
        }
    }
}
