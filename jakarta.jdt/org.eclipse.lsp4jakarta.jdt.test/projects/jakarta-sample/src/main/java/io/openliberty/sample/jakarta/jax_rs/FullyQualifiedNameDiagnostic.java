package io.openliberty.sample.jakarta.jax_rs;

import fake.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;

public class FullyQualifiedNameDiagnostic {
	@DELETE
	public void resourceMethodWithTwoEntityParams(String entityParam1, @FormParam(value = "") String nonEntityParam, int entityParam2) {

    }
}
