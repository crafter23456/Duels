package net.Duels.utility;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

public class HeadUtils {
	
	public static ItemStack getCustomSkull(String url) {
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		PropertyMap propertyMap = profile.getProperties();
		if (propertyMap == null) {
			throw new IllegalStateException("Profile doesn't contain a property map");
		}
		byte[] encodedData = Base64.getEncoder()
				.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
		propertyMap.put("textures", new Property("textures", new String(encodedData)));
		ItemStack head = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial(), 1, (short) 3);
		ItemMeta headMeta = head.getItemMeta();
		Class<?> headMetaClass = headMeta.getClass();
		getField(headMetaClass, "profile", GameProfile.class, 0).set(headMeta, profile);
		headMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		head.setItemMeta(headMeta);
		return head;
	}

	private static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType, int index) {
		Field[] declaredFields;
		for (int length = (declaredFields = target.getDeclaredFields()).length, i = 0; i < length; ++i) {
			Field field = declaredFields[i];
			if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType())
					&& index-- <= 0) {
				field.setAccessible(true);
				return new FieldAccessor<T>() {
					@Override
					public T get(Object target) {
						try {
							return (T) field.get(target);
						} catch (IllegalAccessException e) {
							throw new RuntimeException("Cannot access reflection.", e);
						}
					}

					@Override
					public void set(Object target, Object value) {
						try {
							field.set(target, value);
						} catch (IllegalAccessException e) {
							throw new RuntimeException("Cannot access reflection.", e);
						}
					}

					@Override
					public boolean hasField(Object target) {
						return field.getDeclaringClass().isAssignableFrom(target.getClass());
					}
				};
			}
		}
		if (target.getSuperclass() != null) {
			return (FieldAccessor<T>) getField(target.getSuperclass(), name, (Class<Object>) fieldType, index);
		}
		throw new IllegalArgumentException("Cannot find field with type " + fieldType);
	}

	private interface FieldAccessor<T> {
		T get(Object p0);

		void set(Object p0, Object p1);

		boolean hasField(Object p0);
	}
}
