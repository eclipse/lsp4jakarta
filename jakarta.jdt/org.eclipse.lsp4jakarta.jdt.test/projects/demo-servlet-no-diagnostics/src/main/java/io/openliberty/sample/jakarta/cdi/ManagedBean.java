package io.openliberty.sample.jakarta.cdi;

import jakarta.fake.context.*;

@RequestScoped
public class ManagedBean<T> {
	public int a;
	
	
	public ManagedBean() {
		this.a = 10;
	}
}
