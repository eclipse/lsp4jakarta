package io.openliberty.sample.jakarta.cdi;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ManagedBean {
	public int a;
	
	public ManagedBean(int a) {
		this.a = a;
	}
}
