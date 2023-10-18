package de.ashminh.bikes.middleware.stubs;

import com.google.gson.*;
import de.ashminh.bikes.middleware.common.MethodCall;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ParsingUtil {
	private Gson gson;

	public ParsingUtil(Map<Class<?>, JsonSerializer<?>> serializerMap, Map<Class<?>, JsonDeserializer<?>> deserializerMap) {
		var gsonBuilder = new GsonBuilder();

		for (var s : serializerMap.entrySet()) {
			gsonBuilder.registerTypeAdapter(s.getKey(), s.getValue());
		}

		for (var d : deserializerMap.entrySet()) {
			gsonBuilder.registerTypeAdapter(d.getKey(), d.getValue());
		}

		gsonBuilder.registerTypeAdapter(MethodCall.class, new JsonSerializer<MethodCall>() {
			@Override
			public JsonElement serialize(MethodCall src, Type typeOfSrc, JsonSerializationContext context) {
				var array = new JsonArray();
				array.add(src.interfaceName);
				array.add(src.method);
				array.add(encodeParameterList(src.args));
				return array;
			}
		});

		gsonBuilder.registerTypeAdapter(MethodCall.class, new JsonDeserializer<MethodCall>() {
			@Override
			public MethodCall deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				var ary = json.getAsJsonArray();
				String interfaceName = ary.get(0).getAsString();
				String method = ary.get(1).getAsString();

				var o = decodeJsonToObj(ary.get(2));
				if (o instanceof List<?>) {
					List<Object> args = (List<Object>) o;

					return new MethodCall(interfaceName, method, args);
				} else {
					throw new RuntimeException("Parameterliste konnte nicht deserialisiert werden..");
				}
			}
		});

		gson = gsonBuilder.create();
	}

	private static record Info(String type, boolean is_list) {
	}

	public byte[] marshall(MethodCall methodCall) {
		return encodeObjToJson(methodCall).toString().getBytes(StandardCharsets.UTF_8);
	}

	public MethodCall unmarshal(String message) {
		return (MethodCall) decodeJsonToObj(JsonParser.parseString(message));
	}

	public Object parse(String returnMessage) {
		return decodeJsonToObj(JsonParser.parseString(returnMessage));
	}

	public byte[] serialize(Object oobj) {
		String s = encodeObjToJson(oobj).toString();
		return s.getBytes(StandardCharsets.UTF_8);
	}

	private <T> Info getTypeHeader(T obj) {
		if (obj == null) {
			return new Info("null", false);
		}

		if (obj instanceof Collection) {
			if (((Collection<?>) obj).isEmpty()) {
				return new Info("empty", true);
			} else {
				return new Info(getTypeHeader(((Collection<?>) obj).stream().findFirst().get()).type, true);
			}
		}

		return new Info(obj.getClass().getName(), false);
	}

	// serialize
	public JsonElement encodeParameterList(List<?> list) {
		var array = new JsonArray();

		for (var e : list) {
			if (e == null) {
				array.add(gson.toJsonTree(Map.of("type", "null", "value", "null")));
			}
			array.add(encodeObjToJson(e));
		}

		var map = Map.of(
				"type", "?",
				"values", array
		);

		return gson.toJsonTree(map);
	}

	public JsonElement encodeObjToJson(Object obj) {
		if (obj == null) {
			return gson.toJsonTree(Map.of("type", "null", "value", "null"));
		}

		var type = getTypeHeader(obj);
		JsonElement value;

		value = gson.toJsonTree(obj);
		var map = Map.of(
				"type", type.type,
				type.is_list ? "values" : "value", value
		);

		return gson.toJsonTree(map);
	}

	private String getType(JsonElement j) {
		return j.getAsJsonObject().get("type").getAsString();
	}

	private JsonElement getValue(JsonElement j) {
		return j.getAsJsonObject().get("value");
	}

	private boolean isArray(JsonElement j) {
		if (j == null) return false;
		return j.getAsJsonObject().has("values");
	}

	private JsonArray getValues(JsonElement j) {
		return j.getAsJsonObject().get("values").getAsJsonArray();
	}

	public Object decodeJsonToObj(JsonElement j) {
		if (isArray(j)) {
//			System.out.println("found array with type: " + getType(j));

			String type = getType(j);

			if (type.equals("?")) {
//				System.out.println("mystery!");

				var ary = getValues(j);

				List<Object> parameterList = new ArrayList<>();
				for (var e : ary) {
					Object o = decodeJsonToObj(e);
//					System.out.println(getType(e));
//					System.out.println(o);
					parameterList.add(o);
				}

				return parameterList;
			} else if (type.equals("empty")) {
				return new ArrayList<>();
			} else {
				List<Object> list = new ArrayList<>();
				try {
					for (var element : getValues(j)) {
						list.add(gson.fromJson(element, Class.forName(type)));
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					return null;
				}
				return list;
			}
		} else {
			String objectClassName = getType(j);

			if (objectClassName.equals("null")) {
				return null;
			}

			var value = getValue(j);

			try {
				Class<?> clazz = Class.forName(objectClassName);
				gson.getAdapter(clazz);

				return gson.fromJson(value, clazz);

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			throw new RuntimeException("Ich weiss nicht, wie man " + objectClassName + " entpackt...!!");
		}
	}
}
