package io.openliberty.sample.jakarta.cdi;

import jakarta.enterprise.context.*;

@RequestScoped
public class ManagedBean<T> {
	public int a;
	
	
	public ManagedBean() {
		this.a = 10;
	}
}
