package com.jakeapp.johannes.spuren.tray;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class TolerantResourceBundle extends ResourceBundle {

	private ResourceBundle inner;

	public TolerantResourceBundle(ResourceBundle inner) {
		this.inner = inner;
	}

	@Override
	public Enumeration<String> getKeys() {
		return this.inner.getKeys();
	}

	@Override
	protected Object handleGetObject(String key) {
		try {
			return this.inner.getObject(key);
		} catch (MissingResourceException e) {
			System.err.println("TODO: define " + key + " in properties file");
			return key;
		}
	}

}
