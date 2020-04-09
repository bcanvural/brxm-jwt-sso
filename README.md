## How to work with jwt ?

* Go to https://jwt.io/

* Put in the payload section the following example payload:
```json
{
  "email": "bariscan.vural@bloomreach.com",
  "username": "bariscan.vural",
  "exp": 1893456000
}
```

* Note that the "exp" parameter had to change as this is the expiry parameter. The above parameter says this payload will expire in 2030

* Header section's values:
````json
{
  "alg": "HS256",
  "typ": "JWT"
}
````

* The HS256 algorithm requires a very strong secret(Like around a 60 char string). Put your chosen secret in the secret field in the verify signature section.
Check the box which says: "secret base 64 encoded"
* The spring config uses the jwt.properties file as property source.
There we use JWT_SECRET environemnt variable as secret value. If it does not exist, it defaults to the long 60 char password which is meant for local debugging
Note that this secret value should match the secret value in the JwtUtil class

### Try it out

#### Chrome Plugin
* Set the Bearer token in a chrome plugin called "ModHeader". Add a header called "Authorization" with value: "Bearer <your_token>"
#### Postman
* In Postman, create a request. In the "Authorization" tab chose "Bearer Token" as type and paste in the Token section the token you see on jwt.io on the left hand side.
* Hit http://localhost:8080/cms

