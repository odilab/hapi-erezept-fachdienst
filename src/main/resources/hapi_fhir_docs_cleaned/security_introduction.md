---
source: https://hapifhir.io/hapi-fhir/docs/security/introduction.html
crawled: 2025-08-01T14:06:54.034509
---

# Security Introduction

#  12.0.1REST Server Security: Introduction
[ ](https://hapifhir.io/hapi-fhir/docs/security/introduction.html#rest-server-security-introduction)
Security is a complex topic which goes far beyond the scope of FHIR or HAPI. Every system and architecture operates in a different set of rules, and has different security requirements.
As such, HAPI FHIR does not provide a single one-size-fits-all security layer. Instead, it provides a number of useful tools and building blocks that can be built around as a part of your overall security architecture.
Because HAPI FHIR's REST server is based on the Servlet API, you may use any security mechanism which works in that environment. Some servlet containers may provide security layers you can plug into. The rest of this page does not explore that method, but rather looks at HAPI FHIR hooks that can be used to implement FHIR specific security.
#  12.0.2Authentication vs Authorization
[ ](https://hapifhir.io/hapi-fhir/docs/security/introduction.html#authentication-vs-authorization)
Background reading: [Wikipedia - Authentication](https://en.wikipedia.org/wiki/Authentication)
Server security is divided into three topics:
  * **Authentication (AuthN):** Is verifying that the user is who they say they are. This is typically accomplished by testing a username/password in the request, or by checking a "bearer token" in the request.
  * **Authorization (AuthZ):** Is verifying that the user is allowed to perform the given action. For example, in a FHIR application you might use AuthN to test that the user making a request to the FHIR server is allowed to access the server, but that test might determine that the requesting user is not permitted to perform write operations and therefore block a FHIR `create` operation. This is AuthN and AuthZ in action.
  * **Consent and Audit:** Is verifying that a user has rights to see/modify the specific resources they are requesting, applying any directives to mask data being returned to the client (either partially or completely), and creating a record that the event occurred.


#  12.0.3Authentication Interceptors
[ ](https://hapifhir.io/hapi-fhir/docs/security/introduction.html#authentication-interceptors)
The [Server Interceptor](https://hapifhir.io/hapi-fhir/docs/interceptors/server_interceptors.html) framework can provide an easy way to test for credentials. The following example shows a simple custom interceptor which tests for HTTP Basic Auth.
```
@Interceptor
public class BasicSecurityInterceptor {

   /**
    * This interceptor implements HTTP Basic Auth, which specifies that
    * a username and password are provided in a header called Authorization.
    */
   @Hook(Pointcut.SERVER_INCOMING_REQUEST_POST_PROCESSED)
   public boolean incomingRequestPostProcessed(
         RequestDetails theRequestDetails, HttpServletRequest theRequest, HttpServletResponse theResponse)
         throws AuthenticationException {
      String authHeader = theRequest.getHeader("Authorization");

      // The format of the header must be:
      // Authorization: Basic [base64 of username:password]
      if (authHeader == null || authHeader.startsWith("Basic ") == false) {
         throw new AuthenticationException(Msg.code(642) + "Missing or invalid Authorization header");
      }

      String base64 = authHeader.substring("Basic ".length());
      String base64decoded = new String(Base64.decodeBase64(base64));
      String[] parts = base64decoded.split(":");

      String username = parts[0];
      String password = parts[1];

      /*
       * Here we test for a hardcoded username & password. This is
       * not typically how you would implement this in a production
       * system of course..
       */
      if (!username.equals("someuser") || !password.equals("thepassword")) {
         throw new AuthenticationException(Msg.code(643) + "Invalid username or password");
      }

      // Return true to allow the request to proceed
      return true;
   }
}

```

Copy
##  12.0.3.1HTTP Basic Auth[](https://hapifhir.io/hapi-fhir/docs/security/introduction.html#http-basic-auth)
Note that if you are implementing HTTP Basic Auth, you may want to return a `WWW-Authenticate` header with the response. The following snippet shows how to add such a header with a custom realm:
```
AuthenticationException ex = new AuthenticationException();
ex.addAuthenticateHeaderForRealm("myRealm");
throw ex;

```

Copy
12.0 Introduction 
* Security 
* [ 12.0  Introduction ](https://hapifhir.io/hapi-fhir/docs/security/introduction.html)
* [ 12.1  Authorization Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html)
* [ 12.2  Consent Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/consent_interceptor.html)
* [ 12.3  Search Narrowing Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/search_narrowing_interceptor.html)
* [ 12.4  CORS ](https://hapifhir.io/hapi-fhir/docs/security/cors.html)
* [ 12.5  Basic Audit Log Pattern (BALP) ](https://hapifhir.io/hapi-fhir/docs/security/balp_interceptor.html)
* [ 12.6  Binary Resource Security Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/binary_security_interceptor.html)
[ 12.1 Authorization Interceptor ](https://hapifhir.io/hapi-fhir/docs/security/authorization_interceptor.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)