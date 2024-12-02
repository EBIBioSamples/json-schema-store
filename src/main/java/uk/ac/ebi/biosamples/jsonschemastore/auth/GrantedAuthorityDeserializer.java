package uk.ac.ebi.biosamples.jsonschemastore.auth;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GrantedAuthorityDeserializer extends JsonDeserializer<Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ArrayNode node = p.getCodec().readTree(p); // Get the JSON array
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // Iterate over the elements in the array
        for (int i = 0; i < node.size(); i++) {
            ObjectNode authorityNode = (ObjectNode) node.get(i); // Get each object in the array
            String authority = authorityNode.get("authority").asText(); // Get the 'authority' field
            authorities.add(new SimpleGrantedAuthority(authority)); // Add the authority to the list
        }

        return authorities; // Return the list of authorities
    }
}
