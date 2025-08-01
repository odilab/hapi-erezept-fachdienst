---
source: https://hapifhir.io/hapi-fhir/docs/security/binary_security_interceptor.html
crawled: 2025-08-01T14:04:54.354295
---

# Security Binary Security Interceptor

#  12.6.1Binary Security Interceptor
[ ](https://hapifhir.io/hapi-fhir/docs/security/binary_security_interceptor.html#binary-security-interceptor)
The Binary resource has an element called `Binary.securityContext` that can be used to declare a security context for a given resource instance.
The **BinarySecurityContextInterceptor** can be used to verify whether a calling user/client should have access to a Binary resource they are trying to access.
Note that this interceptor can currently only enforce identifier values found in `Binary.securityContext.identifier`. Reference values found in `Binary.securityContext.reference` are not examined by this interceptor at this time, although this may be added in the future.
This interceptor is intended to be subclassed. A simple example is shown below:
```
/*-
 * #%L
 * HAPI FHIR - Docs
 * %%
 * Copyright (C) 2014 - 2025 Smile CDR, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package ca.uhn.hapi.fhir.docs.interceptor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.interceptor.binary.BinarySecurityContextInterceptor;

/**
 * This class is mostly intended as an example implementation of the
 * {@link BinarySecurityContextInterceptor} although it could be used if
 * you wanted its specific rules.
 */
public class HeaderBasedBinarySecurityContextInterceptor extends BinarySecurityContextInterceptor {

   /**
    * Header name
    */
   public static final String X_SECURITY_CONTEXT_ALLOWED_IDENTIFIER = "X-SecurityContext-Allowed-Identifier";

   /**
    * Constructor
    *
    * @param theFhirContext The FHIR context
    */
   public HeaderBasedBinarySecurityContextInterceptor(FhirContext theFhirContext) {
      super(theFhirContext);
   }

   /**
    * This method should be overridden in order to determine whether the security
    * context identifier is allowed for the user.
    *
    * @param theSecurityContextSystem The <code>Binary.securityContext.identifier.system</code> value
    * @param theSecurityContextValue  The <code>Binary.securityContext.identifier.value</code> value
    * @param theRequestDetails        The request details associated with this request
    */
   @Override
   protected boolean securityContextIdentifierAllowed(
         String theSecurityContextSystem, String theSecurityContextValue, RequestDetails theRequestDetails) {

      // In our simple example, we will use an incoming header called X-SecurityContext-Allowed-Identifier
      // to determine whether the security context is allowed. This is typically not what you
      // would want, since this is trusting the client to tell us what they are allowed
      // to see. You would typically verify an access token or user session with something
      // external, but this is a simple demonstration.
      String actualHeaderValue = theRequestDetails.getHeader(X_SECURITY_CONTEXT_ALLOWED_IDENTIFIER);
      String expectedHeaderValue = theSecurityContextSystem + "|" + theSecurityContextValue;
      return expectedHeaderValue.equals(actualHeaderValue);
   }
}

```

Copy
##  12.6.1.1Combining with Bulk Export[](https://hapifhir.io/hapi-fhir/docs/security/binary_security_interceptor.html#combining-with-bulk-export)
The `setBinarySecurityContextIdentifierSystem(..)` and `setBinarySecurityContextIdentifierValue(..)` properties on the `BulkExportJobParameters` object can be used to automatically populate the security context on Binary resources created by Bulk Export jobs with values that can be verified by this interceptor. An interceptor on the `STORAGE_PRE_INITIATE_BULK_EXPORT` pointcut is the recommended way to set these properties when a new Bulk Export job is being kicked off.
NB: Previous versions recommended using the `STORAGE_INITIATE_BULK_EXPORT` pointcut, but this is no longer the recommended way. `STORAGE_PRE_INITIATE_BULK_EXPORT` pointcut is called before `STORAGE_INITIATE_BULK_EXPORT` and is thus guaranteed to be called before any AuthorizationInterceptors.
[ ](https://hapifhir.io/hapi-fhir/docs/security/balp_interceptor.html)
12.6 Binary Resource Security Interceptor 
* Security 
* [ 12.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/security/introduction.html)
* [ 12.1  Authorization Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html)
* [ 12.2  Consent Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/consent_interceptor.html)
* [ 12.3  Search Narrowing Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/search_narrowing_interceptor.html)
* [ 12.4  CORS ](https://hapifhir.io/hapi-fhir/docs/security/cors.html)
* [ 12.5  Basic Audit Log Pattern (BALP) ](https://hapifhir.io/hapi-fhir/docs/security/balp_interceptor.html)
* [ 12.6  Binary Resource Security Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/binary_security_interceptor.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)