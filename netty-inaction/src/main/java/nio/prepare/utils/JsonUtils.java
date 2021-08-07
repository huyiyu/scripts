package nio.prepare.utils;

import com.google.gson.Gson;
import nio.prepare.pojo.WsRequest;
import nio.prepare.pojo.WsResponse;

public class JsonUtils {
    static {
        GSON = new Gson();
    }

    public static final Gson GSON;

    public static<T> T jsonToObject(String json, Class<T> webSocketMsgClass) {
        return GSON.fromJson(json, webSocketMsgClass);
    }

    public static String toJsonString(WsResponse wsResponse) {
        return GSON.toJson(wsResponse);
    }
}
