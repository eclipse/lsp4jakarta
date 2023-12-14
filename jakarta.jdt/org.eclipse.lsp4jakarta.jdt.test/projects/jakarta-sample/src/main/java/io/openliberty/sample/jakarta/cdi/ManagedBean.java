package io.openliberty.sample.jakarta.cdi;

import jakarta.enterprise.context.*;

@SessionScoped
public class ManagedBean<T> {
	public int a;

	@RequestScoped
	public int b;
	
	public ManagedBean() {
		this.a = 10;
		this.b = 20;
	}
}
