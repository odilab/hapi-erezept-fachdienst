---
source: https://hapifhir.io/hapi-fhir/docs/interceptors/jakarta_upgrade.html
crawled: 2025-08-01T14:05:22.987804
---

# Interceptors Jakarta Upgrade

#  11.8.17.0.0 Interceptor Upgrade Guide
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/jakarta_upgrade.html#700-interceptor-upgrade-guide)
As of HAPI-FHIR 7.0.0, dependency on the `javax.*` packages has now changed to instead use the `jakarta.*` packages. This is a breaking change for any users who have written their own interceptors, as the package names of the interfaces have changed.
In order to upgrade your interceptors, you will need to change, at a minimum, the imports in your affected interceptor implementations. For example, if you have an interceptor that uses imports such as `javax.servlet.http.HttpServletRequest`, you will need to change these to `jakarta.servlet.http.HttpServletRequest`. The following is an example of a migration of an interceptor.
##  11.8.1.1Example[](https://hapifhir.io/hapi-fhir/docs/interceptors/jakarta_upgrade.html#example)
11.8.1.1.1Old Server Interceptor ```
package com.example;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.function.Supplier;

public class SampleInteceptor{

    private static final Logger ourLog = LoggerFactory.getLogger(SampleInteceptor.class);
    
	@Hook(Pointcut.SERVER_INCOMING_REQUEST_PRE_PROCESSED)
	public boolean serverIncomingRequestPreProcessed(HttpServletRequest theHttpServletRequest, HttpServletResponse theHttpServletResponse) {
		ourLog.info("I'm an interceptor!");
		return true;
	}
}

```
Copy
##  11.8.1.2New Server Interceptor[](https://hapifhir.io/hapi-fhir/docs/interceptors/jakarta_upgrade.html#new-server-interceptor)
```
package com.example;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.function.Supplier;

public class SampleInteceptor{

    private static final Logger ourLog = LoggerFactory.getLogger(SampleInteceptor.class);
    
	@Hook(Pointcut.SERVER_INCOMING_REQUEST_PRE_PROCESSED)
	public boolean serverIncomingRequestPreProcessed(HttpServletRequest theHttpServletRequest, HttpServletResponse theHttpServletResponse) {
		ourLog.info("I'm an interceptor!");
		return true;
	}
}

```

Copy
You'll note that there is only one very subtle difference between these two versions, and that is the change from:
```
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

```

Copy
to:
```
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

```

Copy
[ ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html)
11.8 7.0.0 Migration Guide 
* Interceptors 
* [ 11.0  Interceptors Overview ](https://hapifhir.io/hapi-fhir/docs/interceptors/interceptors.html)
* [ 11.1  Client Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/client_interceptors.html)
* [ 11.2  Filter Hook Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/filter_hook_interceptors.html)
* [ 11.3  Client Pointcuts ](https://hapifhir.io/hapi-fhir/docs/interceptors/client_pointcuts.html)
* [ 11.4  Built-In Client Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_client_interceptors.html)
* [ 11.5  Server Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/server_interceptors.html)
* [ 11.6  Server Pointcuts ](https://hapifhir.io/hapi-fhir/docs/interceptors/server_pointcuts.html)
* [ 11.7  Built-In Server Interceptors ](https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_server_interceptors.html)
* [ 11.8  7.0.0 Migration Guide ](https://hapifhir.io/hapi-fhir/docs/interceptors/jakarta_upgrade.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)