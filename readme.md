# Tool to check the LDAP connection
This java client will connect to the LDAP server and search for provided username.
The output will be logged in output.log file.

## Configurations

config.properties file contains the properties to connect to the LDAP
* ConnectionURL : Connection URL to the LDAP server.
* ConnectionName : Username used to connect to the LDAP and perform operations
* ConnectionPassword : Password for the ConnectionName user.
* UserSearchBase : DN of the context or object under which the user entries are stored in the LDAP
* TrustStoreLocation : Full path to client-truststore.jks
* TrustStorePassword : Password for client trust store
* EnableDebug : Enable all java connection related debug logs.

You can refer this documentation[1] for more information
[1] - https://docs.wso2.com/dislay/IS560/Configuring+a+Read-write+LDAP+User+Store

## How to run


```javac LDAPConnectionCheck.java ```

```java LDAPConnectionCheck <username>```

Here replace the username
