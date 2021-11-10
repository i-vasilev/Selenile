package ru.vasilev.selenile.deserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CapabilityDeserializer implements JsonDeserializer<DesiredCapabilities> {
    /**
     * Deserializes Json object into {@link DesiredCapabilities} object.
     *
     * @param json    Json object for deserializing into {@link DesiredCapabilities} object.
     * @param typeOfT The type to deserialize to.
     * @return Deserialized object.
     * @throws JsonParseException If {@code json} has wrong format.
     */
    @Override
    public DesiredCapabilities deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        DesiredCapabilities resultMap = new DesiredCapabilities();
        try {
            final JsonArray jsonArray = json.getAsJsonArray();
            if (jsonArray.size() > 0) {
                final Map<String, Object> map = new ObjectMapper().readValue(jsonArray.get(0)
                                                                                      .toString(), Map.class);
                List<String> ignoreCaps = new ArrayList<>();
                ignoreCaps.add("maxInstances");
                ignoreCaps.add("rotatable");
                ignoreCaps.add("session-override");
                ignoreCaps.add("wdaLocalPort");
                ignoreCaps.add("simulatorStartupTimeout");
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (ignoreCaps.contains(entry.getKey())) {
                        continue;
                    }
                    if (entry.getValue() instanceof Double) {
                        resultMap.setCapability(entry.getKey(), ((Double) entry.getValue()).intValue());
                    } else {
                        resultMap.setCapability(entry.getKey(), entry.getValue());
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new JsonParseException(e);
        }
        return resultMap;
    }

}
